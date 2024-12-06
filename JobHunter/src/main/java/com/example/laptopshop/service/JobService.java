package com.example.laptopshop.service;

import com.example.laptopshop.domain.Company;
import com.example.laptopshop.domain.Job;
import com.example.laptopshop.domain.Skill;
import com.example.laptopshop.domain.response.job.ResCreateJobDTO;
import com.example.laptopshop.domain.response.job.ResJobDTO;
import com.example.laptopshop.domain.response.job.ResUpdateJobDTO;
import com.example.laptopshop.domain.response.ResultPaginationDTO;
import com.example.laptopshop.domain.response.resume.ResResumeDTO;
import com.example.laptopshop.repository.JobRepository;
import com.example.laptopshop.repository.SkillRepository;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyService companyService;
    public JobService(JobRepository jobRepository,SkillRepository skillRepository,CompanyService companyService) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyService = companyService;
    }

    public boolean isExistJobName(String jobName) {
        return this.jobRepository.existsByName(jobName);
    }
    public ResCreateJobDTO createJob(Job job) {
        if(job.getCompany() !=null){
            Optional<Company> company = this.companyService.findById(job.getCompany().getId());
            job.setCompany(company.isPresent()?company.get():null);
        }
        if(job.getSkills() != null){
            List<Long> reqSkills = job.getSkills().stream()
                    .map(x -> x.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findAllById(reqSkills);
            job.setSkills(dbSkills);
        }
        Job currentJob = this.jobRepository.save(job);

        return convertToResCreateJobDTO(currentJob);
    }

    public ResCreateJobDTO convertToResCreateJobDTO(Job job){
        ResCreateJobDTO jobDTO = new ResCreateJobDTO();
        jobDTO.setSalary(job.getSalary());
        jobDTO.setQuantity(job.getQuantity());
        jobDTO.setDescription(job.getDescription());
        jobDTO.setLocation(job.getLocation());
        jobDTO.setLevel(job.getLevel());
        jobDTO.setStartDate(job.getStartDate());
        jobDTO.setEndDate(job.getEndDate());
        jobDTO.setActive(job.isActive());
        jobDTO.setCreatedAt(job.getCreatedAt());
        jobDTO.setCreatedBy(job.getCreatedBy());
        jobDTO.setId(job.getId());
        jobDTO.setName(job.getName());
        if (job.getSkills() != null) {
            List<String> skills = job.getSkills().stream().map(x -> x.getName()).collect(Collectors.toList());
            jobDTO.setSkills(skills);
        }
        return jobDTO;
    }

    public ResJobDTO convertToResJobDTO(Job job){
        ResJobDTO jobDTO = new ResJobDTO();
        jobDTO.setSalary(job.getSalary());
        jobDTO.setQuantity(job.getQuantity());
        jobDTO.setLocation(job.getLocation());
        jobDTO.setLevel(job.getLevel());
        jobDTO.setStartDate(job.getStartDate());
        jobDTO.setDescription(job.getDescription());
        jobDTO.setEndDate(job.getEndDate());
        jobDTO.setActive(job.isActive());
        jobDTO.setCreatedAt(job.getCreatedAt());
        jobDTO.setCreatedBy(job.getCreatedBy());
        jobDTO.setId(job.getId());
        jobDTO.setName(job.getName());
        jobDTO.setUpdatedAt(job.getUpdatedAt());
        jobDTO.setUpdatedBy(job.getUpdatedBy());
        if (job.getSkills() != null) {
            List<ResJobDTO.SkillsJob> skills = job.getSkills().stream().map(x -> new ResJobDTO.SkillsJob(x.getId(), x.getName())).collect(Collectors.toList());
            jobDTO.setSkills(skills);
        }
        if(job.getCompany() != null){
            jobDTO.setCompany(job.getCompany());
        }
        return jobDTO;
    }

    public ResUpdateJobDTO convertToResUpdateJobDTO(Job job){
        ResUpdateJobDTO jobDTO = new ResUpdateJobDTO();
        jobDTO.setSalary(job.getSalary());
        jobDTO.setQuantity(job.getQuantity());
        jobDTO.setDescription(job.getDescription());
        jobDTO.setLocation(job.getLocation());
        jobDTO.setLevel(job.getLevel());
        jobDTO.setStartDate(job.getStartDate());
        jobDTO.setEndDate(job.getEndDate());
        jobDTO.setActive(job.isActive());
        jobDTO.setId(job.getId());
        jobDTO.setName(job.getName());
        jobDTO.setUpdatedAt(job.getUpdatedAt());
        jobDTO.setUpdatedBy(job.getUpdatedBy());
        if (job.getSkills() != null) {
            List<String> skills = job.getSkills().stream().map(x -> x.getName()).collect(Collectors.toList());
            jobDTO.setSkills(skills);
        }
        return jobDTO;
    }
    public ResUpdateJobDTO updateJob(Job job) {
        Job currentJob = this.getJobById(job.getId());
        if(job.getCompany() !=null) {
            Optional<Company> company = this.companyService.findById(job.getCompany().getId());
            job.setCompany(company.isPresent() ? company.get() : null);
            currentJob.setCompany(company.isPresent()?company.get():null);
        }
        if(job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills().stream()
                    .map(x -> x.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findAllById(reqSkills);
            job.setSkills(dbSkills);
            currentJob.setSalary(job.getSalary());
            currentJob.setQuantity(job.getQuantity());
            currentJob.setDescription(job.getDescription());
            currentJob.setLocation(job.getLocation());
            currentJob.setLevel(job.getLevel());
            currentJob.setStartDate(job.getStartDate());
            currentJob.setEndDate(job.getEndDate());
            currentJob.setActive(job.isActive());
            currentJob.setName(job.getName());
            currentJob.setSkills(dbSkills);
        }
        Job savedJob = this.jobRepository.save(currentJob);
        return convertToResUpdateJobDTO(savedJob);
    }

    public Job getJobById(Long id) {
        Optional<Job> job = this.jobRepository.findById(id);
        if(job.isPresent()){
            return job.get();
        }
        return null;
    }
    public ResultPaginationDTO getAllJobs(@Filter Specification<Job> specification, Pageable pageable) {
        Page<Job> jobs = this.jobRepository.findAll(specification, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setTotal(jobs.getTotalElements());
        meta.setPages(jobs.getTotalPages());

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        rs.setMeta(meta);
        rs.setResult(jobs.getContent());
        return rs;
    }

    public void deleteJob(long id) {
        this.jobRepository.deleteById(id);
    }
}
