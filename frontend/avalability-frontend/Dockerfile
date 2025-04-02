# Etapa 1: Construcción de la aplicación Angular
FROM node:20 AS build

WORKDIR /app

# Copia los archivos de dependencias
COPY package.json ./

# Instala todas las dependencias (incluyendo opcionales según package.json)
RUN npm install

# Instala Angular CLI globalmente
RUN npm install -g @angular/cli && export PATH=$PATH:/usr/local/bin

# Copia el resto del código fuente
COPY . .

# Ejecuta el build en modo producción
RUN npm run build -- --configuration production

# Etapa 2: Servir la aplicación con Nginx
FROM nginx:alpine

# Copia los archivos construidos desde la etapa anterior al directorio de Nginx
COPY --from=build /app/dist/avalability-frontend /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
