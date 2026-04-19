# Dogs Web API

A RESTful API for managing dog breeds and sub-breeds, built with **Java 21 + Spring Boot 3 + PostgreSQL**.

The API is pre-seeded with 70+ dog breeds from the supplied dataset and exposes a full **CRUD** interface accessible via an interactive **Swagger UI**.

## Live Demo

| Resource | URL |
|---|---|
| Swagger UI | `https://dogs-web-api.onrender.com/swagger-ui.html` |
| API Docs (JSON) | `https://dogs-web-api.onrender.com/api-docs` |
| Health Check | `https://dogs-web-api.onrender.com/actuator/health` |

> **Note:** Update these links with your actual Render deployment URL after deploying.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3 |
| Database | PostgreSQL (prod) / H2 in-memory (dev) |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Build | Gradle |
| Container | Docker (multi-stage build) |
| Hosting | Render (free tier) |

---

## API Endpoints

### Breeds

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/breeds` | List all breeds with sub-breeds |
| `GET` | `/api/v1/breeds/{breed}` | Get a specific breed |
| `POST` | `/api/v1/breeds` | Create a new breed |
| `PUT` | `/api/v1/breeds/{breed}` | Rename a breed |
| `DELETE` | `/api/v1/breeds/{breed}` | Delete a breed (cascades sub-breeds) |

### Sub-Breeds

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/breeds/{breed}/sub-breeds` | List sub-breeds for a breed |
| `POST` | `/api/v1/breeds/{breed}/sub-breeds` | Add a sub-breed |
| `DELETE` | `/api/v1/breeds/{breed}/sub-breeds/{subBreed}` | Remove a sub-breed |

All breed and sub-breed names are **case-insensitive** and stored in **lowercase**.

### Response Format

All endpoints return a consistent JSON envelope:

```json
{
  "status": "success",
  "message": "Breed retrieved successfully",
  "data": {
    "id": 1,
    "name": "bulldog",
    "subBreeds": ["boston", "french"],
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```

---

## Running Locally

### Prerequisites

- Java 21+
- (Optional) Docker

### With Gradle (H2 in-memory DB)

```bash
git clone https://github.com/shubhkesh/dogs-web-api.git
cd dogs-web-api
./gradlew bootRun
```

Open **http://localhost:8080/swagger-ui.html** in your browser.

The database is seeded automatically on first boot. The H2 console is available at **http://localhost:8080/h2-console** (JDBC URL: `jdbc:h2:mem:dogsdb`, username: `sa`, no password).

### With Docker (H2 in-memory DB)

```bash
docker build -t dogs-web-api .
docker run -p 8080:8080 dogs-web-api
```

### With Docker + PostgreSQL (production-like)

```bash
docker build -t dogs-web-api .
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/dogsdb \
  -e SPRING_DATASOURCE_USERNAME=your_user \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  dogs-web-api
```

---

## Running Tests

```bash
./gradlew test
```

Test reports are generated at `build/reports/tests/test/index.html`.

---

## Deployment on Render

This project includes a `render.yaml` Blueprint for one-click deployment.

### Steps

1. Fork / push this repository to GitHub.
2. Log in to [Render](https://render.com) and click **New → Blueprint**.
3. Connect your GitHub repository — Render will detect `render.yaml` automatically.
4. Click **Apply** — Render provisions the PostgreSQL database and deploys the web service.
5. On first boot, `DataSeeder` automatically populates the database with the full dog breed dataset.

### Environment Variables (set automatically via render.yaml)

| Variable | Source |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `SPRING_DATASOURCE_URL` | Render PostgreSQL (JDBC URL) |
| `SPRING_DATASOURCE_USERNAME` | Render PostgreSQL user |
| `SPRING_DATASOURCE_PASSWORD` | Render PostgreSQL password |

---

## Project Structure

```
src/main/java/com/dogs/api/
├── configuration/
│   ├── DataSeeder.java         # Seeds dogs.json on first boot
│   └── OpenApiConfig.java      # Swagger / OpenAPI configuration
├── constants/
│   └── ApiConstants.java       # API path constants
├── controllers/
│   └── BreedController.java    # REST endpoints
├── dto/
│   ├── request/                # CreateBreedRequest, UpdateBreedRequest, AddSubBreedRequest
│   └── response/               # ApiResponse<T>, BreedResponse
├── exception/
│   ├── BreedNotFoundException.java
│   ├── BreedAlreadyExistsException.java
│   ├── SubBreedAlreadyExistsException.java
│   └── GlobalExceptionHandler.java
├── mapper/
│   └── BreedMapper.java        # Entity → DTO conversion
├── model/
│   ├── Breed.java              # breeds table
│   └── SubBreed.java           # sub_breeds table
├── repository/
│   └── BreedRepository.java
└── service/
    ├── BreedService.java       # Interface (DIP)
    └── impl/
        └── BreedServiceImpl.java
```

### SOLID Principles Applied

| Principle | Application |
|---|---|
| **S** — Single Responsibility | Controller handles HTTP only; Service handles business logic; Repository handles data |
| **O** — Open/Closed | `BreedService` interface allows new implementations without modifying the controller |
| **L** — Liskov Substitution | `BreedServiceImpl` is fully substitutable for `BreedService` |
| **I** — Interface Segregation | `BreedRepository` and `SubBreedRepository` are focused, independent interfaces |
| **D** — Dependency Inversion | `BreedController` depends on `BreedService` interface, not the concrete implementation |

---

## Design Decisions

- **Two-table model** (`breeds` + `sub_breeds`) — proper relational design with a foreign key, enabling independent CRUD on sub-breeds.
- **DataSeeder** — idempotent; only seeds if the `breeds` table is empty. Safe to redeploy.
- **Case-insensitive + normalised names** — all breed names are trimmed and lowercased before storage to prevent duplicates.
- **Consistent API envelope** — every response (success or error) uses `{ status, message, data }` for predictable client-side handling.
- **Swagger UI as the interface** — fully interactive in-browser; no separate frontend required.
