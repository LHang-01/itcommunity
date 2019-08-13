package life.lhang.itcommunity.dto;

import life.lhang.itcommunity.mode.User;
import lombok.Data;

@Data
public class QuestionDTO {
    //QuestionDTO=life.lhang.itcommunity.mode.User+life.lhang.itcommunity.mode.Question，即携带用户信息的问题。
    private Long id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Long creator;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private User user;
}
