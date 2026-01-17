package com.alex.qnr_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alex.qnr_project.entity.Order;
import com.alex.qnr_project.entity.User;
import com.alex.qnr_project.repository.OrderRepository;
import com.alex.qnr_project.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

@SpringBootApplication
public class QnrProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(QnrProjectApplication.class, args);
    }
    @Configuration
    public class DataLoader {

        @Bean
        CommandLineRunner initDatabase(UserRepository userRepository,
                                       OrderRepository orderRepository,
                                       PasswordEncoder passwordEncoder) {
            return args -> {

                // Check if user already exists to avoid duplicates
                if (userRepository.findByUsername("admin").isEmpty()) {
                    // Create a user
                    User admin = User.builder()
                            .username("admin")
                            .password(passwordEncoder.encode("123")) // hashed password
                            .roles(Set.of("USER"))
                            .build();
                    userRepository.save(admin);

                 /*
                    Order order1 = Order.builder()
                            .description("First order")
                            .status("pending")
                            .createdAt(LocalDateTime.now())
                            .user(admin)
                            .build();

                    Order order2 = Order.builder()
                            .description("Second order")
                            .status("completed")
                            .createdAt(LocalDateTime.now())
                            .user(admin)
                            .build();

                    orderRepository.save(order1);
                    orderRepository.save(order2); */

                    System.out.println("user inserted into database!");
                }
            };
        }
    }
}