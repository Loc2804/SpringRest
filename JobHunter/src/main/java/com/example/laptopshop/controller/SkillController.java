package com.example.laptopshop.controller;

import com.example.laptopshop.domain.Skill;
import com.example.laptopshop.domain.response.ResultPaginationDTO;
import com.example.laptopshop.service.SkillService;
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
public class SkillController {
    private final SkillService skillService;
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create new skill")
    public ResponseEntity<Skill> createNewSkill(@Valid @RequestBody Skill postSkill) throws InvalidIdException {
        boolean isExistSkill = this.skillService.checkSkill(postSkill);
        if (isExistSkill) {
            throw new InvalidIdException("This skill already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.handleCreateSkill(postSkill));
    }

    @PutMapping("/skills")
    @ApiMessage("Update skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill postSkill) throws InvalidIdException {
        Skill skill = this.skillService.handleUpdateSkill(postSkill);
        if(skill == null) {
            throw new InvalidIdException("This skill does not exist");
        }
        return ResponseEntity.ok().body(skill);
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws InvalidIdException {
        Skill skill = this.skillService.fetchSkillById(id);
        if(skill == null) {
            throw new InvalidIdException("This skill does not exist");
        }
        this.skillService.deleteSkillById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/skills")
    @ApiMessage("Get all skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(@Filter Specification<Skill> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.skillService.handleGetAllSkill(spec, pageable));
    }
}
