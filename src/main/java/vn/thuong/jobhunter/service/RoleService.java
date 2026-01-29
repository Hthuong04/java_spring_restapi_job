package vn.thuong.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.thuong.jobhunter.domain.Permission;
import vn.thuong.jobhunter.domain.Role;
import vn.thuong.jobhunter.domain.response.ResultPaginationDTO;
import vn.thuong.jobhunter.repository.PermissionRepository;
import vn.thuong.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean checkExistRoleName(String name) {
        return this.roleRepository.existsByName(name);

    }

    public Role handleCreateRole(Role role) {
        if (role.getPermissions() != null) {
            List<Long> listPermissions = role.getPermissions().stream()
                    .map(permission -> permission.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(listPermissions);
            role.setPermissions(dbPermissions);

        }
        return this.roleRepository.save(role);
    }

    public Role fetchRoleById(long id) {
        Optional<Role> currentRole = this.roleRepository.findById(id);
        if (currentRole.isPresent())
            return currentRole.get();
        return null;
    }

    public Role handleUpdateRole(Role role) {
        Role currentRole = this.fetchRoleById(role.getId());

        if (role.getPermissions() != null) {
            List<Long> listPermissions = role.getPermissions().stream()
                    .map(permission -> permission.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(listPermissions);
            role.setPermissions(dbPermissions);

        }

        currentRole.setName(role.getName());
        currentRole.setActive(role.isActive());
        currentRole.setDescription(role.getDescription());
        currentRole.setPermissions(role.getPermissions());

        return this.roleRepository.save(currentRole);

    }

    public void handleDeleteRole(long id) {
        this.roleRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pagePermission = this.roleRepository.findAll(spec, pageable);
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
