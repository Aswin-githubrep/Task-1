# Kaiburr – Task 1: Java REST API with MongoDB

**Author:** Aswin V
**Date:** 28 Sep 2025  
**Tech:** Java 20, Spring Boot 3.5.6, MongoDB Atlas, Maven

## Overview
A Spring Boot REST API that manages **Task** objects (shell commands) and their **execution history**.

### Data Model

**Task**
```json
{
  "id": "string",
  "name": "string",
  "owner": "string",
  "command": "string",
  "taskExecutions": [
    { "startTime": "ISO-8601", "endTime": "ISO-8601", "output": "string" }
  ]
}
