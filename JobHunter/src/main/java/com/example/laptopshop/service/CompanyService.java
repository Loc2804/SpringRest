package com.example.laptopshop.service;

import com.example.laptopshop.domain.dto.Meta;
import com.example.laptopshop.domain.dto.ResultPaginationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.laptopshop.domain.Company;
import com.example.laptopshop.domain.User;
import com.example.laptopshop.repository.CompanyRepository;
import com.example.laptopshop.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public  ResultPaginationDTO handleGetAllCompanies(Specification<Company> spec,Pageable pageable) {
        Page<Company> companies = this.companyRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setTotal(companies.getTotalElements());
        meta.setPages(companies.getTotalPages());

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        rs.setMeta(meta);
        rs.setResult(companies.getContent());
        return rs;
    }

    public void DeleteCompany(Long id) {this.companyRepository.deleteById(id);}

    public Company handleGetCompanyById(long id) {
        Optional<Company> company = this.companyRepository.findById(id);
        if (company.isPresent()) {
            return company.get();
        }
        return null;
    }
    public Company updateCompany(Company company) {
        Company updateCompany = this.handleGetCompanyById(company.getId());
        if (updateCompany != null) {
            updateCompany.setName(company.getName());
            updateCompany.setAddress(company.getAddress());
            updateCompany.setLogo(company.getLogo());
            updateCompany.setDescription(company.getDescription());
            this.companyRepository.save(updateCompany);
        }
        return updateCompany;
    }
}
