package life.lhang.itcommunity.mapper;

import life.lhang.itcommunity.dto.QuestionQueryDTO;
import life.lhang.itcommunity.mode.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


import java.util.List;
import java.util.Map;

public interface QuestionMapper{

    @Insert("insert into question (id, title, gmt_create, gmt_modified, creator, comment_count, view_count, like_count, tag, description) values (#{id}, #{title}, #{gmtCreate}, #{gmtModified}, #{creator}, #{commentCount}, #{viewCount}, #{likeCount}, #{tag}, #{description})")
    void insert(Question question);

    @Select("select * from question where id = #{id}")
    Question selectByPrimaryKey(Long id);

    @Update("update question set gmt_modified=#{gmtModified}, title=#{title}, description=#{description},tag=#{tag} where id =#{id}")
    int update(Question updateQuestion);
    

    /**
     * 注意当有几个句子拼接在一起的时候一定要注意空格的拼接
     * 比如"and id != #{id}"+"and tag regexp #{tag}"->"id != #{id}and tag regexp #{tag}"
     *   而"and id != #{id} "+"and tag regexp #{tag}"->"id != #{id} and tag regexp #{tag}"
     * @param question
     * @return
     */
    @Select("<script>"+"select * from question"
            +"<where>"
            +"<if test=\"id != null\">and id != #{id} </if>"
            +"<if test=\"tag != null and tag != ''\">and tag regexp #{tag}</if>"
            +"</where>"
            +"order by gmt_create desc limit 20"
            +"</script>" )
    List<Question> selectRelated(Question question);

    @Update("update question set view_count = view_count + #{viewCount} where id =#{id}")
    void incView(Question question);

    @Update("update question set comment_count = comment_count + #{commentCount} where id =#{id}")
    void incCommentCount(Question question);

    @Select("<script>" +"select count(*) from question"
            +"<where>"
            +"<if test=\"search != null and search != ''\">"
            +"and title regexp #{search}"
            +"</if>"
            +"</where>"
            +"</script>")
    Integer countBySearch(QuestionQueryDTO questionQueryDTO);

    @Select("<script>" +"select * from question "
            +"<where>"
            +"<if test=\"search != null and search != ''\">"
            +"and title regexp #{search}"
            +"</if>"
            +"</where>"
            +"order by gmt_create desc limit #{page},#{size}"
            +"</script>")
    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);

    @Select("select count(*) from question where creator  = #{creator}")
    Integer countByExample(Question questionExample);

    @Select("select * from question where creator = #{userId} limit #{offset},#{size}")
    List<Question> selectByUserIdWithRowbounds(Map param);
}
