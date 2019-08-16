package life.lhang.itcommunity.dto;

import life.lhang.itcommunity.mode.User;
import lombok.Data;

/**
 * 当查询时，由service->控制层返回的参数是CommentDTO对象
 */

@Data
public class CommentDTO {
    private Long id;
    private Long parentId;
    private Integer type;
    private Long commentator;
    private Long gmtCreate;
    private Long gmtModified;
    private Long likeCount;
    private Integer commentCount;
    private String content;
    private User user;
}
