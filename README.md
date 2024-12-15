# MTask

This project is a demo application for managing categories and products using Spring Boot, PostgreSQL, and MinIO. It demonstrates CRUD operations, integration testing, and the use of Docker to set up development and testing environments.

## Requirements

To run the project, you need:
- **Java 17**
- **Maven 3.8+**
- **Docker** and **Docker Compose**

## Features

1. **Browse Products and Categories**
    - Paginated list of products and categories with their logos.

2. **Unique Product Names**
    - Display a list of unique product names.

3. **Filter Products by Category**
    - Retrieve all products based on category names.

4. **Search Functionality**
    - Search for products by name.

5. **Edit Product**
    - Modify product names and logos.
    - Allowed only for users with the `EDITOR` role.

6. **CRUD Operations**
    - Add, edit, and delete products and categories.
    - Retrieve categories (including their products) and products by ID.

7. **Security**
    - User roles:
        - `USER`: Basic browsing permissions.
        - `EDITOR`: Permissions to edit products.

8. **Documentation**
    - Comprehensive JavaDocs for all methods and classes.
    - This README serves as an entry point to understand the application.

9. **Testing**
    - Unit and integration tests for all application layers.

10. **Docker Compose**
    - A `docker-compose.yml` file is provided for external services like PostgreSQL and MinIO.

---

## Setup

### Step 1: Clone the Repository

### Step 2: Configure the Application

1. Open `src/main/resources/application.yaml` and ensure the basic configurations are correct.
2. Add `application-dev.yaml` in `src/main/resources` with the following contents:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/m_task
    username: mtask_user
    password: mtask_password
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  minio:
    endpoint: http://localhost:9100
    access-key: TESTUSER
    secret-key: TESTPASSWORD
    default-bucket: default
```
### Step 3: Start External Services with Docker Compose

Run the following command from the root of the project:

```bash
docker-compose -f docker/docker-compose.yml up -d
```
This will start up:

- **PostgreSQL**:
    - **Port**: `5432`
    - **Database Name**: `m_task`
    - **Username**: `mtask_user`
    - **Password**: `mtask_password`

- **MinIO**:
    - **API Endpoint**: `http://localhost:9100`
    - **Console Endpoint**: `http://localhost:9101`
    - **Access Key**: `TESTUSER`
    - **Secret Key**: `TESTPASSWORD`

Both services will persist their data using the volumes defined in the `docker-compose.yml` file.

---

### Post-Setup Validation

After starting the services, validate the following:

1. **PostgreSQL**:
    - You can connect to the database using any SQL client or CLI tool to verify the connection:
      ```bash
      psql -h localhost -p 5432 -U mtask_user -d m-task
      ```
      Enter the password `mtask_password` when prompted.

2. **MinIO**:
    - Access the MinIO web console at `http://localhost:9101` using the credentials:
        - Username: `TESTUSER`
        - Password: `TESTPASSWORD`.
    - Ensure that the `default` bucket exists in the MinIO configuration.

---

### Usage

Once the application is running, you can use the following endpoints:

1. **Browse Products and Categories**:
    - Endpoint: `GET /api/categories`
    - Paginated results for all categories with their associated products.

2. **Retrieve Unique Product Names**:
    - Endpoint: `GET /api/products/unique-names`
    - Displays a list of all unique product names.

3. **Get Products by Category Name**:
    - Endpoint: `GET /api/products?categoryName=<category-name>`

4. **Search Products by Name**:
    - Endpoint: `GET /api/products?name=<product-name>`

5. **Edit Product** (Requires `EDITOR` role):
    - Endpoint: `PUT /api/products/{id}`
    - Allows modifying product details.

6. **Add Product or Category**:
    - Endpoint: `POST /api/products` or `POST /api/categories`
    - Add new products or categories, with optional logos.

7. **Delete Product or Category**:
    - Endpoint: `DELETE /api/products/{id}` or `DELETE /api/categories/{id}`

---

### Authentication

The application uses basic authentication for all endpoints:

- **User Roles**:
    - `USER`: Read-only access for browsing products and categories.
    - `EDITOR`: Read and write access, including product edits.

Use the following credentials for testing:
- Username: `regularUser`
- Password: `password`
- Role: `USER`

- Username: `editorUser`
- Password: `password`
- Role: `EDITOR`

---
