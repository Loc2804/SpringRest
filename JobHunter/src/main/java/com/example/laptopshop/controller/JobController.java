package com.example.laptopshop.controller;

import com.example.laptopshop.domain.Job;
import com.example.laptopshop.domain.response.job.ResCreateJobDTO;
import com.example.laptopshop.domain.response.job.ResJobDTO;
import com.example.laptopshop.domain.response.job.ResUpdateJobDTO;
import com.example.laptopshop.domain.response.ResultPaginationDTO;
import com.example.laptopshop.service.JobService;
import com.example.laptopshop.util.annotation.ApiMessage;
import com.example.laptopshop.util.error.InvalidIdException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> create(@Valid @RequestBody Job postJob){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.createJob(postJob));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job")
    public ResponseEntity<Void> delete(@PathVariable("id") long id)throws InvalidIdException {
        Job job = this.jobService.getJobById(id);
        if(job == null){
            throw new InvalidIdException("Job not found");
        }
        this.jobService.deleteJob(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Get a job by id")
    public ResponseEntity<ResJobDTO> getJobById(@PathVariable("id") long id)throws InvalidIdException {
        Job job = this.jobService.getJobById(id);
        if(job == null){
            throw new InvalidIdException("Job not found");
        }
        return ResponseEntity.ok().body(this.jobService.convertToResJobDTO(job));
    }

    @GetMapping("/jobs")
    @ApiMessage("Get all jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(@Filter Specification<Job> spec, Pageable pageable){
        return ResponseEntity.ok().body(this.jobService.getAllJobs(spec, pageable));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job postJob)throws InvalidIdException{
        Job job = this.jobService.getJobById(postJob.getId());
        if(job == null){
            throw new InvalidIdException("Job not found");
        }
        return ResponseEntity.ok().body(this.jobService.updateJob(postJob));
    }
}
