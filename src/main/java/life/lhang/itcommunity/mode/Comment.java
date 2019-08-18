package life.lhang.itcommunity.mode;

import lombok.Data;

@Data
public class Comment {

    //评论编号，自增
    private Long id;

    //一级评论或二级评论所属的问题编号(question.id)
    private Long parentId;

    //评论类型，1表示评论问题（一级评论），2表示评论评论（二级评论）
    private Integer type;

    //评论者，也就是这条评论的创建者
    private Long commentator;

    //创建时间
    private Long gmtCreate;

    //修改时间
    private Long gmtModified;

    //点赞数
    private Long likeCount;

    //评论内容
    private String content;

    //评论数量（一级评论才有值）
    private Integer commentCount;


}