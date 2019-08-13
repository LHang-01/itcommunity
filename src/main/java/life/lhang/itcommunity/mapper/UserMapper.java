package life.lhang.itcommunity.mapper;

import life.lhang.itcommunity.mode.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserMapper {

    @Insert("insert into user (account_id, name,token,gmt_create,gmt_modified,avatar_url)values (#{accountId},#{name},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    void insert(User user);

    @Select("select * from user where token=#{token}")
    List<User> findByToken(@Param("token") String token);

    @Select("select * from user where account_id =#{accountId}")
    List<User> findByAccountId(@Param("accountId")String accountId);

    @Update("update user set name =#{name},token = #{token}, gmt_modified = #{gmtModified},avatar_url=#{avatarUrl} where id =#{id}")
    void update(User user);

    @Select("select * from user where id = #{id}")
    User selectByPrimaryKey(Long creator);
}
