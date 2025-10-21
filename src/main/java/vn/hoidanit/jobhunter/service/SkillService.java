package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPagination;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean checkName(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill createSkill(Skill s) {
        return this.skillRepository.save(s);
    }

    public Skill fetchById(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        if (skillOptional.isPresent()) {
            return skillOptional.get();
        }
        return null;
    }

    public Skill updateSkill(Skill currentSkill) {
        return this.skillRepository.save(currentSkill);
    }

    public ResultPagination fetchAll(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> page = this.skillRepository.findAll(spec, pageable);
        ResultPagination rs = new ResultPagination();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());
        rs.setMeta(meta);
        rs.setResult(page.getContent());
        return rs;
    }

    public void deleteById(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill currentSkill = skillOptional.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));
        this.skillRepository.delete(currentSkill);
    }

}
