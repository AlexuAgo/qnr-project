package com.alex.qnr_project.repository;


import com.alex.qnr_project.entity.Order;
import com.alex.qnr_project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // find all orders of a user with pagination
    Page<Order> findByUser(User user, Pageable pageable);

    // filter orders by status, paginated
    Page<Order> findByUserAndStatus(User user, String status, Pageable pageable);
}
