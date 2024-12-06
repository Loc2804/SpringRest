package com.example.laptopshop.service;

import com.example.laptopshop.domain.Job;
import com.example.laptopshop.repository.JobRepository;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.laptopshop.domain.Company;
import com.example.laptopshop.domain.User;
import com.example.laptopshop.domain.response.ResultPaginationDTO;
import com.example.laptopshop.repository.CompanyRepository;
import com.example.laptopshop.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    public CompanyService(CompanyRepository companyRepository,UserRepository userRepository,JobRepository jobRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO handleGetAllCompanies(@Filter Specification<Company> spec, Pageable pageable) {
        Page<Company> companies = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setTotal(companies.getTotalElements());
        meta.setPages(companies.getTotalPages());

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        rs.setMeta(meta);
        rs.setResult(companies.getContent());
        return rs;
    }

    public void DeleteCompany(Long id) {
        Optional<Company> company = this.companyRepository.findById(id);
        if (company.isPresent()) {
            Company com = company.get();
            List<User> users = this.userRepository.findByCompany(com);
            List<Job> jobs = this.jobRepository.findByCompany(com);
            this.jobRepository.deleteAll(jobs);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }

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
    public Optional<Company> findById(long id) {
        Optional<Company> company = this.companyRepository.findById(id);
        return company.isPresent() ? company : Optional.empty();
    }
}
