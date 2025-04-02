# Etapa 1: Construcción
FROM node:18-alpine AS build

WORKDIR /app

# Copia solo archivos de dependencias
COPY package.json package-lock.json ./

# Instala dependencias (incluyendo devDependencies)
RUN npm ci --include=dev

# Copia todo el código fuente
COPY . .

# Construye la aplicación
RUN npm run build --prod

# Etapa 2: Servir con Nginx
FROM nginx:alpine

# Copia los archivos construidos
COPY --from=build /app/dist/avalability-frontend /usr/share/nginx/html

# Expone el puerto 80
EXPOSE 80

# Inicia Nginx
CMD ["nginx", "-g", "daemon off;"]
