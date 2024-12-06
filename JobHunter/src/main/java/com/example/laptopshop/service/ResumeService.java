package com.example.laptopshop.service;

import com.example.laptopshop.domain.Company;
import com.example.laptopshop.domain.Job;
import com.example.laptopshop.domain.Resume;
import com.example.laptopshop.domain.User;
import com.example.laptopshop.domain.response.ResultPaginationDTO;
import com.example.laptopshop.domain.response.resume.ResCreateResume;
import com.example.laptopshop.domain.response.resume.ResResumeDTO;
import com.example.laptopshop.domain.response.resume.ResUpdateResume;
import com.example.laptopshop.repository.ResumeRepository;
import com.example.laptopshop.util.SecurityUtil;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserService userService;
    private final JobService jobService;
    private final FilterParser filterParser;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeService(ResumeRepository resumeRepository, UserService userService, JobService jobService, FilterParser filterParserImpl, FilterSpecificationConverter filterSpecificationImpl) {
        this.resumeRepository = resumeRepository;
        this.userService = userService;
        this.jobService = jobService;
        this.filterParser = filterParserImpl;
        this.filterSpecificationConverter = filterSpecificationImpl;
    }

    public ResCreateResume createResume(Resume resume) {
        if(resume.getUser() != null && resume.getJob() != null) {
            Job job = this.jobService.getJobById(resume.getJob().getId());
            User user = this.userService.handleGetUserById(resume.getUser().getId());
            resume.setJob(job);
            resume.setUser(user);
        }
        Resume resumeCreate = this.resumeRepository.save(resume);
        ResCreateResume res = new ResCreateResume();
        res.setId(resumeCreate.getId());
        res.setCreatedBy(resumeCreate.getCreatedBy());
        res.setCreatedAt(resumeCreate.getCreatedAt());
        return res;
    }
    public void deleteResume(Long resumeId) {
        this.resumeRepository.deleteById(resumeId);
    }

    public ResUpdateResume updateResume(Resume resume) {
        Optional<Resume> resumeUpdate = this.resumeRepository.findById(resume.getId());
        ResUpdateResume res = new ResUpdateResume();
        if(resumeUpdate.isPresent()) {
            Resume currentResume = resumeUpdate.get();
            currentResume.setStatus(resume.getStatus());
            Resume updated = this.resumeRepository.save(currentResume);
            res.setUpdatedAt(updated.getUpdatedAt());
            res.setUpdatedBy(updated.getUpdatedBy());
        }
        return res;
    }


    public Resume getResumeByID(Long resumeId) {
        Optional<Resume> resume = this.resumeRepository.findById(resumeId);
        ResResumeDTO resumeDTO = new ResResumeDTO();

        if(resume.isPresent()) {
            return resume.get();
        }
       return null;
    }

    public ResResumeDTO convertResumeToResumeDTO(Resume resume) {
        ResResumeDTO resumeDTO = new ResResumeDTO();
        resumeDTO.setId(resume.getId());
        resumeDTO.setEmail(resume.getEmail());
        resumeDTO.setUrl(resume.getUrl());
        resumeDTO.setStatus(resume.getStatus());
        resumeDTO.setCreatedAt(resume.getCreatedAt());
        resumeDTO.setCreatedBy(resume.getCreatedBy());
        resumeDTO.setUpdatedAt(resume.getUpdatedAt());
        resumeDTO.setUpdatedBy(resume.getUpdatedBy());
        ResResumeDTO.UserResume userResume = new ResResumeDTO.UserResume();
        userResume.setId(resume.getUser().getId());
        userResume.setName(resume.getUser().getName());
        ResResumeDTO.JobResume jobResume = new ResResumeDTO.JobResume();
        jobResume.setId(resume.getJob().getId());
        jobResume.setName(resume.getJob().getName());
        resumeDTO.setJob(jobResume);
        resumeDTO.setUser(userResume);
        if(resume.getJob() != null) {
            resumeDTO.setCompanyName(resume.getJob().getCompany().getName());
        }
        return resumeDTO;
    }

    public ResultPaginationDTO getAllResume(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setTotal(pageResume.getTotalElements());
        meta.setPages(pageResume.getTotalPages());

        rs.setMeta(meta);
        List<ResResumeDTO> resumeDTOList = pageResume.getContent().stream()
                .map(item -> new ResResumeDTO(item.getId(), item.getEmail(), item.getUrl(),
                        item.getStatus(),item.getCreatedAt(),item.getUpdatedAt(),item.getCreatedBy(),item.getUpdatedBy(),
                        item.getUser().getCompany()!= null ? item.getUser().getCompany().getName() : "",
                        new ResResumeDTO.UserResume(
                                item.getUser() != null ? item.getUser().getId(): 0,
                                item.getUser() != null ? item.getUser().getName() : ""),
                        new ResResumeDTO.JobResume(
                                item.getJob() != null ? item.getJob().getId():0,
                                item.getJob() != null ? item.getJob().getName(): "")
                )).collect(Collectors.toList());
        rs.setResult(resumeDTOList);
        return rs;
    }

    public ResultPaginationDTO getResumesByUser( Pageable pageable) {
        //query builder
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get() : "";
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);

        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setTotal(pageResume.getTotalElements());
        meta.setPages(pageResume.getTotalPages());

        rs.setMeta(meta);
        List<ResResumeDTO> resumeDTOList = pageResume.getContent().stream()
                .map(item -> new ResResumeDTO(item.getId(), item.getEmail(), item.getUrl(),
                        item.getStatus(),item.getCreatedAt(),item.getUpdatedAt(),item.getCreatedBy(),item.getUpdatedBy(),
                        item.getUser().getCompany() != null ? item.getUser().getCompany().getName() : "",
                        new ResResumeDTO.UserResume(
                                item.getUser() != null ? item.getUser().getId(): 0,
                                item.getUser() != null ? item.getUser().getName() : ""),
                        new ResResumeDTO.JobResume(
                                item.getJob() != null ? item.getJob().getId():0,
                                item.getJob() != null ? item.getJob().getName(): "")
                )).collect(Collectors.toList());
        rs.setResult(resumeDTOList);
        return rs;
    }
}
