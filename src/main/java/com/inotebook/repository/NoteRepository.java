package com.inotebook.repository;

import com.inotebook.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByOwnerUsernameOrderByIdDesc(String username);
    Optional<Note> findByIdAndOwnerUsername(Long id, String username);
}
