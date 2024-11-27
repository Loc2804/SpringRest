package com.example.laptopshop.service;

import com.example.laptopshop.domain.Company;
import com.example.laptopshop.domain.Skill;
import com.example.laptopshop.domain.response.ResultPaginationDTO;
import com.example.laptopshop.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SkillService {
    private final SkillRepository skillRepository;
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean checkSkill(Skill skill) {
        return this.skillRepository.existsByName(skill.getName());
    }
    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public Skill fetchSkillById(long id) {
        Optional<Skill> skill = this.skillRepository.findById(id);
        if (skill.isPresent()) {
            return skill.get();
        }
        return null;
    }
    public Skill handleUpdateSkill(Skill skill) {
        Skill updatedSkill = fetchSkillById(skill.getId());
        if(updatedSkill != null) {
            updatedSkill.setName(skill.getName());
            this.skillRepository.save(updatedSkill);
        }
        return updatedSkill;
    }

    public void deleteSkillById(long id) {
        //delete data in join_table trước
        Optional<Skill> skill = this.skillRepository.findById(id);
        Skill currentSkill = skill.get();
        currentSkill.getJobs().forEach(j -> j.getSkills().remove(currentSkill));

        this.skillRepository.deleteById(id);
    }

    public ResultPaginationDTO handleGetAllSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> skills = this.skillRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setTotal(skills.getTotalElements());
        meta.setPages(skills.getTotalPages());

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        rs.setMeta(meta);
        rs.setResult(skills.getContent());
        return rs;
    }
}
