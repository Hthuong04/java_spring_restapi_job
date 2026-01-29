package vn.thuong.jobhunter.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.thuong.jobhunter.domain.Company;
import vn.thuong.jobhunter.domain.Role;
import vn.thuong.jobhunter.domain.User;
import vn.thuong.jobhunter.domain.response.ResCreateUserDTO;
import vn.thuong.jobhunter.domain.response.ResUpdateUserDTO;
import vn.thuong.jobhunter.domain.response.ResUserDTO;
import vn.thuong.jobhunter.domain.response.ResultPaginationDTO;
import vn.thuong.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User handleCreateUser(User user) {
        // check company
        if (user.getCompany() != null) {
            Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }
        if (user.getRole() != null) {
            Role role = this.roleService.fetchRoleById(user.getRole().getId());
            user.setRole(role != null ? role : null);
        }
        return this.userRepository.save(user);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToCreateUserDTO(User user) {
        ResCreateUserDTO cr = new ResCreateUserDTO();
        cr.setId(user.getId());
        cr.setAddress(user.getAddress());
        cr.setAge(user.getAge());
        cr.setEmail(user.getEmail());
        cr.setGender(user.getGender());
        cr.setName(user.getName());
        cr.setCreatedAt(user.getCreatedAt());

        if (user.getCompany() != null) {
            ResCreateUserDTO.CompanyDTO companyDTO = new ResCreateUserDTO.CompanyDTO();
            companyDTO.setId(user.getCompany().getId());
            companyDTO.setName(user.getCompany().getName());
            cr.setCompany(companyDTO);
        }

        return cr;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO cr = new ResUserDTO();

        if (user.getCompany() != null) {
            ResUserDTO.CompanyDTO companyDTO = new ResUserDTO.CompanyDTO();
            companyDTO.setId(user.getCompany().getId());
            companyDTO.setName(user.getCompany().getName());
            cr.setCompany(companyDTO);
        }
        if (user.getRole() != null) {
            ResUserDTO.RoleUserDTO roleUser = new ResUserDTO.RoleUserDTO();
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            cr.setRole(roleUser);
        }
        cr.setId(user.getId());
        cr.setAddress(user.getAddress());
        cr.setAge(user.getAge());
        cr.setEmail(user.getEmail());
        cr.setGender(user.getGender());
        cr.setName(user.getName());
        cr.setCreatedAt(user.getCreatedAt());
        cr.setUpdateAt(user.getUpdatedAt());
        return cr;
    }

    public ResultPaginationDTO fetchAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        rs.setMeta(meta);
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        // List<ResUserDTO> listUser = new ArrayList<>();

        // for (User item : pageUser.getContent()) {
        // ResUserDTO dto = new ResUserDTO(
        // item.getId(),
        // item.getEmail(),
        // item.getName(),
        // item.getGender(),
        // item.getAddress(),
        // item.getAge(),
        // item.getUpdatedAt(),
        // item.getCreatedAt());
        // listUser.add(dto);
        // }
        rs.setResult(listUser);
        return rs;
    }

    public User fetchUserById(long id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    public User handleUpdateUser(User user) {
        User currentUser = this.fetchUserById(user.getId());
        if (currentUser != null) {
            currentUser.setAddress(user.getAddress());
            currentUser.setName(user.getName());
            currentUser.setAge(user.getAge());
            currentUser.setGender(user.getGender());

            if (user.getCompany() != null) {
                Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
                currentUser.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
            }
            if (user.getRole() != null) {
                Role role = this.roleService.fetchRoleById(user.getRole().getId());
                currentUser.setRole(role != null ? role : null);
            }
            this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setUpdateAt(user.getUpdatedAt());

        if (user.getCompany() != null) {
            ResUpdateUserDTO.CompanyDTO companyDTO = new ResUpdateUserDTO.CompanyDTO();
            companyDTO.setId(user.getCompany().getId());
            companyDTO.setName(user.getCompany().getName());
            res.setCompany(companyDTO);
        }
        return res;
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
