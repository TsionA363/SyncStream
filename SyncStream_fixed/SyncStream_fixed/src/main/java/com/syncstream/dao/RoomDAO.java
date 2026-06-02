package com.syncstream.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.syncstream.db.DBConnection;
import com.syncstream.model.Room;

public class RoomDAO {

    private final Connection conn;

    public RoomDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public int createRoom(String roomName, int hostUserId, String videoPath) {
        String roomCode = generateRoomCode();
        String sql = "INSERT INTO rooms (room_code, room_name, host_user_id, video_path) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, roomCode);
            ps.setString(2, roomName);
            ps.setInt(3, hostUserId);
            ps.setString(4, videoPath);

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int newId = keys.getInt(1);
                System.out.println("[RoomDAO] Room created: " + roomName + " (code=" + roomCode + ")");
                return newId;
            }
        } catch (SQLException e) {
            System.err.println("[RoomDAO] createRoom error: " + e.getMessage());
        }

        return -1;
    }

    public Room getRoomByCode(String roomCode) {
        String sql = """
            SELECT r.*, u.username AS host_username
            FROM rooms r
            JOIN users u ON r.host_user_id = u.user_id
            WHERE r.room_code = ? AND r.is_active = 1
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomCode);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToRoom(rs);
            }
        } catch (SQLException e) {
            System.err.println("[RoomDAO] getRoomByCode error: " + e.getMessage());
        }

        return null;
    }

    public Room getRoomById(int roomId) {
        String sql = """
            SELECT r.*, u.username AS host_username
            FROM rooms r
            JOIN users u ON r.host_user_id = u.user_id
            WHERE r.room_id = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToRoom(rs);
            }
        } catch (SQLException e) {
            System.err.println("[RoomDAO] getRoomById error: " + e.getMessage());
        }

        return null;
    }

    public List<Room> getActiveRooms() {
        List<Room> rooms = new ArrayList<>();

        String sql = """
            SELECT r.*, u.username AS host_username
            FROM rooms r
            JOIN users u ON r.host_user_id = u.user_id
            WHERE r.is_active = 1
            ORDER BY r.created_at DESC
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                rooms.add(mapRowToRoom(rs));
            }
        } catch (SQLException e) {
            System.err.println("[RoomDAO] getActiveRooms error: " + e.getMessage());
        }

        return rooms;
    }

    public boolean closeRoom(int roomId) {
        String sql = "UPDATE rooms SET is_active = 0, closed_at = NOW() WHERE room_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);

            boolean success = ps.executeUpdate() > 0;

            if (success) {
                System.out.println("[RoomDAO] Room closed: " + roomId);
            }

            return success;
        } catch (SQLException e) {
            System.err.println("[RoomDAO] closeRoom error: " + e.getMessage());
        }

        return false;
    }

    private String generateRoomCode() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();
    }

    private Room mapRowToRoom(ResultSet rs) throws SQLException {
        Room r = new Room();

        r.setRoomId(rs.getInt("room_id"));
        r.setRoomCode(rs.getString("room_code"));
        r.setRoomName(rs.getString("room_name"));
        r.setHostUserId(rs.getInt("host_user_id"));
        r.setVideoPath(rs.getString("video_path"));
        r.setActive(rs.getBoolean("is_active"));
        r.setMaxUsers(rs.getInt("max_users"));
        r.setHostUsername(rs.getString("host_username"));

        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            r.setCreatedAt(created.toLocalDateTime());
        }

        Timestamp closed = rs.getTimestamp("closed_at");
        if (closed != null) {
            r.setClosedAt(closed.toLocalDateTime());
        }

        return r;
    }
}