package vn.thuong.jobhunter.domain.response;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import vn.thuong.jobhunter.domain.Company;
import vn.thuong.jobhunter.util.SecurityUtil;
import vn.thuong.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant createdAt;
    private CompanyDTO company;

    @Getter
    @Setter
    public static class CompanyDTO {
        private long id;
        private String name;
    }
}
