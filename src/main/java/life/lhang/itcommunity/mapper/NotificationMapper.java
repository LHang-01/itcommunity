package life.lhang.itcommunity.mapper;


import life.lhang.itcommunity.mode.Notification;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;


public interface NotificationMapper {

    @Select("select count(*) from notification where receiver = #{receiver}")
    Integer countByExample(Notification notification);

    @Insert("insert into notification (notifier,receiver,outerid, type, gmt_create, status, notifier_name, outer_title) values (#{notifier},#{receiver},#{outerid},#{type},#{gmtCreate},#{status},#{notifierName},#{outerTitle})")
    int insert(Notification record);

    @Select("select * from notification where id = #{id}")
    Notification selectByPrimaryKey(Long id);

    /**
     * 出错的地方
     * @param record
     * @return
     */
    @Update("update notification set status = #{status} where id = #{id}")
    int updateByPrimaryKey(Notification record);

    @Select("select * from notification where receiver = #{receiver} order by gmt_create desc limit #{offset},#{size}")
    List<Notification> selectByExampleWithRowbounds(Map param);

    @Select("select count(*) from notification where receiver = #{receiver} and status = #{status}")
    Long unreadCount(Notification notification);
}