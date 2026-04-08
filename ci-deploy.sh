#!/bin/bash
# GitHub Actions üzerinden deploy tetikler
# gh CLI gerektirir: brew install gh && gh auth login
#
# Kullanım:
#   ./ci-deploy.sh              → hepsi
#   ./ci-deploy.sh app          → sadece backend
#   ./ci-deploy.sh author       → sadece author service
#   ./ci-deploy.sh frontend     → sadece frontend

set -e

SERVICE="${1:-all}"

# Geçerli servis mi kontrol et
case $SERVICE in
    all|app|author|frontend) ;;
    *)
        echo "HATA: Geçersiz servis '$SERVICE'"
        echo "Geçerli değerler: all, app, author, frontend"
        exit 1
        ;;
esac

# gh CLI kurulu mu?
if ! command -v gh &> /dev/null; then
    echo "HATA: gh CLI bulunamadı."
    echo "Kur: brew install gh && gh auth login"
    exit 1
fi

# ── Frontend lint & build kontrolü ────────────────────────────────────────
if [ "$SERVICE" = "frontend" ] || [ "$SERVICE" = "all" ]; then
    echo "Frontend lint & build kontrolü yapılıyor..."
    cd demo4-frontend && npm run build > /dev/null 2>&1 && cd ..
    if [ $? -ne 0 ]; then
        echo "HATA: Frontend build başarısız — deploy iptal edildi"
        echo "Detay için: cd demo4-frontend && npm run build"
        exit 1
    fi
    echo "✓ Frontend build kontrolü geçti."
    echo ""
fi

echo ""
echo "============================================"
echo " GitHub Actions tetikleniyor"
echo " Servis: $SERVICE"
echo "============================================"
echo ""

gh workflow run ci.yml --field service=$SERVICE

echo ""
echo "Pipeline başladı. Durumu takip et:"
echo "https://github.com/hasandemircse-hub/spring_demos/actions"
echo ""

# Birkaç saniye bekle, sonra son workflow'un durumunu göster
sleep 3
gh run list --workflow=ci.yml --limit=1
