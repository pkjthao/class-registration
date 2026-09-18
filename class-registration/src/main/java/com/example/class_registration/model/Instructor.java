package com.example.class_registration.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Instructor {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String firstName;

    @Column(nullable=true)
    private String middleName;

    @Column(nullable=false)
    private String lastName;

    @Column(nullable=false, unique=true)
    private String phone;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable=true)
    private String password;

    @Column
    private String aboutMe;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(length = 64)
    private String verificationToken;

    @Column
    private LocalDateTime verificationTokenExpiry;
}
