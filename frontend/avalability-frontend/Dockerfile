# Etapa 1: Construcción de la aplicación Angular
FROM node:20 AS build

# Establece el PATH globalmente (normalmente ya viene, pero lo definimos para asegurarnos)
ENV PATH=/usr/local/bin:$PATH

WORKDIR /app

# Copia los archivos de dependencias
COPY package.json package-lock.json* ./

# Actualiza npm a la versión más reciente (opcional, si lo necesitas)
RUN npm install -g npm@latest

# Elimina node_modules si existiera (normalmente no es necesario en una imagen limpia)
RUN rm -rf node_modules

# Instala todas las dependencias (incluyendo las opcionales definidas en package.json)
RUN npm install

# Instala Angular CLI globalmente
RUN npm install -g @angular/cli

# Verifica que node y npm estén disponibles
RUN node -v && npm -v && ng version

# Copia el resto del código fuente
COPY . .

# Ejecuta el build de la aplicación en modo producción (ajusta si es necesario)
RUN npm run build -- --configuration production

# Etapa 2: Servir la aplicación con Nginx
FROM nginx:alpine

# Copia los archivos construidos desde la etapa de build al directorio de Nginx
COPY --from=build /app/dist/avalability-frontend /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
