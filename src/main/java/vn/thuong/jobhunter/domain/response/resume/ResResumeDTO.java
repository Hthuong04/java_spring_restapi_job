package vn.thuong.jobhunter.domain.response.resume;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.thuong.jobhunter.util.constant.ResumeStateEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResResumeDTO {
    private long id;
    private String email;
    private String url;
    private ResumeStateEnum status;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private String companyName;
    private JobDTO job;
    private UserDTO user;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobDTO {
        private long id;
        private String name;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        private long id;
        private String name;
    }
}
