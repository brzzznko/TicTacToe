# Tic-Tac-Toe Game with Spring WebSockets

![build status badge](https://github.com/brzzznko/TicTacToe/actions/workflows/build.yml/badge.svg)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/bc40e3751b0e4cc48046aa5b880d91ad)](https://app.codacy.com/gh/brzzznko/TicTacToe/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/bc40e3751b0e4cc48046aa5b880d91ad)](https://app.codacy.com/gh/brzzznko/TicTacToe/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_coverage)


This project implements a Tic-Tac-Toe game using Spring Boot and WebSocket technology.  
It consists of two instances, a server and a client, which communicate with each other over WebSocket connections and playing the game automatically.

## Getting Started

###  Running with Docker Compose
* Clone the repository:
   ```bash
   git clone https://github.com/brzzznko/TicTacToe.git

* Navigate to the project directory:
    ```bash
  cd TicTacToe

* Run docker compose:
     ```bash
     docker compose up
     ```

### Running Locally with Gradle
* Clone the repository:
   ```bash
   git clone https://github.com/brzzznko/TicTacToe.git

* Navigate to the project directory:
    ```bash
  cd TicTacToe

* Run server instance with gradle:
     ```bash
     ./gradlew bootRun --args='--spring.profiles.active=server'
     ```
* Run client instance with gradle:
     ```bash
     ./gradlew bootRun --args='--spring.profiles.active=client'
     ```

## Modifying Application Properties
You can modify the application properties to adjust the steps delay, running port, and server destination URL.

## Checking game state
You can access the `/state` endpoint to see the current game state.