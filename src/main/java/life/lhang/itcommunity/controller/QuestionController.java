package life.lhang.itcommunity.controller;


import life.lhang.itcommunity.dto.CommentDTO;
import life.lhang.itcommunity.dto.QuestionDTO;
import life.lhang.itcommunity.enums.CommentTypeEnum;
import life.lhang.itcommunity.exception.CustomizeErrorCode;
import life.lhang.itcommunity.exception.CustomizeException;
import life.lhang.itcommunity.service.CommentService;
import life.lhang.itcommunity.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


/**
 *
 */
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    /**
     * 当用户访问问题详情页面时进入此方法
     * 情况一：点击首页（index.html）上的某一条问题进入
     * 情况二：点击profile.html页面上“我的问题”或“最新回复”列表上的某一条问题进入
     * 情况三：点击question.html页面上“相关问题”列表上的某一条问题进入
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") String id, Model model) {
        Long questionId = null;
        try {
            questionId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            //id无法转换为long型时->非法输入
            throw new CustomizeException(CustomizeErrorCode.INVALID_INPUT);
        }
        //拿到问题和创建者的组合信息对象
        QuestionDTO questionDTO = questionService.getById(questionId);
        //根据上一步返回的问题到数据库中查询有相同标签的问题列表
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);
        //查询出该问题的一级评论
        List<CommentDTO> comments = commentService.listByTargetId(questionId, CommentTypeEnum.QUESTION);
        //累加阅读数
        questionService.incView(questionId);
        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", comments);
        model.addAttribute("relatedQuestions", relatedQuestions);
        return "question";
    }
}
