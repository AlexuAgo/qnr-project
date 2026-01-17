package com.alex.qnr_project.controller;

import com.alex.qnr_project.entity.Order;
import com.alex.qnr_project.entity.User;
import com.alex.qnr_project.repository.OrderRepository;
import com.alex.qnr_project.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;  // repo for order
    private final UserRepository userRepository;    // repo to load user data from db

    public OrderController(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }


    // create order
    @PostMapping
    public Order createOrder(@RequestBody CreateOrderRequest request,
                             @AuthenticationPrincipal UserDetails userDetails) {

        // load user
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // build order entity
        Order order = Order.builder()
                .description(request.getDescription())
                .status(request.getStatus())
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        //return the order
        return orderRepository.save(order);
    }

    // get all orders of a user
    @GetMapping
    public Page<Order> getOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status
    ) {

        //load user
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //create pageable object
        Pageable pageable = PageRequest.of(page, size);

        // if status filter is provided, we paginate orders according to that status
        if (status != null) {
            return orderRepository.findByUserAndStatus(user, status, pageable);
        }

        //return all orders of the user only with pagination
        return orderRepository.findByUser(user, pageable);
    }

    // get one specific order
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id,
                                          @AuthenticationPrincipal UserDetails userDetails) {

        // load user
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // fetch order and verify ownership
        return orderRepository.findById(id)
                .filter(order -> order.getUser().getId() == user.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).build());
    }

    // update an order
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id,
                                             @RequestBody UpdateOrderRequest request,
                                             @AuthenticationPrincipal UserDetails userDetails) {

        // load user
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // fetch order and verify ownrership before updating
        return orderRepository.findById(id)
                .filter(order -> order.getUser().getId() == user.getId())
                .map(order -> {
                    order.setDescription(request.getDescription());
                    order.setStatus(request.getStatus());
                    return ResponseEntity.ok(orderRepository.save(order));
                })
                .orElse(ResponseEntity.status(403).build());
    }

    // delete an order
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id,
                                         @AuthenticationPrincipal UserDetails userDetails) {

        // load user
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // fetch order and verify ownrership before deleting
        return orderRepository.findById(id)
                .filter(order -> order.getUser().getId() == user.getId())
                .map(order -> {
                    orderRepository.delete(order);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.status(403).build());
    }

    // DTO for creating a new order
    public static class CreateOrderRequest {
        private String description;
        private String status;

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    // DTO used for updating an existing order
    public static class UpdateOrderRequest {
        private String description;
        private String status;

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
