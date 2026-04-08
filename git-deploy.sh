#!/bin/bash
# Git push ile GitHub Actions CI/CD'yi tetikler
# Kullanım:
#   ./git-deploy.sh "commit mesajı"              → tüm değişiklikler
#   ./git-deploy.sh "commit mesajı" frontend     → sadece frontend dosyaları

set -e

MESSAGE="${1:-}"
SERVICE="${2:-all}"

if [ -z "$MESSAGE" ]; then
    echo "HATA: Commit mesajı gerekli"
    echo "Kullanım: ./git-deploy.sh \"commit mesajı\" [app|author|frontend|all]"
    exit 1
fi

# Geçerli servis mi kontrol et
case $SERVICE in
    all|app|author|frontend) ;;
    *)
        echo "HATA: Geçersiz servis '$SERVICE'"
        echo "Geçerli değerler: all, app, author, frontend"
        exit 1
        ;;
esac

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

# ── Değişiklik var mı kontrol et ──────────────────────────────────────────
if git diff --quiet && git diff --staged --quiet; then
    UNTRACKED=$(git ls-files --others --exclude-standard | grep -v "spring-demos-key.pem" | head -1)
    if [ -z "$UNTRACKED" ]; then
        echo "HATA: Deploy edilecek değişiklik yok."
        echo "Dosyaları düzenleyip tekrar dene."
        exit 1
    fi
fi

# ── Hangi dosyaları stage'e al ────────────────────────────────────────────
case $SERVICE in
    app)
        git add demo4/
        ;;
    author)
        git add demo4-author-service/
        ;;
    frontend)
        git add demo4-frontend/
        ;;
    all)
        # key.pem hariç her şeyi ekle
        git add .
        git restore --staged spring-demos-key.pem 2>/dev/null || true
        ;;
esac

# Stage'de bir şey var mı?
if git diff --staged --quiet; then
    echo "HATA: Seçilen servis için stage'de değişiklik yok."
    exit 1
fi

echo ""
echo "============================================"
echo " Git Deploy"
echo " Servis : $SERVICE"
echo " Mesaj  : $MESSAGE"
echo "============================================"
echo ""

echo "Stage'deki değişiklikler:"
git diff --staged --stat
echo ""

git commit -m "$MESSAGE"
git push origin main

echo ""
echo "✓ Push tamamlandı. GitHub Actions başlatılıyor..."
echo ""
echo "Pipeline durumu:"
echo "  https://github.com/hasandemircse-hub/spring_demos/actions"
echo ""
echo "Servis canlıya geçince:"
echo "  Frontend : http://63.178.241.158"
echo "  Backend  : http://63.178.241.158:8080"
