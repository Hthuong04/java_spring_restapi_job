package vn.thuong.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.thuong.jobhunter.domain.Job;
import vn.thuong.jobhunter.domain.Resume;
import vn.thuong.jobhunter.domain.User;
import vn.thuong.jobhunter.domain.response.ResultPaginationDTO;
import vn.thuong.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.thuong.jobhunter.domain.response.resume.ResResumeDTO;
import vn.thuong.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.thuong.jobhunter.repository.JobRepository;
import vn.thuong.jobhunter.repository.ResumeRepository;
import vn.thuong.jobhunter.repository.UserRepository;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository,
            JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    public ResCreateResumeDTO handleCreateResume(Resume resume) {
        Resume newResume = this.resumeRepository.save(resume);

        ResCreateResumeDTO dto = new ResCreateResumeDTO();
        dto.setId(newResume.getId());
        dto.setCreatedAt(newResume.getCreatedAt());
        dto.setCreatedBy(newResume.getCreatedBy());

        return dto;
    }

    public boolean checkResumeExistByUserAndJob(Resume resume) {
        if (resume.getUser() == null)
            return false;
        Optional<User> userOptional = this.userRepository.findById(resume.getUser().getId());
        if (userOptional.isEmpty())
            return false;

        if (resume.getJob() == null)
            return false;
        Optional<Job> jobOptional = this.jobRepository.findById(resume.getJob().getId());
        if (jobOptional.isEmpty())
            return false;

        return true;
    }

    public Optional<Resume> fetchById(long id) {
        return this.resumeRepository.findById(id);
    }

    public ResResumeDTO getResume(Resume resume) {
        ResResumeDTO res = new ResResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        if (resume.getJob() != null) {
            res.setCompanyName(resume.getJob().getCompany().getName());
        }
        res.setUser(new ResResumeDTO.UserDTO(resume.getUser().getId(), resume.getUser().getName()));
        res.setJob(new ResResumeDTO.JobDTO(resume.getJob().getId(), resume.getJob().getName()));

        return res;
    }

    public ResUpdateResumeDTO handleUpdateResume(Resume resume) {
        Resume currentResume = this.resumeRepository.save(resume);

        ResUpdateResumeDTO dto = new ResUpdateResumeDTO();

        dto.setUpdatedAt(currentResume.getUpdatedAt());
        dto.setUpdatedBy(currentResume.getUpdatedBy());

        return dto;

    }

    public void deleteResume(long id) {
        this.resumeRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAllResume(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);
        List<ResResumeDTO> listResume = pageResume.getContent()
                .stream().map(item -> this.getResume(item))
                .collect(Collectors.toList());
        rs.setResult(listResume);

        return rs;
    }
}
