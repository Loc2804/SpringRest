package com.example.laptopshop.controller;

import com.example.laptopshop.domain.dto.ResultPaginationDTO;
import com.example.laptopshop.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.laptopshop.domain.Company;
import com.example.laptopshop.domain.User;
import com.example.laptopshop.service.CompanyService;
import com.example.laptopshop.service.UserService;
import com.example.laptopshop.util.error.GlobalException;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    @ApiMessage("Create a company")
    public ResponseEntity<Company> createNewCompany(@Valid @RequestBody Company company) {
        Company response = this.companyService.handleCreateCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("Get a company")
    public ResponseEntity<Company> getCompanyById(@PathVariable("id") long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.handleGetCompanyById(id));
    }
    @GetMapping("/companies")
    @ApiMessage("Fetch all companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompany(@Filter Specification<Company> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.handleGetAllCompanies(spec,pageable));
    }

    @DeleteMapping("/companies/{id}")
    @ApiMessage("Delete a company")
    public ResponseEntity<String> deleteCompany(@PathVariable("id") long id)
    {
        this.companyService.DeleteCompany(id);
        return ResponseEntity.status(HttpStatus.OK).body("Delete company success");
    }

    @PutMapping("/companies")
    @ApiMessage("Update a company")
    public ResponseEntity<Company> UpdateCompany(@Valid @RequestBody Company company) {
        Company company1 = this.companyService.updateCompany(company);
        return ResponseEntity.status(HttpStatus.OK).body(company1);
    }
}
