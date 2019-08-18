package life.lhang.itcommunity.dto;

import lombok.Data;

/**
 *NotificationDTO与Notification属性区别：
 * Notification特有接受者receiver，NotificationDTO特有typeName
 */
@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    //状态：已读或未读
    private Integer status;
    //通知者
    private Long notifier;
    //通知者姓名
    private String notifierName;
    //标题、大纲
    private String outerTitle;
    //外键（questionId）
    private Long outerid;
    //类型（文字表示）
    private String typeName;
    //类型（数字表示）
    private Integer type;
}
