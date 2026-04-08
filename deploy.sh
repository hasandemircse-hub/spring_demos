#!/bin/bash
# Lokal build → EC2 deploy scripti
# Kullanım:
#   ./deploy.sh              → tüm servisler
#   ./deploy.sh app          → sadece backend
#   ./deploy.sh author       → sadece author service
#   ./deploy.sh frontend     → sadece frontend
#   ./deploy.sh app frontend → birden fazla

set -e

# ── Ayarlar ────────────────────────────────────────────────────────────────
KEY="./spring-demos-key.pem"
EC2_USER="ubuntu"
EC2_HOST="${EC2_HOST:-}"
COMPOSE_DIR="~/spring_demos/demo4"

if [ -z "$EC2_HOST" ]; then
    read -p "EC2 IP adresi: " EC2_HOST
fi

SSH="ssh -i $KEY $EC2_USER@$EC2_HOST"
TIMESTAMP=$(date +'%Y-%m-%d-%H%M')

# ── Hangi servisler deploy edilecek ────────────────────────────────────────
# Argüman verilmezse hepsi
if [ $# -eq 0 ]; then
    SERVICES="app author frontend"
else
    SERVICES="$@"
fi

echo ""
echo "============================================"
echo " Deploy başlıyor: $TIMESTAMP"
echo " Servisler: $SERVICES"
echo " Hedef: $EC2_USER@$EC2_HOST"
echo "============================================"
echo ""

# ── Yardımcı fonksiyonlar ───────────────────────────────────────────────────
build_service() {
    local SERVICE=$1
    case $SERVICE in
        app)
            echo "  → spring-demos-app build ediliyor..."
            docker build --platform linux/amd64 -t spring-demos-app:latest ./demo4
            ;;
        author)
            echo "  → spring-demos-author build ediliyor..."
            docker build --platform linux/amd64 -t spring-demos-author:latest ./demo4-author-service
            ;;
        frontend)
            echo "  → spring-demos-frontend build ediliyor..."
            docker build --platform linux/amd64 -t spring-demos-frontend:latest \
                --build-arg REACT_APP_API_URL="http://$EC2_HOST:8080" \
                ./demo4-frontend
            ;;
        *)
            echo "HATA: Bilinmeyen servis '$SERVICE'. Geçerli değerler: app, author, frontend"
            exit 1
            ;;
    esac
}

transfer_service() {
    local SERVICE=$1
    local IMAGE="spring-demos-$SERVICE"
    echo "  → $IMAGE aktarılıyor..."
    docker save $IMAGE:latest | gzip | $SSH "gunzip | sudo docker load"
    echo "  ✓ $IMAGE aktarıldı"
}

backup_service() {
    local SERVICE=$1
    local IMAGE="spring-demos-$SERVICE"
    $SSH bash << EOF
BACKUP_DIR=~/docker-backups
OUT=\$BACKUP_DIR/${SERVICE}-${TIMESTAMP}.tar.gz
if sudo docker image inspect ${IMAGE}:latest > /dev/null 2>&1; then
    sudo docker save ${IMAGE}:latest | gzip -1 > \$OUT
    echo "  ✓ ${SERVICE} yedeklendi: \$(ls -lh \$OUT | awk '{print \$5}')"
elif sudo docker image inspect hasandemircse/${IMAGE}:latest > /dev/null 2>&1; then
    sudo docker save hasandemircse/${IMAGE}:latest | gzip -1 > \$OUT
    echo "  ✓ ${SERVICE} yedeklendi: \$(ls -lh \$OUT | awk '{print \$5}')"
else
    echo "  ! ${SERVICE} image bulunamadi, yedek atlaniyor"
fi
ls -t \$BACKUP_DIR/${SERVICE}-*.tar.gz 2>/dev/null | tail -n +3 | xargs -r rm
EOF
}

restart_services() {
    local SERVICES=$1
    $SSH bash << EOF
cd $COMPOSE_DIR
CORS_ALLOWED_ORIGINS=http://${EC2_HOST} sudo -E docker compose up -d --no-deps $SERVICES
sudo docker image prune -f
EOF
}

# ── 1. Ön kontrol ─────────────────────────────────────────────────────────
# Frontend değişiyorsa build öncesi syntax/lint kontrolü yap
if echo "$SERVICES" | grep -qw "frontend"; then
    echo "[0/4] Frontend lint & build kontrolü yapılıyor..."
    cd demo4-frontend && npm run build > /dev/null 2>&1 && cd ..
    if [ $? -ne 0 ]; then
        echo "HATA: Frontend build başarısız — deploy iptal edildi"
        echo "Detay için: cd demo4-frontend && npm run build"
        exit 1
    fi
    echo "      ✓ Frontend build kontrolü geçti."
    echo ""
fi

# ── 2. Build ───────────────────────────────────────────────────────────────
echo "[1/4] Build ediliyor..."
for SERVICE in $SERVICES; do
    build_service $SERVICE
done
echo "      Build tamamlandı."
echo ""

# ── 2. Backup ──────────────────────────────────────────────────────────────
echo "[2/4] EC2'daki mevcut image'lar yedekleniyor..."
$SSH "mkdir -p ~/docker-backups"
for SERVICE in $SERVICES; do
    backup_service $SERVICE
done
echo ""

# ── 3. Transfer ────────────────────────────────────────────────────────────
echo "[3/4] Image'lar EC2'ya aktarılıyor..."
for SERVICE in $SERVICES; do
    transfer_service $SERVICE
done
echo ""

# ── 4. Restart ─────────────────────────────────────────────────────────────
echo "[4/4] Container'lar yeniden başlatılıyor..."
restart_services "$SERVICES"

echo ""
echo "============================================"
echo " Deploy tamamlandı!"
echo " Servisler: $SERVICES"
echo " Frontend : http://$EC2_HOST"
echo " Backend  : http://$EC2_HOST:8080"
echo "============================================"
