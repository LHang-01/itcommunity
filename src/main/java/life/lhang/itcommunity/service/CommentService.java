package life.lhang.itcommunity.service;

import life.lhang.itcommunity.dto.CommentDTO;
import life.lhang.itcommunity.enums.CommentTypeEnum;
import life.lhang.itcommunity.mapper.CommentMapper;
import life.lhang.itcommunity.mapper.QuestionMapper;
import life.lhang.itcommunity.mapper.UserMapper;
import life.lhang.itcommunity.mode.Comment;
import life.lhang.itcommunity.mode.Question;
import life.lhang.itcommunity.mode.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by codedrinker on 2019/5/31.
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    //@Autowired
    //private NotificationMapper notificationMapper;

    /**
     *
     * @param comment
     * @param commentator
     */
    @Transactional
    public void insert(Comment comment, User commentator) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            //评论的父编号为空
            //throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            //评论的类型为空
            //throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            // 回复评论(二级评论)
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {
                //该二级评论评论的一级评论找不到
                //throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }

            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if (question == null) {
                //该二级评论评论的一级评论所评论的问题找不到
                //throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);

            // 增加评论数
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentMapper.incCommentCount(parentComment);

            // 创建通知
            //createNotify(comment, dbComment.getCommentator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT, question.getId());
        } else {
            // 回复问题（一级评论）
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {
                //评论的问题没找到
                //throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            //初始化一级评论的评论数
            comment.setCommentCount(0);
            commentMapper.insert(comment);
            //增加问题的一级评论数
            question.setCommentCount(1);
            questionMapper.incCommentCount(question);

            // 创建通知
            //createNotify(comment, question.getCreator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_QUESTION, question.getId());
        }
    }

    /*
    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationType, Long outerId) {
        if (receiver == comment.getCommentator()) {
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterid(outerId);
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }
    */

    //
    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        //查询数据库中父编号=id并且评论类型=传入的评论类型的评论列表
        Comment commentExample = new Comment();
        commentExample.setId(id);
        commentExample.setType(type.getType());
        List<Comment> comments = commentMapper.selectByExample(commentExample);


        if (comments.size() == 0) {
            return new ArrayList<>();
        }

        // 获取去重的评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList();
        userIds.addAll(commentators);


        // 获取评论人并转换为 Map
        List<User> users = userMapper.selectByExample(userIds);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));


        // 转换 comment 为 commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
