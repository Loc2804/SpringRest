package com.example.laptopshop.controller;

import com.example.laptopshop.domain.Company;
import com.example.laptopshop.domain.Job;
import com.example.laptopshop.domain.Resume;
import com.example.laptopshop.domain.User;
import com.example.laptopshop.domain.response.ResultPaginationDTO;
import com.example.laptopshop.domain.response.resume.ResCreateResume;
import com.example.laptopshop.domain.response.resume.ResResumeDTO;
import com.example.laptopshop.domain.response.resume.ResUpdateResume;
import com.example.laptopshop.repository.ResumeRepository;
import com.example.laptopshop.service.JobService;
import com.example.laptopshop.service.ResumeService;
import com.example.laptopshop.service.UserService;
import com.example.laptopshop.util.SecurityUtil;
import com.example.laptopshop.util.annotation.ApiMessage;
import com.example.laptopshop.util.error.InvalidIdException;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;
    private final JobService jobService;
    private final ResumeRepository resumeRepository;
    private final FilterSpecificationConverter filterSpecificationConverter;
    private final FilterBuilder filterBuilder;

    public ResumeController(ResumeService resumeService, UserService userService, JobService jobService, ResumeRepository resumeRepository, FilterSpecificationConverter filterSpecificationConverter, FilterBuilder filterBuilder) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.jobService = jobService;
        this.resumeRepository = resumeRepository;
        this.filterSpecificationConverter = filterSpecificationConverter;
        this.filterBuilder = filterBuilder;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a new resume")
    public ResponseEntity<ResCreateResume> createResume(@Valid @RequestBody Resume resume) throws InvalidIdException {
        User user = this.userService.handleGetUserById(resume.getUser().getId());
        Job job = this.jobService.getJobById(resume.getJob().getId());
        if(user == null || job == null) {
            throw new InvalidIdException("User not found or Job not found");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.createResume(resume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws InvalidIdException {
       Resume resume = this.resumeService.getResumeByID(id);
        if(resume == null) {
            throw new InvalidIdException("Resume not found");
        }
        this.resumeService.deleteResume(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get resume by Id")
    public ResponseEntity<ResResumeDTO> getResume(@PathVariable("id") long id) throws InvalidIdException {
        Resume resume = this.resumeService.getResumeByID(id);
        if(resume == null) {
            throw new InvalidIdException("Resume not found");
        }
        return ResponseEntity.ok().body(this.resumeService.convertResumeToResumeDTO(resume));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResume> updateResume(@RequestBody Resume resume) throws InvalidIdException {
        Resume resumeFromDB = this.resumeService.getResumeByID(resume.getId());
        if(resumeFromDB == null) {
            throw new InvalidIdException("Resume not found");
        }
        return ResponseEntity.ok().body(this.resumeService.updateResume(resume));
    }

    @GetMapping("/resumes")
    @ApiMessage("Get all resumes")
    public ResponseEntity<ResultPaginationDTO> getAllResumes(@Filter Specification<Resume> spec, Pageable pageable) {
        List<Long> arrJobIds;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : null;
        User currentUser = this.userService.handleGetUserByUsername(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobIds = companyJobs.stream().map(x -> x.getId())
                            .collect(Collectors.toList());
                } else {
                    arrJobIds = null;
                }
            } else {
                arrJobIds = null;
            }
        } else {
            arrJobIds = null;
        }
        System.out.println("arrJobIds: " + arrJobIds);

        Specification<Resume> jobInSpec = (root, query, criteriaBuilder) ->
                root.get("job").get("id").in(arrJobIds);


        Specification<Resume> finalSpec = jobInSpec.and(spec);
        return ResponseEntity.ok().body(this.resumeService.getAllResume(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get resumes by username")
    public  ResponseEntity<ResultPaginationDTO> getResumesByUsername(Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.getResumesByUser(pageable));
    }
}
