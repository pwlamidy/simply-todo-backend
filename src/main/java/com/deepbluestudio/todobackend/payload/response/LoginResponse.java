package com.deepbluestudio.todobackend.payload.response;

public class LoginResponse {
    private String accessToken;
    private String username;
    private String email;

    public LoginResponse(String accessToken, String username, String email) {
        this.accessToken = accessToken;
        this.username = username;
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
