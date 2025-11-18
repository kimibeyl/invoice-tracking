FROM node:20-3.21 AS build
LABEL authors="kimberleymyburgh"

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --configuration=production

FROM nginx:alpine AS final
COPY --from=build /app/dist/invoice-tracking-invoice/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]