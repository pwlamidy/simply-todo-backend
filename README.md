# API for Todo app
<div>
  <p>
    Backend source code for <a href="https://github.com/pwlamidy/simply-todo">Todo app</a>
  </p>

  <p>This project is built for demonstration and still under development.</p>
</div>

<!-- GETTING STARTED -->
## Getting Started

This project can be set up with the following steps:

### Prerequisites

* PostgresSQL database. Configurations in `application.properties`

### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/pwlamidy/simply-todo-backend.git
   ```
2. Go to project folder
   ```sh
   cd simply-todo-backend
   ```
3. Start the application
   ```sh
   ./mvnw spring-boot:run
   ```


<!-- Usage -->
## Usage
To check the application is up and running,
```sh
curl localhost:8080
```
You should see similar response
```json
{"timestamp":"2022-12-04T09:18:16.302+00:00","status":404,"error":"Not Found","message":"No message available","path":"/"}
```
Documenatation available on [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)