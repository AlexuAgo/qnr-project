package com.alex.qnr_project.service;

import com.alex.qnr_project.entity.Order;
import com.alex.qnr_project.entity.User;
import com.alex.qnr_project.repository.OrderRepository;
import com.alex.qnr_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    // 404 if user does not exist
    private User loadUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );
    }

    // 201 semantics handled by controller; service just creates
    public Order createOrder(String username, Order order) {
        User user = loadUser(username);
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    // always scoped to the authenticated user
    public Page<Order> getOrders(String username, String status, Pageable pageable) {
        User user = loadUser(username);

        if (status != null) {
            return orderRepository.findByUserAndStatus(user, status, pageable);
        }
        return orderRepository.findByUser(user, pageable);
    }

    // 404 if order doesn't exist, 403 if it exists but not owned
    public Order getOrder(String username, Long orderId) {
        User user = loadUser(username);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Order not found"
                        )
                );

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not own this order"
            );
        }

        return order;
    }

    // reuses getOrder → ownership + existence already enforced
    public Order updateOrder(String username, Long orderId, String desc, String status) {
        Order order = getOrder(username, orderId);
        order.setDescription(desc);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    // same logic path → consistent errors
    public void deleteOrder(String username, Long orderId) {
        Order order = getOrder(username, orderId);
        orderRepository.delete(order);
    }
}
