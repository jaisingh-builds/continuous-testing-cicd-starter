# Day 8 Capstone Strategy Template

## 1) Project Scenario
- Application type:
- Team context:
- Current CI maturity:

## 2) Test Strategy (Pyramid)
- Unit tests: __% (target range 60-70%)
- Integration tests: __% (target range 20-30%)
- API tests: __% (service contract focus)
- E2E tests: __% (keep <= 10%)
- Rationale:

## 3) Tooling Decisions
- Test framework:
- API testing:
- Coverage/reporting:
- Container strategy:
- CI platform:
- Why this stack:

## 4) Quality Gates
- JaCoCo LINE threshold: __ (recommended >= 0.80)
- JaCoCo BRANCH threshold: __ (recommended >= 0.70)
- Pass rate target:
- Flake rate target:
- Merge-block rules:

## 5) Pipeline Architecture
- Trigger model (push / PR / schedule):
- Fail-fast ordering:
- Parallel jobs:
- Artifact/report publish approach:
- Expected pipeline duration target:

## 6) Environment and Test Data
- Secret management approach:
- Env var injection plan:
- Seed data strategy:
- Data isolation approach (UUID / cleanup / reset):

## 7) Anti-Patterns You Intentionally Avoid
- [ ] Ice-cream cone test distribution
- [ ] Hardcoded secrets/config in test code
- [ ] Retry-only flake handling
- [ ] Full E2E on every PR push
- [ ] Missing report upload on failures

## 8) Rollout Plan
- Week 1:
- Month 1:
- Quarter 1:
- Owners:
