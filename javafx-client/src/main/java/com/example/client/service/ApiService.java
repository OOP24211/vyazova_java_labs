package com.example.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String AUTH_URL = "http://localhost:8080/users";
    private static final String ROOMS_URL = "http://localhost:8080/api/rooms";
    private static final String FILES_URL = "http://localhost:8080/files";

    public boolean login(String username, String password) {
        try {
            String body = """
        {
            "username": "%s",
            "password": "%s"
        }
        """.formatted(username, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AUTH_URL + "/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            String resp = response.body().trim();
            System.out.println("LOGIN RESPONSE: '" + resp + "'");

            return response.statusCode() == 200 && "Login successful".equals(resp);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(String username, String password) {
        try {
            String body = """
            {
                "username": "%s",
                "password": "%s"
            }
            """.formatted(username, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AUTH_URL + "/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            System.out.println("REGISTER RESPONSE: " + response.body());
            System.out.println("REGISTER STATUS: " + response.statusCode());

            return response.statusCode() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getRooms() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ROOMS_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            System.out.println("ROOMS RESPONSE: " + response.body());
            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String createRoom(String name) {
        try {
            String body = """
            {
                "name": "%s"
            }
            """.formatted(name);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ROOMS_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            System.out.println("CREATE ROOM RESPONSE: " + response.body());
            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getHistory(int roomId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/messages?roomId=" + roomId))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            System.out.println("HISTORY RAW JSON: " + body);
            return body;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean uploadFile(java.io.File file, int roomId, String username) {
        try {
            String boundary = "---boundary_" + System.currentTimeMillis();
            String lineBreak = "\r\n";

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            writer.append("--").append(boundary).append(lineBreak);
            writer.append("Content-Disposition: form-data; name=\"roomId\"").append(lineBreak);
            writer.append(lineBreak);
            writer.append(String.valueOf(roomId)).append(lineBreak);

            writer.append("--").append(boundary).append(lineBreak);
            writer.append("Content-Disposition: form-data; name=\"username\"").append(lineBreak);
            writer.append(lineBreak);
            writer.append(username).append(lineBreak);

            writer.append("--").append(boundary).append(lineBreak);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"").append(lineBreak);
            writer.append("Content-Type: " + Files.probeContentType(file.toPath())).append(lineBreak);
            writer.append(lineBreak);
            writer.flush();

            Files.copy(file.toPath(), outputStream);
            outputStream.flush();

            writer.append(lineBreak);
            writer.append("--").append(boundary).append("--").append(lineBreak);
            writer.flush();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(FILES_URL + "/upload"))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(outputStream.toByteArray()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("FILE UPLOAD RESPONSE: " + response.body());
            System.out.println("FILE UPLOAD STATUS: " + response.statusCode());

            return response.statusCode() == 200;

        } catch (Exception e) {
            System.err.println("Error uploading file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}