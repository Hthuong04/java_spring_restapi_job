package vn.thuong.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.thuong.jobhunter.controller.CompanyController;
import vn.thuong.jobhunter.domain.Company;
import vn.thuong.jobhunter.domain.User;
import vn.thuong.jobhunter.domain.response.ResultPaginationDTO;
import vn.thuong.jobhunter.repository.CompanyRepository;
import vn.thuong.jobhunter.repository.UserRepository;

@Service
public class CompanyService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO fetchAllCompanies(Specification spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageCompany.getContent());
        return rs;
    }

    public Company handleUpdateCompany(Company company) {
        Optional<Company> currentCompany = this.companyRepository.findById(company.getId());
        if (currentCompany.isPresent()) {
            Company updateCompany = currentCompany.get();
            updateCompany.setName(company.getName());
            updateCompany.setAddress(company.getAddress());
            updateCompany.setDescription(company.getDescription());
            updateCompany.setLogo(company.getLogo());
            return this.companyRepository.save(updateCompany);

        }
        return null;
    }

    public void handleDeleteCompany(long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            Company com = companyOptional.get();
            List<User> users = this.userRepository.findByCompany(com);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }

    public Optional<Company> findById(long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        return companyOptional;
    }
}
