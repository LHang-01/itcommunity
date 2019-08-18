package life.lhang.itcommunity.mode;

import lombok.Data;

@Data
public class Notification {


    private Long id;
    //通知者,
    private Long notifier;
    //接受者question.creator
    private Long receiver;
    //外键（question.id）
    private Long outerid;
    //类型
    private Integer type;
    private Long gmtCreate;
    //状态
    private Integer status;
    //通知者名字commentator.name
    private String notifierName;
    //大纲question.title
    private String outerTitle;

}