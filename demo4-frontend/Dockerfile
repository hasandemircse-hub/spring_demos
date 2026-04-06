# 1. AŞAMA: Bağımlılıkları yükle ve build al
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
# Build sırasında API URL'ini dışarıdan al
ARG REACT_APP_API_URL=http://localhost:8080
ENV REACT_APP_API_URL=$REACT_APP_API_URL
RUN npm run build

# 2. AŞAMA: Build çıktısını nginx ile sun
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
