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
import org.springframework.web.context.annotation.ApplicationScope;

import com.turkraft.springfilter.boot.Filter;

import io.micrometer.core.instrument.Meter.Id;
import jakarta.validation.Valid;
import vn.thuong.jobhunter.domain.Permission;
import vn.thuong.jobhunter.domain.Role;
import vn.thuong.jobhunter.domain.response.ResultPaginationDTO;
import vn.thuong.jobhunter.repository.RoleRepository;
import vn.thuong.jobhunter.service.RoleService;
import vn.thuong.jobhunter.util.annotation.ApiMessage;
import vn.thuong.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("create a role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) throws IdInvalidException {
        boolean isExistRole = this.roleService.checkExistRoleName(role.getName());
        if (isExistRole) {
            throw new IdInvalidException("role đã tồn tại");

        }
        Role newRole = this.roleService.handleCreateRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
    }

    @PutMapping("/roles")
    @ApiMessage("update a role")
    public ResponseEntity<Role> updateRole(@RequestBody Role role) throws IdInvalidException {

        Role currentRole = this.roleService.fetchRoleById(role.getId());
        if (currentRole == null) {
            throw new IdInvalidException("role không tồn tại");
        }

        Role upRole = this.roleService.handleUpdateRole(role);
        return ResponseEntity.ok().body(upRole);
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws IdInvalidException {
        Role currentRole = this.roleService.fetchRoleById(id);
        if (currentRole == null) {
            throw new IdInvalidException("role không tồn tại");
        }

        this.roleService.handleDeleteRole(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles")
    @ApiMessage("get all roles")
    public ResponseEntity<ResultPaginationDTO> getAllPermission(@Filter Specification<Role> spec,
            Pageable pageable) {
        ResultPaginationDTO rs = this.roleService.fetchAllRoles(spec, pageable);

        return ResponseEntity.ok().body(rs);
    }

    @GetMapping("roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> getById(@PathVariable("id") long id) throws IdInvalidException {
        Role currentRole = this.roleService.fetchRoleById(id);
        if (currentRole == null) {
            throw new IdInvalidException("role không tồn tại");
        }
        return ResponseEntity.ok().body(currentRole);
    }
}
