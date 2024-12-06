package com.example.laptopshop.repository;

import com.example.laptopshop.domain.Company;
import com.example.laptopshop.domain.Job;
import com.example.laptopshop.domain.Subscriber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long>, JpaSpecificationExecutor<Subscriber> {
    Page<Subscriber> findAll(Specification<Subscriber> spec, Pageable pageable);
    boolean existsByEmail(String email);
    boolean existsById(Long id);
    Subscriber findByEmail(String email);
}
