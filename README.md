## **Prerequisites**

Before starting, ensure you have the following installed on your system:

1. **Docker**: Version 20.10 or higher.
2. **Docker Compose**: Version 1.29 or higher.
---

## **1. Clone the Repository**

Clone the repository to your local machine:

```bash
git clone https://github.com/mjanisk/measurement-app.git

```
## **2. Configure Environment Variables**
Create the .env file (in the project root directory) and fill it

## **3. Deploy and Run Using Docker**
Build and run the application:
```bash
docker compose up --build
```

The application will be accessible at http://localhost:4200.

Swagger UI will be available at http://localhost:8080/swagger-ui.html.
