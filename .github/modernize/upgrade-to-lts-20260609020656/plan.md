# Upgrade Plan

## Overview

Upgrade the Java project to the latest LTS versions: **Java 25** and **Spring Boot 4.x** (with Spring Framework 7.x and Jakarta EE namespace migration).

## Tasks

See `.metadata/tasks.json` for the detailed task breakdown.

### 001 — Upgrade Java & Spring Boot

- **Target Java**: 25
- **Target Spring Boot**: 4.x
- **Target Spring Framework**: 7.x
- **Jakarta EE**: Migrate `javax.*` imports to `jakarta.*`

## Notes

- Spring Boot 4.x requires Java 25 and includes Spring Framework 7.x.
- The `javax.*` to `jakarta.*` namespace migration is included in the Spring Boot 4.x upgrade task.
- No separate JDK or Spring Framework tasks are needed -- they are covered by the Spring Boot 4.x upgrade.
