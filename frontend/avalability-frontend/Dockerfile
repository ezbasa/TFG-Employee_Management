# Etapa 1: Construcción de la aplicación Angular con Yarn
FROM node:20-alpine AS build

# Establece el directorio de trabajo
WORKDIR /app

# Elimina cualquier rastro de dependencias previas (aunque en una imagen limpia no debería haber)
RUN rm -rf node_modules yarn.lock package-lock.json

# Copia únicamente los archivos de dependencias para aprovechar la caché
COPY package.json yarn.lock ./

# Instala las dependencias en modo producción (usando Yarn)
RUN yarn install --production --frozen-lockfile

# Copia el resto del código fuente
COPY . .

# Ejecuta el build de la aplicación
# Asegúrate de que en angular.json el outputPath esté configurado correctamente, por ejemplo "dist/avalability-frontend"
RUN yarn run build

# Etapa 2: Servir la aplicación con Nginx
FROM nginx:alpine

# Copia los archivos estáticos generados en la etapa de build
COPY --from=build /app/dist/avalability-frontend /usr/share/nginx/html

# Expone el puerto 80 para Nginx
EXPOSE 80

# Comando para iniciar Nginx en modo foreground
CMD ["nginx", "-g", "daemon off;"]
