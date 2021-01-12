package com.bikemongo.api.payload.response;

import java.util.List;

public class JwtResponse {
    
    private String token;
    private String type = "Bearer";
    private String id;
    private String email;
    private List<String> roles;

    public JwtResponse(String token, String id, String email, List<String> roles) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

}
