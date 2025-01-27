# News Search Micro Service

This service fetches news articles based on a keyword and groups them by specified time intervals.

## Features

- SOLID Principles
- 12-Factor App Principles
- HATEOAS
- Caching
- Docker Support
- CI/CD with GitHub Actions

## Setup

1. Clone the repository.
2. Build the project: `./mvnw clean package`.
3. Run the service: `docker-compose up --build`.

## API Endpoints

- `GET /search?keyword=apple&interval=hours&n=12`

## Docker

Build and run the Docker container:

```bash
docker-compose up --build