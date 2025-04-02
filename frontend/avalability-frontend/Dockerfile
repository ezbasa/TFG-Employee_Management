# Etapa 1: Construcción de la aplicación Angular
FROM node:20-alpine AS build

WORKDIR /app

# Copia los archivos de dependencias
COPY package.json ./
#package-lock.json* ./

# Actualiza npm a la versión más reciente para evitar el bug
RUN npm install -g npm@latest

# Limpia cualquier rastro de dependencias (normalmente no es necesario en una imagen limpia)
RUN rm -rf node_modules

# Instala todas las dependencias (las opcionales se instalarán según lo definido en package.json)
RUN npm install --omit=dev

# Copia el resto del código fuente
COPY . .

# Ejecuta el build de la aplicación (asegúrate de que angular.json tenga el outputPath correcto, ej. "dist/avalability-frontend")
RUN npm run build

# Etapa 2: Servir la aplicación con Nginx
FROM nginx:alpine

# Copia los archivos construidos desde la etapa anterior al directorio de Nginx
COPY --from=build /app/dist/avalability-frontend /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
