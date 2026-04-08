#!/bin/bash
# Tüm servisleri local'de başlatır
# Kullanım: ./local-start.sh
# Durdurmak için: Ctrl+C

set -e

ROOT=$(pwd)

echo ""
echo "============================================"
echo " Local Başlatılıyor"
echo " Author Service : http://localhost:8081"
echo " Backend        : http://localhost:8080"
echo " Frontend       : http://localhost:3000"
echo "============================================"
echo ""

# Temizlik — Ctrl+C'de tüm arka plan processleri öldür
cleanup() {
    echo ""
    echo "Durduruluyor..."
    kill $AUTHOR_PID $APP_PID $FRONTEND_PID 2>/dev/null
    exit 0
}
trap cleanup SIGINT SIGTERM

# ── Author Service ─────────────────────────────────────────────────────────
echo "[1/3] Author Service başlatılıyor (port 8081)..."
cd "$ROOT/demo4-author-service"
mvn spring-boot:run -q > /tmp/author-service.log 2>&1 &
AUTHOR_PID=$!

# ── Backend ────────────────────────────────────────────────────────────────
echo "[2/3] Backend başlatılıyor (port 8080)..."
cd "$ROOT/demo4"
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev -q > /tmp/book-service.log 2>&1 &
APP_PID=$!

# ── Backend hazır olana kadar bekle ───────────────────────────────────────
echo "      Backend'in hazır olması bekleniyor..."
until curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; do
    sleep 2
done
echo "      ✓ Backend hazır."
echo ""

# ── Frontend ───────────────────────────────────────────────────────────────
echo "[3/3] Frontend başlatılıyor (port 3000)..."
cd "$ROOT/demo4-frontend"
npm start > /tmp/frontend.log 2>&1 &
FRONTEND_PID=$!

echo ""
echo "============================================"
echo " Servisler çalışıyor!"
echo " Frontend : http://localhost:3000"
echo " Backend  : http://localhost:8080"
echo " Author   : http://localhost:8081"
echo ""
echo " Loglar:"
echo "   Author  : tail -f /tmp/author-service.log"
echo "   Backend : tail -f /tmp/book-service.log"
echo "   Frontend: tail -f /tmp/frontend.log"
echo ""
echo " Durdurmak için: Ctrl+C"
echo "============================================"
echo ""

# Processlerin bitmesini bekle
wait $AUTHOR_PID $APP_PID $FRONTEND_PID
