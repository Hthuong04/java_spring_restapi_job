package vn.thuong.jobhunter.controller;

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
import vn.thuong.jobhunter.domain.Skill;
import vn.thuong.jobhunter.domain.response.ResultPaginationDTO;
import vn.thuong.jobhunter.service.SkillService;
import vn.thuong.jobhunter.util.annotation.ApiMessage;
import vn.thuong.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("create a skills")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        boolean isSkillExist = this.skillService.isSkillExist(skill.getName());
        if (isSkillExist) {
            throw new IdInvalidException("Skill với tên " + skill.getName() + " đã tồn tại.");
        }
        Skill newsSkill = this.skillService.handleCreateSkill(skill);

        return ResponseEntity.status(HttpStatus.CREATED).body(newsSkill);
    }

    @GetMapping("/skills")
    @ApiMessage("get all skill")
    public ResponseEntity<ResultPaginationDTO> getAllSkill(@Filter Specification<Skill> spec, Pageable pageable) {
        ResultPaginationDTO skills = this.skillService.fetchAllSkill(spec, pageable);
        return ResponseEntity.ok().body(skills);
    }

    @PutMapping("/skills")
    @ApiMessage("update skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skill)
            throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchSkillById(skill.getId());
        if (currentSkill == null) {
            throw new IdInvalidException("Skill với id" + skill.getId() + " không tồn tại");
        }
        boolean isSkillExist = this.skillService.isSkillExist(skill.getName());
        if (isSkillExist) {
            throw new IdInvalidException("Skill với tên " + skill.getName() + " đã tồn tại.");
        }
        currentSkill.setName(skill.getName());
        return ResponseEntity.ok().body(this.skillService.handleUpdateSkill(currentSkill));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("delete a skill")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchSkillById(id);
        if (currentSkill == null) {
            throw new IdInvalidException("Skill với id " + id + "không tồn tại");
        }
        this.skillService.handleDeleteSkill(id);
        return ResponseEntity.ok().body(null);
    }

}
