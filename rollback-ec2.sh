#!/bin/bash
# EC2 TERMİNALİNDE direkt çalıştırılır — SSH gerekmez
# EC2'ya kopyala: scp -i key.pem rollback-ec2.sh ubuntu@IP:~/
# Kullanım:
#   ./rollback-ec2.sh                  → backup listeler, timestamp sorar
#   ./rollback-ec2.sh 2026-04-07-0950  → direkt timestamp ile

set -e

BACKUP_DIR=~/docker-backups
COMPOSE_DIR=~/spring_demos/demo4

# ── Mevcut backup'ları listele ─────────────────────────────────────────────
echo ""
echo "Mevcut backup'lar:"
echo "------------------"
ls -lh $BACKUP_DIR/app-*.tar.gz 2>/dev/null \
    | awk '{print $5, $9}' \
    | sed 's|.*/app-||;s|\.tar\.gz||' \
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

# ── Dosyaları kontrol et ───────────────────────────────────────────────────
echo "[1/3] Backup dosyaları kontrol ediliyor..."

for service in app author frontend; do
    FILE=$BACKUP_DIR/${service}-${TIMESTAMP}.tar.gz
    if [ ! -f $FILE ]; then
        echo "HATA: $FILE bulunamadi"
        exit 1
    fi
    SIZE=$(stat -c%s $FILE)
    if [ $SIZE -lt 1000 ]; then
        echo "HATA: $FILE bozuk veya bos ($SIZE byte)"
        exit 1
    fi
    echo "  ok $service: $(ls -lh $FILE | awk '{print $5}')"
done

echo ""

# ── Image'ları yükle ───────────────────────────────────────────────────────
echo "[2/3] Image'lar yukleniyor..."

gunzip -c $BACKUP_DIR/app-${TIMESTAMP}.tar.gz      | sudo docker load
gunzip -c $BACKUP_DIR/author-${TIMESTAMP}.tar.gz   | sudo docker load
gunzip -c $BACKUP_DIR/frontend-${TIMESTAMP}.tar.gz | sudo docker load

echo ""

# ── Servisleri yeniden başlat ──────────────────────────────────────────────
echo "[3/3] Servisler yeniden baslatiliyor..."

EC2_IP=$(curl -s http://checkip.amazonaws.com)
cd $COMPOSE_DIR
CORS_ALLOWED_ORIGINS=http://$EC2_IP sudo -E docker compose up -d
sudo docker image prune -f

echo ""
echo "============================================"
echo " Rollback tamamlandi: $TIMESTAMP"
echo " Frontend : http://$EC2_IP"
echo " Backend  : http://$EC2_IP:8080"
echo "============================================"
