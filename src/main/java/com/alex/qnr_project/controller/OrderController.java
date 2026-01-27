package com.alex.qnr_project.controller;

import com.alex.qnr_project.dto.CreateOrderRequest;
import com.alex.qnr_project.entity.Order;
import com.alex.qnr_project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Order createOrder(@RequestBody CreateOrderRequest request) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Order order = Order.builder()
                .description(request.getDescription())
                .status(request.getStatus())
                .quantity(request.getQuantity())
                .build();

        return orderService.createOrder(username, order);
    }

    @GetMapping
    public Page<Order> getOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable,
            @RequestParam(required = false) String status) {

        return orderService.getOrders(
                userDetails.getUsername(),
                status,
                pageable
        );
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id,
                          @AuthenticationPrincipal UserDetails userDetails) {
        return orderService.getOrder(userDetails.getUsername(), id);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id,
                             @RequestBody CreateOrderRequest request,
                             @AuthenticationPrincipal UserDetails userDetails) {

        return orderService.updateOrder(
                userDetails.getUsername(),
                id,
                request.getDescription(),
                request.getStatus()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id,
                                         @AuthenticationPrincipal UserDetails userDetails) {

        orderService.deleteOrder(userDetails.getUsername(), id);
        return ResponseEntity.ok().build();
    }

}
