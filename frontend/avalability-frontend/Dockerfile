# Etapa 1: Construcción
FROM node:18-alpine AS build

WORKDIR /app

# Copia solo los archivos necesarios para instalar dependencias
COPY package.json package-lock.json ./

# Instala dependencias
RUN npm ci

# Copia el resto de los archivos
COPY . .

# Construye la aplicación
RUN npm run build --prod

# Etapa 2: Servir con Nginx
FROM nginx:alpine

# Copia la configuración de nginx (opcional)
# COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copia los archivos construidos
COPY --from=build /app/dist/avalability-frontend /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
