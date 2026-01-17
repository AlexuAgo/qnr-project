package com.alex.qnr_project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes ={
        @Index(name = "idx_username", columnList = "username", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            //PK

    @Column(nullable = false, name = "username")
    private String username;    //user username

    @Column(nullable = false, name = "password")
    private String password;    //user password

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name= "role")
    private Set<String> roles = new HashSet<>(); //roles assigned to user
}
