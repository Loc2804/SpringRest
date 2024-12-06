package com.example.laptopshop.repository;

import com.example.laptopshop.domain.Company;
import com.example.laptopshop.domain.Job;
import com.example.laptopshop.domain.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    Page<Job> findAll(Specification<Job> spec, Pageable pageable);
    boolean existsByName(String name);
    List<Job> findByCompany(Company company);
    List<Job> findBySkillsIn(List<Skill> skills);
}
