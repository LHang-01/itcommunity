package life.lhang.itcommunity.mode;

import lombok.Data;

@Data
public class User {
    //id自增
    private Long id;

    //用户id,作为用户的唯一标识
    private String accountId;

    //姓名
    private String name;

    //用于判断用户是否登录
    private String token;

    // 创建时间
    private Long gmtCreate;

    //修改时间
    private Long gmtModified;

    //用户的描述信息
    private String bio;

    //用户头像地址
    private String avatarUrl;
}
