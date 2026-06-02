# SyncStream

## Synchronized Video Streaming Platform

SyncStream is a Java-based synchronized video streaming application that allows multiple users to watch videos together in real time. The system provides room creation, synchronized playback, live chat, user management, and watch history tracking through a client-server architecture backed by a MySQL database.

---

## Project Overview

Watching videos remotely with friends often leads to playback synchronization issues. SyncStream solves this problem by enabling users to create shared watch rooms where video playback actions such as play, pause, and seek are synchronized among all participants.

### Technologies Used

- JavaFX GUI
- Java Socket Programming
- MySQL Database
- Maven Build System
- Multi-threaded Server Architecture

---

## Features

### User Management

- User Registration
- User Login
- Password Protection
- Session Management

### Watch Rooms

- Create Rooms
- Join Rooms
- Host Management
- Participant Tracking

### Synchronized Streaming

- Play Synchronization
- Pause Synchronization
- Seek Synchronization

### Live Chat

- Real-Time Messaging
- Room-Based Communication
- Chat History Storage

### Database Management

- User Records
- Room Records
- Participant Records
- Chat Records
- Watch History Records

### Reporting

- Export Chat Logs
- Session Reports
- Watch History Tracking

---

##  System Architecture

```text
+---------------------+
|    JavaFX Client    |
+----------+----------+
           |
           v
+---------------------+
|     TCP Server      |
|     Port 8080       |
+----------+----------+
           |
           v
+---------------------+
|     MySQL DB        |
+---------------------+
```

### Architecture Layers

1. Presentation Layer (JavaFX GUI)
2. Application Layer (Server & Business Logic)
3. Data Layer (MySQL Database)

---

## Project Structure

```text
SyncStream
│
├── sql/
│   ├── 01_schema.sql
│   ├── 02_seed_data.sql
│   └── 03_queries.sql
│
├── src/
│   ├── client/
│   ├── server/
│   ├── dao/
│   ├── model/
│   ├── gui/
│   ├── db/
│   └── util/
│
├── reports/
├── lib/
└── pom.xml
```

---

##  Requirements

- Java 17+
- JavaFX
- MySQL 8+
- Maven
- JDBC Driver

---

## Database Setup

### Option 1: Using phpMyAdmin (XAMPP)

1. Start **Apache** and **MySQL** from XAMPP.
2. Open:

```
http://localhost/phpmyadmin
```

3. Import:

```text
sql/01_schema.sql
```

This file automatically creates:

```sql
CREATE DATABASE IF NOT EXISTS syncstream_db;
```

and all required tables.

4. Import:

```text
sql/02_seed_data.sql
```

to insert sample data.

### Option 2: MySQL Command Line

```sql
SOURCE sql/01_schema.sql;
SOURCE sql/02_seed_data.sql;
```

---

##  Configure Database Connection

Open:

```java
DBConnection.java
```

Update credentials:

```java
private static final String URL =
    "jdbc:mysql://localhost:3306/syncstream_db";

private static final String USERNAME = "root";
private static final String PASSWORD = "";
```

> Note: If using XAMPP, the default MySQL password is usually empty.

---

## Running the Application

### Build Project

```bash
mvn clean install
```

### Start Server

```bash
java -jar target/SyncStream-Server.jar
```

Expected Output:

```text
SyncStream Server starting on port 8080
Waiting for connections...
```

### Launch Client

```bash
mvn javafx:run
```

---

##  Workflow

1. User registers or logs in.
2. User creates or joins a room.
3. Host selects a video.
4. Playback events are synchronized.
5. Users communicate through chat.
6. Activity is stored in MySQL.
7. Reports can be generated and exported.

---

##  Testing

Run all tests:

```bash
mvn test
```

Tests include:

- Database Connectivity
- User Operations
- Room Operations
- Chat Operations
- Watch History Operations

---

## Security Features

- Password Hashing
- Session Tracking
- Input Validation
- Database Constraints
- Foreign Key Relationships

---

## Future Improvements

- Voice Chat
- Video Chat
- Screen Sharing
- Cloud Deployment
- Mobile Application
- End-to-End Encryption

---

## Team Roles

### Frontend Developer

- JavaFX Interface
- Controllers
- User Experience

### Backend Developer

- TCP Server
- Client Communication
- Synchronization Logic

### Database Developer

- Database Design
- SQL Queries
- DAO Implementation
- Testing

---


---

