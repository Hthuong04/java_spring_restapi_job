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

import io.micrometer.core.instrument.Meter.Id;
import jakarta.validation.Valid;
import vn.thuong.jobhunter.domain.Permission;
import vn.thuong.jobhunter.domain.response.ResultPaginationDTO;
import vn.thuong.jobhunter.service.PermissionService;
import vn.thuong.jobhunter.util.annotation.ApiMessage;
import vn.thuong.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("create a permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException {
        boolean isExistPermission = this.permissionService.checkExistPermission(permission);

        if (isExistPermission) {
            throw new IdInvalidException("Permission đã tồn tại");
        }
        Permission newPermission = this.permissionService.handleCreatePermission(permission);

        return ResponseEntity.status(HttpStatus.CREATED).body(newPermission);
    }

    @PutMapping("/permissions")
    @ApiMessage("update a permission")
    public ResponseEntity<Permission> updatePermission(@RequestBody Permission permission) throws IdInvalidException {
        boolean isExistPermission = this.permissionService.checkExistPermission(permission);

        if (isExistPermission) {
            throw new IdInvalidException("Permission đã tồn tại");
        }

        Permission currentPermission = this.permissionService.fetchPermissionById(permission.getId());
        if (currentPermission == null) {
            throw new IdInvalidException("Permission không tồn tại");
        }
        Permission newPermission = this.permissionService.handleUpdatePermission(permission);
        return ResponseEntity.ok().body(newPermission);
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("delete a permission")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id) throws IdInvalidException {
        Permission currentPermission = this.permissionService.fetchPermissionById(id);
        if (currentPermission == null) {
            throw new IdInvalidException("Permission không tồn tại");
        }

        this.permissionService.handleDeletePermission(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/permissions")
    @ApiMessage("get all permission")
    public ResponseEntity<ResultPaginationDTO> getAllPermission(@Filter Specification<Permission> spec,
            Pageable pageable) {
        ResultPaginationDTO rs = this.permissionService.fetchAllPermission(spec, pageable);

        return ResponseEntity.ok().body(rs);
    }
}
