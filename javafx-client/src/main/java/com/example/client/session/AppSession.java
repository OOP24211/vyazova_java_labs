package com.example.client.session;

public class AppSession {

    private static String username;
    private static Long userId;
    private static Long roomId;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        AppSession.username = username;
    }

    public static Long getUserId() {
        return userId;
    }

    public static void setUserId(Long userId) {
        AppSession.userId = userId;
    }

    public static Long getRoomId() {
        return roomId;
    }

    public static void setRoomId(Long roomId) {
        AppSession.roomId = roomId;
    }

    public static void clear() {
        username = null;
        userId = null;
        roomId = null;
    }
}
