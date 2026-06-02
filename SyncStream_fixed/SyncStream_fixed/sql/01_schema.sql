CREATE DATABASE IF NOT EXISTS syncstream_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE syncstream_db;

CREATE TABLE IF NOT EXISTS users (
    user_id    INT          AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(255) DEFAULT NULL,
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    last_login DATETIME     DEFAULT NULL,
    is_active  TINYINT(1)   DEFAULT 1
);

CREATE TABLE IF NOT EXISTS rooms (
    room_id      INT          AUTO_INCREMENT PRIMARY KEY,
    room_code    VARCHAR(10)  NOT NULL UNIQUE,
    room_name    VARCHAR(100) NOT NULL,
    host_user_id INT          NOT NULL,
    video_path   VARCHAR(500) DEFAULT NULL,
    is_active    TINYINT(1)   DEFAULT 1,
    max_users    INT          DEFAULT 10,
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    closed_at    DATETIME     DEFAULT NULL,
    FOREIGN KEY (host_user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS participants (
    participant_id INT        AUTO_INCREMENT PRIMARY KEY,
    room_id        INT        NOT NULL,
    user_id        INT        NOT NULL,
    joined_at      DATETIME   DEFAULT CURRENT_TIMESTAMP,
    left_at        DATETIME   DEFAULT NULL,
    role           ENUM('host','viewer') DEFAULT 'viewer',
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY uq_room_user (room_id, user_id)
);

CREATE TABLE IF NOT EXISTS chat_messages (
    message_id  INT           AUTO_INCREMENT PRIMARY KEY,
    room_id     INT           NOT NULL,
    user_id     INT           NOT NULL,
    message     TEXT          NOT NULL,
    msg_type    ENUM('text','reaction','system') DEFAULT 'text',
    sent_at     DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS watch_history (
    history_id    INT          AUTO_INCREMENT PRIMARY KEY,
    user_id       INT          NOT NULL,
    room_id       INT          NOT NULL,
    video_path    VARCHAR(500) DEFAULT NULL,
    watch_start   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    watch_end     DATETIME     DEFAULT NULL,
    total_seconds INT          DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE
);

CREATE INDEX idx_rooms_host        ON rooms(host_user_id);
CREATE INDEX idx_participants_room ON participants(room_id);
CREATE INDEX idx_chat_room         ON chat_messages(room_id);
CREATE INDEX idx_chat_sent         ON chat_messages(sent_at);
CREATE INDEX idx_history_user      ON watch_history(user_id);