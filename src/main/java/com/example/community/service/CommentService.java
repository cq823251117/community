package com.example.community.service;

import com.example.community.dto.CommentDTO;
import com.example.community.enums.CommentTypeEnum;
import com.example.community.enums.NotificationStatusEnum;
import com.example.community.enums.NotificationTypeEnum;
import com.example.community.exception.CustomizeErrorCode;
import com.example.community.exception.CustomizeException;
import com.example.community.mapper.*;
import com.example.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional
    public void insert(Comment comment, User commentator) {
        if (comment.getParentId()==null||comment.getParentId()==0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType()==null||!CommentTypeEnum.isExist(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if (comment.getType()==CommentTypeEnum.COMMENT.getType()){
//回复评论  dbComment代表存在的评论，就是再对这条评论进行再评论
            Comment dbComment= commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment==null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question==null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
//增加评论数
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentExtMapper.incCommentCount(parentComment);
//创建通知
            createNotify(comment, dbComment.getCommentator(), commentator.getName(),question.getTitle() , NotificationTypeEnum.REPLY_COMMENT, question.getId());
        }else {
//回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question==null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
//创建通知
            createNotify(comment,question.getCreator(), commentator.getName(),question.getTitle(),NotificationTypeEnum.REPLY_QUESTION, question.getId());
        }
    }
//将回复评论和回复问题的时需要创建通知的代码抽离出来
    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationType, Long outerId) {
        Notification notification = new Notification();
        notification.setOuterid(outerId);
        notification.setType(notificationType.getType());
        notification.setReceiver(receiver);
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setNotifier(comment.getCommentator());
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {

        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if (comments.size() == 0)
        {
            return new ArrayList<>();
        }
//        获取去重的评论人id
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList();
        userIds.addAll(commentators);
//        获取评论人详情并转换为Map
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
//        转换 comment 为 commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }
}
