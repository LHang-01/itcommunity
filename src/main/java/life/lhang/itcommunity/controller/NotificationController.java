package life.lhang.itcommunity.controller;

import life.lhang.itcommunity.dto.NotificationDTO;
import life.lhang.itcommunity.enums.NotificationTypeEnum;
import life.lhang.itcommunity.mode.User;
import life.lhang.itcommunity.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 当点击最新回复页面上的某一条通知（回复）,则进入此方法，返回相关问题的详情页面
     * @param request
     * @param id
     * @return
     */
    @GetMapping("/notification/{id}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "id") Long id) {

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        //查询这条通知的信息
        NotificationDTO notificationDTO = notificationService.read(id, user);

        //当通知的类型是回复的问题还是评论，都返回问题页面
        if (NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()
                || NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType()) {
            return "redirect:/question/" + notificationDTO.getOuterid();
        } else {
            return "redirect:/";
        }
    }
}
