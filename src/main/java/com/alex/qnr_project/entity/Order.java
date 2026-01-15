package com.alex.qnr_project.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders", indexes ={
        @Index(name = "idx_status", columnList = "status"),
        @Index(name =  "idx_created_at", columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    //PK

    @Column(nullable = false, name = "description")
    private String description;         //Order description

    @Column(nullable = false, name = "status")
    private String status;              //Order status (completed,pending)

    @Column(nullable = false, name = "createdAt")
    private LocalDateTime createdAt;    //Timestamp at order creation

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;                  //the user who owns this order

}
