# 🧩 Nonograms Puzzle Game

A web-based logic puzzle game built with modern technologies, offering user registration, levels of difficulty, scoring, comments, and a ranking system.

## 🛠 Tech Stack

- **Frontend**: React, Axios
- **Backend**: Spring Boot, Spring Security
- **Database**: PostgreSQL
- **API**: RESTful services for client-server communication

## 🎮 Features

- 🔐 User authentication and registration (Spring Security)
- 🧠 Multiple levels of puzzle difficulty
- 🏆 Scoring system and player leaderboard
- 💬 Comment section for each puzzle
- 📊 Real-time ranking of top players
- 🔄 Seamless communication via Axios and REST API

## 🚀 Getting Started

### Prerequisites

- Node.js & npm
- Java 17+ and Maven
- PostgreSQL

### Setup Instructions

### 1. 📥 Clone the Repository

```bash
git clone https://github.com/your-username/nonogram-game.git
cd nonogram-game
```

### 2. ⚙️ Configure the Backend

- Open `src/main/resources/application.properties`
- Update the PostgreSQL configuration with your database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db_name
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. ▶️ Run the Spring Boot Backend

Make sure you're in the project root, then run:

```bash
mvn spring-boot:run
```

> The backend will start at `http://localhost:8080`.

### 4. 💻 Start the React Frontend

```bash
cd frontend
yarn install
yarn start
```

> The frontend will be available at `http://localhost:3000`.

---

✅ Now you can open [http://localhost:3000](http://localhost:3000) in your browser and start playing!

