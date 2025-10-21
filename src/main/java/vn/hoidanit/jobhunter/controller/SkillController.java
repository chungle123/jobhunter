package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.dto.ResultPagination;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.service.errors.IdInvalidException;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("create skill successfully")
    public ResponseEntity<Skill> create(@Valid @RequestBody Skill s) throws IdInvalidException {
        if (s.getName() != null && this.skillService.checkName(s.getName())) {
            throw new IdInvalidException("Name này đã tồn tại!");
        }
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(this.skillService.createSkill(s));
    }

    @PutMapping("/skills")
    @ApiMessage("update skill successfully")
    public ResponseEntity<Skill> update(@Valid @RequestBody Skill s) throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchById(s.getId());
        if (currentSkill == null) {
            throw new IdInvalidException("Skill này không tồn tại!");
        }
        if (s.getName() != null && this.skillService.checkName(s.getName())) {
            throw new IdInvalidException("Name này đã tồn tại!");
        }
        currentSkill.setName(s.getName());
        return ResponseEntity.ok().body(this.skillService.updateSkill(currentSkill));
    }

    @GetMapping("/skills")
    @ApiMessage("get all skill")
    public ResponseEntity<ResultPagination> fetchAll(
            @Filter Specification<Skill> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.skillService.fetchAll(spec, pageable));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("delete skill")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchById(id);
        if (currentSkill == null) {
            throw new IdInvalidException("Skill này không tồn tại!");
        }
        this.skillService.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

}
