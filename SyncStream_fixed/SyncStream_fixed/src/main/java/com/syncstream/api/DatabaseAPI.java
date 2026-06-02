package com.syncstream.api;

import java.util.List;

import com.syncstream.dao.ChatMessageDAO;
import com.syncstream.dao.ParticipantDAO;
import com.syncstream.dao.RoomDAO;
import com.syncstream.dao.UserDAO;
import com.syncstream.dao.WatchHistoryDAO;
import com.syncstream.model.ChatMessage;
import com.syncstream.model.Room;
import com.syncstream.model.User;
import com.syncstream.model.WatchHistory;
import com.syncstream.util.FileUtil;

public class DatabaseAPI {

    private static final UserDAO userDAO = new UserDAO();
    private static final RoomDAO roomDAO = new RoomDAO();
    private static final ParticipantDAO participantDAO = new ParticipantDAO();
    private static final ChatMessageDAO chatMessageDAO = new ChatMessageDAO();
    private static final WatchHistoryDAO watchHistoryDAO = new WatchHistoryDAO();

    public static int register(String username, String email, String password) {
        if (userDAO.usernameExists(username)) {
            System.out.println("[API] Username already taken: " + username);
            return -2;
        }
        return userDAO.registerUser(username, email, password);
    }

    public static User login(String username, String password) {
        return userDAO.login(username, password);
    }

    public static User getUser(int userId) {
        return userDAO.getUserById(userId);
    }

    public static int createRoom(String roomName, int hostUserId, String videoPath) {
        int roomId = roomDAO.createRoom(roomName, hostUserId, videoPath);
        if (roomId > 0) {
            participantDAO.joinRoom(roomId, hostUserId, "host");
        }
        return roomId;
    }

    public static Room joinRoomByCode(String roomCode, int userId) {
        Room room = roomDAO.getRoomByCode(roomCode);
        if (room == null) {
            System.out.println("[API] Room not found: " + roomCode);
            return null;
        }

        int count = participantDAO.getActiveCount(room.getRoomId());

        if (count >= room.getMaxUsers()) {
            System.out.println("[API] Room is full: " + roomCode);
            return null;
        }

        participantDAO.joinRoom(room.getRoomId(), userId, "viewer");
        return room;
    }

    public static boolean closeRoom(int roomId) {
        return roomDAO.closeRoom(roomId);
    }

    public static List<Room> getActiveRooms() {
        return roomDAO.getActiveRooms();
    }

    public static boolean leaveRoom(int roomId, int userId) {
        return participantDAO.leaveRoom(roomId, userId);
    }

    public static int getParticipantCount(int roomId) {
        return participantDAO.getActiveCount(roomId);
    }

    public static int saveMessage(int roomId, int userId, String message, String msgType) {
        return chatMessageDAO.saveMessage(roomId, userId, message, msgType);
    }

    public static List<ChatMessage> getChatHistory(int roomId) {
        return chatMessageDAO.getMessagesByRoom(roomId);
    }

    public static String exportChatLog(int roomId) {
        String logContent = chatMessageDAO.exportChatLog(roomId);
        return FileUtil.saveChatLog(roomId, logContent);
    }

    public static int startWatchSession(int userId, int roomId, String videoPath) {
        return watchHistoryDAO.startSession(userId, roomId, videoPath);
    }

    public static boolean endWatchSession(int historyId, int totalSeconds) {
        return watchHistoryDAO.endSession(historyId, totalSeconds);
    }

    public static List<WatchHistory> getWatchHistory(int userId) {
        return watchHistoryDAO.getHistoryByUser(userId);
    }
}