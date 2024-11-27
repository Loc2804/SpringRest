package com.example.laptopshop.repository;

import com.example.laptopshop.domain.Company;
import com.example.laptopshop.domain.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository  extends JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill> {
    Skill findByName(String name);
    boolean existsByName(String name);
    Page<Skill> findAll(Specification<Skill> spec, Pageable pageable);
    List<Skill> findByIdIn(List<Long> id);
}
