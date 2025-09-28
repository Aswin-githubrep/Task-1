Kaiburr – Task 1: Java REST API with MongoDB

Author: Aswin V
Date: 28 Sep 2025
Tech Stack: Java 20 · Spring Boot 3.5.6 · MongoDB Atlas · Maven

📌 Overview

This project is a Spring Boot REST API that manages Task objects and their execution history.
Each task represents a shell command that can be created, executed, searched, and deleted.

📂 Data Model

Task Object
{
  "id": "string",
  "name": "string",
  "owner": "string",
  "command": "string",
  "taskExecutions": [
    {
      "startTime": "ISO-8601",
      "endTime": "ISO-8601",
      "output": "string"
    }
  ]
}

⚙️ Setup Instructions

Clone the repository

git clone https://github.com/Aswin-githubrep/Task-1.git
cd Task-1


Configure MongoDB Atlas

Create a MongoDB cluster (or use existing).

Add your IP to Network Access.

Create a database user and note the password.

Copy src/main/resources/application.properties.example to application.properties and update with your MongoDB URI:

spring.data.mongodb.uri=mongodb+srv://<username>:<password>@cluster0.xxxxx.mongodb.net/TaskDB


Run the project

mvn spring-boot:run

🚀 API Endpoints
Create a Task
curl -X POST http://localhost:8080/tasks -H "Content-Type: application/json" \
-d '{"id":"123","name":"Print Hello","owner":"Aswin","command":"echo Hello World!"}'

Get All Tasks
curl http://localhost:8080/tasks

Get Task by ID
curl http://localhost:8080/tasks/123

Search Tasks by Name
curl "http://localhost:8080/tasks/search?name=Print"

Execute a Task
curl -X PUT http://localhost:8080/tasks/123/execute

Delete a Task
curl -X DELETE http://localhost:8080/tasks/123

📸 Screenshots

(Add screenshots of API calls, VS Code terminal, MongoDB Atlas connection, etc.)



✅ Status

 Implemented REST API

 Connected to MongoDB Atlas

 CRUD operations working

 Task execution feature working