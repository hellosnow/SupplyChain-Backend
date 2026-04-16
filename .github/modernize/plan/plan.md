# Modernization Plan: SupplyChain Backend Java 25 Upgrade

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven
- **Database**: MySQL 8.0
- **Key Dependencies**: Spring Data JPA, Spring AMQP
  (RabbitMQ), Lombok, Spring Validation

---

## Overview

> This migration upgrades the SupplyChain Backend
> application from Java 8 and Spring Boot 2.7.18 to
> Java 25 and Spring Boot 4.0+. The application
> currently runs on legacy Java 8 with Spring Boot 2.x,
> both of which are end-of-life per organizational
> policy. The upgraded architecture will:
>
> - Run on Java 25 (latest LTS) as required by
>   organizational targets
> - Use Spring Boot 4.0+ with the corresponding
>   Spring Framework upgrade
> - Complete the javax.* to jakarta.* namespace
>   migration required by the framework upgrade
> - Update the Dockerfile to use organizationally
>   approved Java 25 base images
>
> The max supported upgrade path configuration
> (Java 21 / Spring Boot 3.x) is intentionally
> overridden per user directive to align with
> the organizational playbook targets.

---

## Migration Impact Summary

| Application | Original | Target | Comments |
|---|---|---|---|
| supplychain-backend | Java 8, Spring Boot 2.7.18 | Java 25, Spring Boot 4.0+ | Full upgrade |
