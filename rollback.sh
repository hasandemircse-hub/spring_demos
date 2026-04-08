#!/bin/bash
# LOCAL'DEN çalıştırılır — SSH ile EC2'ya bağlanır
# Kullanım:
#   ./rollback.sh                        → backup listeler, timestamp sorar
#   ./rollback.sh 2026-04-07-0950        → direkt timestamp ile
#   EC2_HOST=1.2.3.4 ./rollback.sh      → IP sormadan

set -e

KEY="./spring-demos-key.pem"
EC2_USER="ubuntu"
EC2_HOST="${EC2_HOST:-}"
BACKUP_DIR="~/docker-backups"
COMPOSE_DIR="~/spring_demos/demo4"

if [ -z "$EC2_HOST" ]; then
    read -p "EC2 IP adresi: " EC2_HOST
fi

SSH="ssh -i $KEY $EC2_USER@$EC2_HOST"

# ── Mevcut backup'ları listele ─────────────────────────────────────────────
echo ""
echo "Mevcut backup'lar:"
echo "------------------"
$SSH "ls -lh $BACKUP_DIR/app-*.tar.gz 2>/dev/null | awk '{print \$5, \$9}' | sed 's|.*/app-||;s|\.tar\.gz||'" \
    | nl -w2 -s') '
echo ""

TIMESTAMP="${1:-}"
if [ -z "$TIMESTAMP" ]; then
    read -p "Hangi timestamp'e rollback yapmak istiyorsun? (örn: 2026-04-07-0950): " TIMESTAMP
fi

echo ""
echo "============================================"
echo " Rollback: $TIMESTAMP"
echo "============================================"
echo ""

echo "[1/3] Backup dosyaları kontrol ediliyor..."
$SSH bash << EOF
for service in app author frontend; do
    FILE=$BACKUP_DIR/\${service}-${TIMESTAMP}.tar.gz
    if [ ! -f \$FILE ]; then
        echo "HATA: \$FILE bulunamadi"
        exit 1
    fi
    SIZE=\$(stat -c%s \$FILE)
    if [ \$SIZE -lt 1000 ]; then
        echo "HATA: \$FILE bozuk veya bos (\$SIZE byte)"
        exit 1
    fi
    echo "  ok \$service: \$(ls -lh \$FILE | awk '{print \$5}')"
done
EOF

echo ""
echo "[2/3] Image'lar yukleniyor..."
$SSH bash << EOF
gunzip -c $BACKUP_DIR/app-${TIMESTAMP}.tar.gz      | sudo docker load
gunzip -c $BACKUP_DIR/author-${TIMESTAMP}.tar.gz   | sudo docker load
gunzip -c $BACKUP_DIR/frontend-${TIMESTAMP}.tar.gz | sudo docker load
EOF

echo ""
echo "[3/3] Servisler yeniden baslatiliyor..."
$SSH bash << EOF
cd $COMPOSE_DIR
CORS_ALLOWED_ORIGINS=http://${EC2_HOST} sudo -E docker compose up -d
sudo docker image prune -f
EOF

echo ""
echo "============================================"
echo " Rollback tamamlandi: $TIMESTAMP"
echo " Frontend : http://$EC2_HOST"
echo " Backend  : http://$EC2_HOST:8080"
echo "============================================"
