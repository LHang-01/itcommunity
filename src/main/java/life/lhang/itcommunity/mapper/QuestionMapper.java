package life.lhang.itcommunity.mapper;

import life.lhang.itcommunity.mode.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface QuestionMapper{

    @Insert("insert into question (id, title, gmtCreate, gmtModified, creator, commentCount, viewCount, likeCount, tag, description) values (#{id}, #{title}, #{gmtCreate}, #{gmtModified}, #{creator}, #{commentCount}, #{viewCount}, #{likeCount}, #{tag}, #{description})")
    void insert(Question question);

    @Select("select * from question where id = #{id}")
    Question selectByPrimaryKey(Long id);

    @Update("update question set gmtModified=#{gmtModified}, title=#{title}, description=#{description},tag=#{tag} where id =#{id}")
    int update(Question updateQuestion);

}
