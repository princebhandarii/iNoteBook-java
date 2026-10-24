package com.inotebook.controller;

import com.inotebook.dto.Dtos.*;
import com.inotebook.model.Note;
import com.inotebook.model.User;
import com.inotebook.repository.NoteRepository;
import com.inotebook.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteRepository notes;
    private final UserRepository users;

    public NoteController(NoteRepository notes, UserRepository users) {
        this.notes = notes;
        this.users = users;
    }

    private String currentUser(HttpSession session) {
        Object u = session.getAttribute(AuthController.SESSION_USER);
        if (u == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please sign in again");
        return u.toString();
    }

    private static NoteResponse toDto(Note n) {
        return new NoteResponse(n.getId(), n.getTitle(), n.getContent());
    }

    private Note findOwned(Long id, String username) {
        return notes.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<NoteResponse> list(HttpSession session) {
        return notes.findByOwnerUsernameOrderByIdDesc(currentUser(session)).stream().map(NoteController::toDto).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public NoteResponse create(@Valid @RequestBody NoteRequest req, HttpSession session) {
        User owner = users.findByUsername(currentUser(session))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please sign in again"));
        return toDto(notes.save(new Note(req.title().trim(), req.content(), owner)));
    }

    @PutMapping("/{id}")
    @Transactional
    public NoteResponse update(@PathVariable Long id, @Valid @RequestBody NoteRequest req, HttpSession session) {
        Note n = findOwned(id, currentUser(session));
        n.setTitle(req.title().trim());
        n.setContent(req.content());
        return toDto(notes.save(n));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpSession session) {
        notes.delete(findOwned(id, currentUser(session)));
        return ResponseEntity.noContent().build();
    }
}
