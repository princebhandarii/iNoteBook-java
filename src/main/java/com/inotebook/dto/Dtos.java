package com.inotebook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class Dtos {
    private Dtos() {}

    public record RegisterRequest(
            @NotBlank(message = "Username is required")
            @Size(min = 3, max = 50, message = "Username must be 3 to 50 characters")
            @Pattern(regexp = "^[A-Za-z0-9_.-]+$", message = "Username can use letters, numbers, dot, dash and underscore")
            String username,
            @NotBlank(message = "Password is required")
            @Size(min = 6, max = 72, message = "Password must be 6 to 72 characters")
            String password) {}

    public record LoginRequest(
            @NotBlank(message = "Username is required") String username,
            @NotBlank(message = "Password is required") String password) {}

    public record NoteRequest(
            @NotBlank(message = "Title is required")
            @Size(max = 200, message = "Title can be up to 200 characters")
            String title,
            @NotBlank(message = "Content is required")
            @Size(max = 60000, message = "Content is too long")
            String content) {}

    public record NoteResponse(Long id, String title, String content) {}

    public record UserResponse(String username) {}
}
