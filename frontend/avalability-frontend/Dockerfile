# Usa imágenes no Alpine para evitar problemas de compatibilidad
FROM node:18 AS build

WORKDIR /app
COPY package*.json ./
RUN npm ci --include=dev
COPY . .
RUN npm run build --prod

FROM nginx:stable
COPY --from=build /app/dist/avalability-frontend /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
