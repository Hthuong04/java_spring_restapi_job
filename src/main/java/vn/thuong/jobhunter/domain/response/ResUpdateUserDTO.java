package vn.thuong.jobhunter.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.thuong.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant updateAt;
    private CompanyDTO company;

    @Getter
    @Setter
    public static class CompanyDTO {
        private long id;
        private String name;
    }
}
