package life.lhang.itcommunity.dto;

import lombok.Data;

/**
 * 创建评论时，由控制层->service层传递的参数是CommentCreateDTO对象
 * Created by codedrinker on 2019/5/30.
 */
@Data
public class CommentCreateDTO {
    private Long parentId;
    private String content;
    private Integer type;
}
