package com.example.coursework.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Setter
@Getter
@Entity
public class Review {
    // Геттеры и сеттеры
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    private String text;

    @ManyToOne
    private User user;

    @ManyToOne
    private Course course;

    private LocalDateTime date;

}

