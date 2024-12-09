package com.example.coursework.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID
    @Getter
    private String courseName;
    @Getter
    @Column(columnDefinition = "LONGTEXT")
    private String courseDescription;
    @Getter
    @Column(columnDefinition = "LONGTEXT")
    private String courseText;
    @Getter
    private String teacher;
    @Getter
    private LocalDateTime startDateTime;
    @Getter
    private LocalDateTime endDateTime;


    public Course() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }


    @Override
    public String toString() {
        return "course [id=" + id + ", courseName=" + courseName +", courseDescription=" + courseDescription + ", courseText=" + courseText + ", teacher=" + teacher + ", startDateTime=" + startDateTime + ",endDateTime" + endDateTime + "]";
    }
}