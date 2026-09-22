# RestAssured BDD Framework

A Maven-based BDD API test framework using **Cucumber**, **RestAssured**, and **Allure** reporting.

[![Java](https://img.shields.io/badge/Java-17+-orange)](pom.xml)
· [StockRoom AUT](https://github.com/ashishraj-tyagi/StockRoom)
· [QualForge](https://github.com/ashishraj-tyagi/QualForge)

**Primary target:** [StockRoom](https://github.com/ashishraj-tyagi/StockRoom) (local or Vercel AUT).  
**Design pipeline:** [QualForge](https://github.com/ashishraj-tyagi/QualForge) syncs human-approved Gherkin into `features/qualforge/`.

## Portfolio triad

| Repo | Role |
|------|------|
| [StockRoom](https://github.com/ashishraj-tyagi/StockRoom) | Application under test |
| [QualForge](https://github.com/ashishraj-tyagi/QualForge) | AI-assisted design + guardrails → `approved/` |
| **[RestAssured-BDD](https://github.com/ashishraj-tyagi/RestAssured-BDD)** | Execute approved API scenarios + Allure |
## Stack

| Component   | Purpose                          |
|-------------|----------------------------------|
| Cucumber 7  | Gherkin BDD scenarios            |
| RestAssured | HTTP client & JSON assertions  |
| JUnit 5     | Test execution platform          |
| Allure      | Rich HTML test reports           |

## Project Structure

```
src/test/
├── java/org/poc/
│   ├── client/           # RestAssured HTTP client (+ Vercel bypass header)
│   ├── config/           # Env / -D / properties resolution
│   ├── context/          # Per-scenario shared state
│   ├── hooks/            # Before/After hooks + Allure attachments
│   ├── runners/          # JUnit Platform suite runner
│   ├── stepdefinitions/  # Cucumber step definitions
│   └── utils/            # JSON helpers
└── resources/
    ├── config/           # config.properties (StockRoom base URL)
    ├── features/         # StockRoom smoke + qualforge/ (synced)
    │   └── legacy/       # JSONPlaceholder samples
    └── schemas/          # JSON Schema for response validation
```

## Prerequisites

- Java 17+
- Maven 3.8+
- StockRoom running on `http://127.0.0.1:43124` (or set `STOCKROOM_BASE_URL`)

## Configuration

Default (`src/test/resources/config/config.properties`):

```properties
base.url=${STOCKROOM_BASE_URL:http://127.0.0.1:43124}
request.timeout.ms=10000
```

Overrides (highest wins): `-Dbase.url=...` → `STOCKROOM_BASE_URL` / `BASE_URL` → properties file.

Vercel AUT:

```bash
export STOCKROOM_BASE_URL=https://stock-room-ashishraj-tyagi.vercel.app
export VERCEL_AUTOMATION_BYPASS_SECRET=your-secret
mvn test
```

## Running Tests

```bash
# Default: StockRoom @api and @stockroom
mvn test

# Smoke only
mvn test -Dcucumber.filter.tags="@smoke and @stockroom"

# Legacy JSONPlaceholder suite
STOCKROOM_BASE_URL=https://jsonplaceholder.typicode.com \
  mvn test -Dbase.url=https://jsonplaceholder.typicode.com \
  -Dcucumber.filter.tags="@api and @jsonplaceholder"
```

From QualForge (sync approved features + run):

```bash
cd ../QualForge && npm run test:api
```

## Allure Reports

```bash
mvn clean test
mvn allure:serve
```

## Tags

| Tag | Meaning |
|-----|---------|
| `@api` | API scenario |
| `@stockroom` | StockRoom AUT (default runner filter) |
| `@smoke` / `@critical` | Risk / CI tiers |
| `@jsonplaceholder` | Legacy demo suite |

## Architecture

```
QualForge approved/ ──sync──► features/qualforge/
Feature (.feature)
    ↓
Step Definitions (CommonSteps, …)
    ↓
ApiClient (RestAssured + Allure + optional Vercel bypass)
    ↓
StockRoom AUT
```
