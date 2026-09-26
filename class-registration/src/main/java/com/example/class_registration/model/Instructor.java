package com.example.class_registration.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "instructors", schema="class_admin")
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

    @ManyToOne
    @JoinColumn(name = "association_id")
    private Associations association;

    @Column
    private String aboutMe;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(length = 64)
    private String verificationToken;

    @Column
    private LocalDateTime verificationTokenExpiry;
}
