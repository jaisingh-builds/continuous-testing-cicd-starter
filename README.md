# Course starter repository

Spring Boot + JUnit 5 project used from **Day 1 onward**. Earlier labs cover tagging and REST Assured; **Day 6 (Module 6)** adds Dockerised Postgres tests here.

## Day 6 — Docker Compose + JDBC tests

**Artifacts (repo root)**

| File | Role |
|------|------|
| `Dockerfile.test` | Temurin **21** Alpine + Maven Wrapper + dependency cache layer |
| `docker-compose.test.yml` | `postgres:16-alpine` + healthcheck + `test-runner` |
| `src/test/resources/data/seed.sql` | DB init + baseline rows (`test_alice`, …) |
| `src/test/java/com/training/ctcicd/lab6/*` | JDBC tests + optional Testcontainers |

**Commands**

```bash
# Host (no Postgres): JDBC tests skip; optional Testcontainers excluded by default
chmod +x mvnw
./mvnw test -B --no-transfer-progress

# Full stack (from this directory)
export DB_PASS=testpass   # optional; compose default matches training
docker compose -f docker-compose.test.yml up --build --abort-on-container-exit --exit-code-from test-runner
docker compose -f docker-compose.test.yml down -v
```

Inside Compose, Surefire matches the **unit** slice (`-DexcludedGroups=integration`) plus **4** JDBC tests against Postgres (`PostgresSeedDataTest`, `PostgresTransactionRollbackTest`). Exact totals depend on earlier lab tests in `*Test.java`; as shipped, expect **12** tests with **0** failures (**8** lab smoke/data-driven + **4** Day 6 JDBC).

**Optional Testcontainers (host must expose Docker to the JVM)**

```bash
./mvnw test -Pwith-testcontainers -DexcludedGroups=integration -B --no-transfer-progress
```

Background: [`docs/TESTCONTAINERS_NOTES.md`](docs/TESTCONTAINERS_NOTES.md).

Uses `PostgresTcTest` (`disabledWithoutDocker = true` → skips instead of failing when Docker is unavailable to Java). On Docker Desktop macOS, if `docker compose` works but Testcontainers skips, try:

```bash
export DOCKER_HOST="unix://${HOME}/Library/Containers/com.docker.docker/Data/docker-cli.sock"
./mvnw test -Pwith-testcontainers -DexcludedGroups=integration -B --no-transfer-progress
```

**CI**

This repository’s `.github/workflows/ci.yml` includes job **`Day 6 — Docker Compose (Postgres + test-runner)`** (`docker-compose-day6`). If you work inside the **course monorepo** (where `starter-repo/` is a subfolder), the same job is defined in the **parent** repository’s `.github/workflows/ci.yml` and runs with `working-directory: starter-repo`.

**Deliverables**

Templates live under [`deliverables/`](deliverables/).

**Secrets**

Training default password is `${DB_PASS:-testpass}` in compose. For real projects use GitHub **Secrets** and map `DB_PASS: ${{ secrets.TEST_DB_PASS }}` in the workflow.
