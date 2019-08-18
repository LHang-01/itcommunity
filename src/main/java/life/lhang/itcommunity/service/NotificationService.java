package life.lhang.itcommunity.service;

import life.lhang.itcommunity.dto.NotificationDTO;
import life.lhang.itcommunity.dto.PaginationDTO;
import life.lhang.itcommunity.enums.NotificationStatusEnum;
import life.lhang.itcommunity.enums.NotificationTypeEnum;
import life.lhang.itcommunity.mapper.NotificationMapper;
import life.lhang.itcommunity.mode.Notification;
import life.lhang.itcommunity.mode.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by codedrinker on 2019/6/14.
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 最新回复列表分页查询
     * @param userId 当前session中的登录用户id
     * @param page 当前页
     * @param size 页面大小
     * @return
     */
    public PaginationDTO list(Long userId, Integer page, Integer size) {

        //查询数据库中接收者为当前登录用户的通知数量
        Notification notificationExample = new Notification();
        notificationExample.setReceiver(userId);
        //注意：有可能查询出来为空表，此时的page=0
        Integer totalCount = notificationMapper.countByExample(notificationExample);

        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();

        Integer totalPage;
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }

        paginationDTO.setPagination(totalPage, page);

        //如果页面小于1（即查询出来为空表时页面=0），起始条数=0（防止offset计算结果<0）；否则起始数=size * (page - 1)，即当前页的第一条数据编号
        Integer offset = page < 1 ? 0 : size * (page - 1);

        Map param = new HashMap();
        param.put("receiver", userId);
        param.put("offset", offset);
        param.put("size", size);
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(param);

        if (notifications.size() == 0) {
            return paginationDTO;
        }

        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    public Long unreadCount(Long userId) {
        Notification notification = new Notification();
        notification.setReceiver(userId);
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.unreadCount(notification);
    }

    /**
     * 当点击未读通知时，将未读改成已读
     * @param id 通知的id编号
     * @param user 当前登录用户
     * @return
     */
    public NotificationDTO read(Long id, User user) {
        //查询数据库中编号为id的通知是否存在
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null) {
            //throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        //判断通知的接受者是否等于当前登录的用户
        if (!Objects.equals(notification.getReceiver(), user.getId())) {
            //throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        //将此条通知标记为已读
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
