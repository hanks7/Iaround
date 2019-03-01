package net.iaround.model.entity;

/**
 * Created by yz on 2018/8/14.
 */

public class AuthenticationBean extends BaseEntity {
    public AuthInfo authInfo;
    public class AuthInfo{
        public String realName;
        public String phone;
        public String frontPhoto;//身份证前面
        public String backPhoto;//身份证背面
        public String reason;//原因
        public int status;
    }
}
