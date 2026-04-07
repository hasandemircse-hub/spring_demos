#!/bin/bash
# Lokal build → EC2 deploy scripti
# GitHub Actions ve Docker Hub gerektirmez
# Kullanım: ./deploy.sh

set -e  # herhangi bir komut başarısız olursa script durur

# ── Ayarlar ────────────────────────────────────────────────────────────────
KEY="./spring-demos-key.pem"
EC2_USER="ubuntu"
EC2_HOST="${EC2_HOST:-}"          # env'den al, yoksa aşağıda sor
COMPOSE_DIR="~/spring_demos/demo4"

# EC2 host girilmemişse sor
if [ -z "$EC2_HOST" ]; then
    read -p "EC2 IP adresi: " EC2_HOST
fi

SSH="ssh -i $KEY $EC2_USER@$EC2_HOST"
TIMESTAMP=$(date +'%Y-%m-%d-%H%M')

echo ""
echo "============================================"
echo " Deploy başlıyor: $TIMESTAMP"
echo " Hedef: $EC2_USER@$EC2_HOST"
echo "============================================"
echo ""

# ── 1. Local build ─────────────────────────────────────────────────────────
echo "[1/5] Docker image'ları build ediliyor..."

docker build -t spring-demos-app:latest    ./demo4
docker build -t spring-demos-author:latest ./demo4-author-service
docker build -t spring-demos-frontend:latest \
    --build-arg REACT_APP_API_URL="http://$EC2_HOST:8080" \
    ./demo4-frontend

echo "      Build tamamlandı."
echo ""

# ── 2. EC2'da backup klasörü oluştur ───────────────────────────────────────
echo "[2/5] EC2'da yedek klasörü hazırlanıyor..."

$SSH "mkdir -p ~/docker-backups"

echo "      Hazır."
echo ""

# ── 3. Mevcut image'ları EC2'da yedekle ───────────────────────────────────
echo "[3/5] EC2'daki mevcut image'lar yedekleniyor ($TIMESTAMP)..."

# image yoksa docker save hata verir, || true ile geç
$SSH "
    sudo docker save spring-demos-app:latest    2>/dev/null | gzip > ~/docker-backups/app-$TIMESTAMP.tar.gz    || true
    sudo docker save spring-demos-author:latest 2>/dev/null | gzip > ~/docker-backups/author-$TIMESTAMP.tar.gz || true
    sudo docker save spring-demos-frontend:latest 2>/dev/null | gzip > ~/docker-backups/frontend-$TIMESTAMP.tar.gz || true

    # Son 3 yedeği tut
    ls -t ~/docker-backups/app-*.tar.gz      2>/dev/null | tail -n +4 | xargs -r rm
    ls -t ~/docker-backups/author-*.tar.gz   2>/dev/null | tail -n +4 | xargs -r rm
    ls -t ~/docker-backups/frontend-*.tar.gz 2>/dev/null | tail -n +4 | xargs -r rm
"

echo "      Yedek alındı."
echo ""

# ── 4. Image'ları EC2'ya aktar ─────────────────────────────────────────────
# docker save → stdout | gzip → sıkıştır | ssh → EC2'da gunzip | docker load
# Docker Hub yok — image direkt local'den EC2'ya pipe ile gider
echo "[4/5] Image'lar EC2'ya aktarılıyor (bu biraz sürebilir)..."

docker save spring-demos-app:latest    | gzip | $SSH "gunzip | sudo docker load"
echo "      ✓ spring-demos-app aktarıldı"

docker save spring-demos-author:latest | gzip | $SSH "gunzip | sudo docker load"
echo "      ✓ spring-demos-author aktarıldı"

docker save spring-demos-frontend:latest | gzip | $SSH "gunzip | sudo docker load"
echo "      ✓ spring-demos-frontend aktarıldı"

echo ""

# ── 5. EC2'da container'ları yeniden başlat ────────────────────────────────
echo "[5/5] EC2'da container'lar yeniden başlatılıyor..."

$SSH "
    cd $COMPOSE_DIR
    # docker-compose.yml image olarak local tag'leri kullansın
    DOCKERHUB_USERNAME='' \
    CORS_ALLOWED_ORIGINS=http://$EC2_HOST \
    sudo -E docker compose up -d

    # Tag'siz eski image'ları temizle
    sudo docker image prune -f
"

echo ""
echo "============================================"
echo " Deploy tamamlandı!"
echo " Frontend : http://$EC2_HOST"
echo " Backend  : http://$EC2_HOST:8080"
echo "============================================"
