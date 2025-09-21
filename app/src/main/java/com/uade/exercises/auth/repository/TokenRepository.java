package com.uade.exercises.auth.repository;

public interface TokenRepository {  
    void saveToken(String token);
    String getToken();
    void clearToken();
    boolean hasToken();
}
