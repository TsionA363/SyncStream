package com.syncstream.test;

import java.util.List;

import com.syncstream.api.DatabaseAPI;
import com.syncstream.model.ChatMessage;
import com.syncstream.model.Room;
import com.syncstream.model.User;
import com.syncstream.model.WatchHistory;
import com.syncstream.util.PasswordUtil;

public class TestSuite {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   SyncStream Test Suite - Person 3");
        System.out.println("===========================================\n");

        testPasswordHashing();
        testUserRegistration();
        testDuplicateUsername();
        testLoginSuccess();
        testLoginFailure();
        testCreateRoom();
        testJoinRoomByCode();
        testSaveAndLoadChat();
        testWatchHistory();
        testExportChatLog();

        System.out.println("\n===========================================");
        System.out.println("  Results: " + passed + " passed, " + failed + " failed");
        System.out.println("===========================================");
    }

    static void testPasswordHashing() {
        System.out.print("TEST 1 - Password Hashing: ");

        String hash1 = PasswordUtil.hash("password123");
        String hash2 = PasswordUtil.hash("password123");
        String hash3 = PasswordUtil.hash("differentpassword");

        boolean sameInput = hash1.equals(hash2);
        boolean diffInput = !hash1.equals(hash3);
        boolean correctLen = hash1.length() == 64;
        boolean verifyOk = PasswordUtil.verify("password123", hash1);

        if (sameInput && diffInput && correctLen && verifyOk) {
            System.out.println("PASS ✅");
            passed++;
        } else {
            System.out.println("FAIL ❌  (sameInput=" + sameInput
                    + " diffInput=" + diffInput
                    + " len=" + hash1.length() + ")");
            failed++;
        }
    }

    static void testUserRegistration() {
        System.out.print("TEST 2 - User Registration: ");

        String uniqueUser = "testuser_" + System.currentTimeMillis();
        int userId = DatabaseAPI.register(
                uniqueUser,
                uniqueUser + "@test.com",
                "TestPass1!"
        );

        if (userId > 0) {
            System.out.println("PASS ✅  (user_id=" + userId + ")");
            passed++;
        } else {
            System.out.println("FAIL ❌  (returned " + userId + ")");
            failed++;
        }
    }

    static void testDuplicateUsername() {
        System.out.print("TEST 3 - Duplicate Username Rejected: ");

        String user = "dup_test_" + System.currentTimeMillis();

        DatabaseAPI.register(user, user + "@test.com", "pass");

        int result = DatabaseAPI.register(
                user,
                "different@test.com",
                "pass"
        );

        if (result == -2) {
            System.out.println("PASS ✅  (duplicate correctly rejected)");
            passed++;
        } else {
            System.out.println("FAIL ❌  (should return -2, got " + result + ")");
            failed++;
        }
    }

    static void testLoginSuccess() {
        System.out.print("TEST 4 - Login Success: ");

        String user = "login_ok_" + System.currentTimeMillis();

        DatabaseAPI.register(user, user + "@test.com", "myPassword");

        User loggedIn = DatabaseAPI.login(user, "myPassword");

        if (loggedIn != null && loggedIn.getUsername().equals(user)) {
            System.out.println("PASS ✅  (welcome back " + loggedIn.getUsername() + ")");
            passed++;
        } else {
            System.out.println("FAIL ❌  (login returned null or wrong user)");
            failed++;
        }
    }

    static void testLoginFailure() {
        System.out.print("TEST 5 - Login Failure (wrong password): ");

        String user = "login_fail_" + System.currentTimeMillis();

        DatabaseAPI.register(user, user + "@test.com", "correctPassword");

        User result = DatabaseAPI.login(user, "wrongPassword");

        if (result == null) {
            System.out.println("PASS ✅  (bad credentials correctly rejected)");
            passed++;
        } else {
            System.out.println("FAIL ❌  (login should have returned null)");
            failed++;
        }
    }

    static void testCreateRoom() {
        System.out.print("TEST 6 - Room Creation: ");

        String user = "host_" + System.currentTimeMillis();

        int userId = DatabaseAPI.register(
                user,
                user + "@test.com",
                "pass"
        );

        int roomId = DatabaseAPI.createRoom(
                "Test Movie Night",
                userId,
                "/videos/test.mp4"
        );

        if (roomId > 0) {
            System.out.println("PASS ✅  (room_id=" + roomId + ")");
            passed++;
        } else {
            System.out.println("FAIL ❌  (returned " + roomId + ")");
            failed++;
        }
    }

    static void testJoinRoomByCode() {
        System.out.print("TEST 7 - Join Room by Code: ");

        String host = "hostjoin_" + System.currentTimeMillis();

        int hostId = DatabaseAPI.register(
                host,
                host + "@test.com",
                "pass"
        );

        int roomId = DatabaseAPI.createRoom(
                "Join Test Room",
                hostId,
                "/videos/movie.mp4"
        );

        List<Room> rooms = DatabaseAPI.getActiveRooms();

        String code = rooms.stream()
                .filter(r -> r.getRoomId() == roomId)
                .map(Room::getRoomCode)
                .findFirst()
                .orElse(null);

        String viewer = "viewer_" + System.currentTimeMillis();

        int viewerId = DatabaseAPI.register(
                viewer,
                viewer + "@test.com",
                "pass"
        );

        Room joined = DatabaseAPI.joinRoomByCode(code, viewerId);

        if (joined != null && joined.getRoomId() == roomId) {
            System.out.println("PASS ✅  (joined room '" + joined.getRoomName() + "')");
            passed++;
        } else {
            System.out.println("FAIL ❌  (could not join room by code)");
            failed++;
        }
    }

    static void testSaveAndLoadChat() {
        System.out.print("TEST 8 - Chat Save & Load: ");

        String user = "chatter_" + System.currentTimeMillis();

        int userId = DatabaseAPI.register(
                user,
                user + "@test.com",
                "pass"
        );

        int roomId = DatabaseAPI.createRoom(
                "Chat Test Room",
                userId,
                "/video.mp4"
        );

        DatabaseAPI.saveMessage(roomId, userId, "Hello world!", "text");
        DatabaseAPI.saveMessage(roomId, userId, "❤️", "reaction");
        DatabaseAPI.saveMessage(roomId, userId, "Goodbye", "text");

        List<ChatMessage> messages = DatabaseAPI.getChatHistory(roomId);

        if (messages.size() == 3 &&
                messages.get(0).getMessage().equals("Hello world!")) {

            System.out.println("PASS ✅  (" + messages.size() + " messages loaded)");
            passed++;
        } else {
            System.out.println("FAIL ❌  (expected 3, got " + messages.size() + ")");
            failed++;
        }
    }

    static void testWatchHistory() {
        System.out.print("TEST 9 - Watch History: ");

        String user = "watcher_" + System.currentTimeMillis();

        int userId = DatabaseAPI.register(
                user,
                user + "@test.com",
                "pass"
        );

        int roomId = DatabaseAPI.createRoom(
                "History Room",
                userId,
                "/video.mp4"
        );

        int historyId = DatabaseAPI.startWatchSession(
                userId,
                roomId,
                "/video.mp4"
        );

        boolean ended = DatabaseAPI.endWatchSession(
                historyId,
                1800
        );

        List<WatchHistory> history = DatabaseAPI.getWatchHistory(userId);

        if (ended &&
                !history.isEmpty() &&
                history.get(0).getTotalSeconds() == 1800) {

            System.out.println(
                    "PASS ✅  (watched "
                            + history.get(0).getFormattedDuration()
                            + ")"
            );

            passed++;
        } else {
            System.out.println(
                    "FAIL ❌  (ended="
                            + ended
                            + ", historySize="
                            + history.size()
                            + ")"
            );

            failed++;
        }
    }

    static void testExportChatLog() {
        System.out.print("TEST 10 - Chat Log Export: ");

        String user = "exporter_" + System.currentTimeMillis();

        int userId = DatabaseAPI.register(
                user,
                user + "@test.com",
                "pass"
        );

        int roomId = DatabaseAPI.createRoom(
                "Export Room",
                userId,
                "/video.mp4"
        );

        DatabaseAPI.saveMessage(
                roomId,
                userId,
                "Test message for export",
                "text"
        );

        String filePath = DatabaseAPI.exportChatLog(roomId);

        if (filePath != null &&
                new java.io.File(filePath).exists()) {

            System.out.println("PASS ✅  (saved to " + filePath + ")");
            passed++;
        } else {
            System.out.println("FAIL ❌  (file not created at: " + filePath + ")");
            failed++;
        }
    }
}