# Austin Local Discovery

A local discovery app for Austin — search nearby events, spots, and deals,
ranked by distance, rating, and your personal taste.

## Stack 

- **Backend:** Java 17, Spring Boot 3, Spring Data JPA
- **Database:** H2 (in-memory, zero setup)
- **Caching:** Spring's in-memory `ConcurrentMapCache`
- **Geo search:** Haversine formula computed in-process 
- **Android client:** Kotlin + Jetpack Compose + Retrofit/Moshi/OkHttp 

## Running the backend

Requires JDK 17+ and Maven (or use `./mvnw` if you add the wrapper).

```bash
cd backend
mvn spring-boot:run
```

The API starts on `http://localhost:8080`. Seed data (15 Austin venues across
5 categories) loads automatically on startup via `data.sql`.

Try it:
```bash
curl "http://localhost:8080/api/places/nearby?lat=30.2672&lon=-97.7431&radiusKm=5"
```

H2 console (optional, for poking at the data): `http://localhost:8080/h2-console`
JDBC URL: `jdbc:h2:mem:austinlocal`, user `sa`, no password.

## Running the Android client

1. Open the `android/` folder in Android Studio (free — Community edition works fine).
2. Let Gradle sync (pulls dependencies from Maven Central / Google's repo, all free).
3. Run the backend first (see above).
4. Run the app on an emulator — `10.0.2.2` in `RetrofitClient.kt` is already
   set to the emulator's alias for your host machine's `localhost:8080`.
   If testing on a physical device, replace it with your machine's LAN IP.

## What each piece demonstrates

| Feature | File | Resume-relevant signal |
|---|---|---|
| Geo-ranked search | `GeoService.java`, `PlaceService.java` | Geospatial querying, ranking algorithms |
| Personalization | `RecommendationService.java` | Rules-based scoring / recommendation logic |
| Caching | `CacheConfig.java`, `@Cacheable` in `PlaceService` | Performance optimization, cache-key design |
| REST API design | `PlaceController.java` | API design, request/response modeling |
| Android client | `SearchScreen.kt`, `DetailScreen.kt`, `SearchViewModel.kt` | Full-stack ownership, shipped UI not just endpoints |

## Suggested next steps to strengthen resume bullets

- Add a simple load test (e.g. with a free tool like `hey` or `wrk`) to get a
  real "handles X req/sec" or "p99 latency under Yms" number.
- Seed a larger dataset (100s of places) and measure cache hit-rate improvement
  on repeated queries — gives you a concrete "%X faster" number, same shape as
  your chatbot project's 52%/71% figures.
- Add a basic `/api/places/search?q=` text search endpoint for a second
  resume-worthy feature.
