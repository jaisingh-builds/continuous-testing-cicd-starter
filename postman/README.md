# Postman / Newman (Day 5)

From repo root (uses `package.json` devDependency):

```bash
npm install
npm run newman
```

Or with a global Newman: `newman run postman/collection.json -e postman/env-ci.json -r cli,junit --reporter-junit-export postman/results/newman-junit.xml`

Point `baseUrl` in `env-ci.json` at a running starter app to hit `UserController` instead of JSONPlaceholder.
