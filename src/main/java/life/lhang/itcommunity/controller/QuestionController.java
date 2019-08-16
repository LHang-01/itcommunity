package life.lhang.itcommunity.controller;


import life.lhang.itcommunity.dto.CommentDTO;
import life.lhang.itcommunity.dto.QuestionDTO;
import life.lhang.itcommunity.enums.CommentTypeEnum;
import life.lhang.itcommunity.service.CommentService;
import life.lhang.itcommunity.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


/**
 * Created by codedrinker on 2019/5/21.
 */
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") String id, Model model) {
        Long questionId = null;
        try {
            questionId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            //throw new CustomizeException(CustomizeErrorCode.INVALID_INPUT);
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
