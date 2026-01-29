package vn.thuong.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import vn.thuong.jobhunter.controller.AuthController;
import vn.thuong.jobhunter.domain.Permission;
import vn.thuong.jobhunter.domain.response.ResultPaginationDTO;
import vn.thuong.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {

    private final AuthController authController;
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository, AuthController authController) {
        this.permissionRepository = permissionRepository;
        this.authController = authController;
    }

    public boolean checkExistPermission(Permission permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(),
                permission.getApiPath(),
                permission.getMethod());
    }

    public Permission handleCreatePermission(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission fetchPermissionById(long id) {
        Optional<Permission> currentPermission = this.permissionRepository.findById(id);
        if (currentPermission.isPresent()) {
            return currentPermission.get();
        }
        return null;
    }

    public Permission handleUpdatePermission(Permission permission) {
        Permission currentPermission = this.fetchPermissionById(permission.getId());
        if (currentPermission != null) {
            currentPermission.setName(permission.getName());
            currentPermission.setApiPath(permission.getApiPath());
            currentPermission.setMethod(permission.getMethod());
            currentPermission.setModule(permission.getModule());

            return this.permissionRepository.save(currentPermission);
        }

        return null;
    }

    public void handleDeletePermission(long id) {
        Permission currentPermission = this.fetchPermissionById(id);
        if (currentPermission != null) {
            currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));
            this.permissionRepository.delete(currentPermission);
        }
    }

    public ResultPaginationDTO fetchAllPermission(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pagePermission.getTotalPages());
        mt.setTotal(pagePermission.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pagePermission.getContent());

        return rs;

    }
}
