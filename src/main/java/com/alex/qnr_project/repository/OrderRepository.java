package com.alex.qnr_project.repository;


import com.alex.qnr_project.entity.Order;
import com.alex.qnr_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // find all order of a user
    List<Order> findByUser(User user);

    // find orders by status
    List<Order> findByStatus(String status);
}
