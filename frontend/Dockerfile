# Use Node.js 20 for building the Angular app
FROM node:20 AS build

WORKDIR /app

# Copy package.json and install dependencies
COPY package.json package-lock.json ./
RUN npm install

# Copy the rest of the application and build it
COPY . .
RUN npm run build

# Use Nginx to serve the Angular app
FROM nginx:alpine
COPY --from=build /app/dist/frontend /usr/share/nginx/html

# Expose the port your application runs on

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
