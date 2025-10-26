package com.todo.webapp.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String firstname;

    @Column(nullable = false, length = 100)
    private String lastname;

    public User(String email, String firstname, String lastname, String cognitoSub) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.cognitoSub = cognitoSub;
    }

    @Column(nullable = false)
    private String cognitoSub;

    // One user can have many tasks
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;
}
