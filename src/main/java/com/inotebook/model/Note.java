package com.inotebook.model;

import jakarta.persistence.*;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** Foreign key: notes.owner_username -> users.username */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_username", referencedColumnName = "username", nullable = false)
    private User owner;

    protected Note() {}

    public Note(String title, String content, User owner) {
        this.title = title;
        this.content = content;
        this.owner = owner;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public User getOwner() { return owner; }
}
