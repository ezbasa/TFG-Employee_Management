# Etapa 1: Construcción de la aplicación Angular
FROM node:20-alpine AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de dependencias primero (para aprovechar la caché de Docker)
COPY package*.json ./

# Copia el resto del código fuente
COPY . .

# Instala las dependencias; forzamos la instalación de @rollup/rollup-linux-x64-gnu
RUN npm install && npm install @rollup/rollup-linux-x64-gnu

# Ejecuta el build de la aplicación (asegúrate de que en angular.json el outputPath esté configurado correctamente,
# por ejemplo "dist/avalability-frontend")
RUN npm run build

# Etapa 2: Servir la aplicación con Nginx
FROM nginx:alpine

# Copia los archivos construidos desde la etapa anterior al directorio que Nginx utiliza para servir contenido
COPY --from=build /app/dist/avalability-frontend /usr/share/nginx/html

# Exponer el puerto 80 (Nginx escucha en este puerto)
EXPOSE 80

# Comando por defecto para iniciar Nginx en modo foreground
CMD ["nginx", "-g", "daemon off;"]
