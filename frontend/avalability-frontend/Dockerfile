# Etapa 1: Construcción de la aplicación Angular
FROM node:20-alpine AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de dependencias
COPY package.json package-lock.json* ./

# Instala todas las dependencias
RUN npm install

# Copia el resto del código fuente
COPY . .

# Ejecuta el build de la aplicación en modo producción
# Se utiliza la opción de configuración "production" (ajusta según tu angular.json)
RUN npm run build -- --configuration production

# Etapa 2: Servir la aplicación con Nginx
FROM nginx:alpine

# Copia los archivos construidos de la etapa anterior al directorio de Nginx
COPY --from=build /app/dist/avalability-frontend /usr/share/nginx/html

# Expone el puerto 80
EXPOSE 80

# Inicia Nginx en modo foreground para que el contenedor no se detenga
CMD ["nginx", "-g", "daemon off;"]
