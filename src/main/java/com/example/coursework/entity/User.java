package com.example.coursework.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;

import java.security.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Data
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = true)
    private String firstName;

    @Column(nullable = true)
    private String lastName;

    private String role = "USER";

    @Column(nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
    }

    public User(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.setPassword(password);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User() {
        this.username = "";
        this.password = "";
        this.email = "";
        this.firstName = "";
        this.lastName = "";
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Геттеры и сеттеры
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

