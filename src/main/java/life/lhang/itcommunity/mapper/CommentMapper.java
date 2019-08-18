package life.lhang.itcommunity.mapper;


import life.lhang.itcommunity.mode.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CommentMapper {

    @Select("select * from comment where parent_id = #{parentId} and type = #{type} order by #{gmtCreate} desc")
    List<Comment> selectByExample(Comment comment);

    @Insert("insert into comment (id, parent_id, type, commentator, gmt_create, gmt_modified, like_count, content, comment_count) values (#{id}, #{parentId}, #{type}, #{commentator}, #{gmtCreate}, #{gmtModified}, #{likeCount}, #{content}, #{commentCount}) ")
    void insert(Comment comment);

    @Select("select * from comment where id = #{id}")
    Comment selectByPrimaryKey(Long parentId);

    @Update("update comment set comment_count = comment_count + #{commentCount} where id =#{id}")
    void incCommentCount(Comment parentComment);
}