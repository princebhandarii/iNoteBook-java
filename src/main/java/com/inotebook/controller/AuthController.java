package com.inotebook.controller;

import com.inotebook.dto.Dtos.*;
import com.inotebook.model.User;
import com.inotebook.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public static final String SESSION_USER = "username";

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest req) {
        String username = req.username().trim();
        if (users.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That username is already taken");
        }
        try {
            users.save(new User(username, encoder.encode(req.password())));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That username is already taken");
        }
        return new UserResponse(username);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest req, HttpServletRequest request) {
        User user = users.findByUsername(req.username().trim())
                .filter(u -> encoder.matches(req.password(), u.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong username or password"));
        HttpSession old = request.getSession(false);
        if (old != null) old.invalidate();          // prevent session fixation
        request.getSession(true).setAttribute(SESSION_USER, user.getUsername());
        return new UserResponse(user.getUsername());
    }

    @GetMapping("/me")
    public UserResponse me(HttpSession session) {
        Object u = session.getAttribute(SESSION_USER);
        if (u == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not signed in");
        return new UserResponse(u.toString());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }
}
