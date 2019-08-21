package life.lhang.itcommunity.service;

import life.lhang.itcommunity.dto.PaginationDTO;
import life.lhang.itcommunity.dto.QuestionDTO;
import life.lhang.itcommunity.dto.QuestionQueryDTO;
import life.lhang.itcommunity.exception.CustomizeErrorCode;
import life.lhang.itcommunity.exception.CustomizeException;
import life.lhang.itcommunity.mapper.QuestionMapper;
import life.lhang.itcommunity.mapper.UserMapper;
import life.lhang.itcommunity.mode.Question;
import life.lhang.itcommunity.mode.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 首页的问题列表
     * @param search 搜索框输入的内容
     * @param page 当前页
     * @param size 页面大小
     * @return
     */
    public PaginationDTO list(String search, Integer page, Integer size) {

        //判断搜索框是否为空
        if (StringUtils.isNotBlank(search)) {
            //以空格切分成数组，再用"|"连接起来，方便正则运算
            String[] tags = StringUtils.split(search, " ");
            //因为在+、*、？在正则sql查询中会出现异常，所以查询时简单替换了
            search = Arrays
                    .stream(tags)
                    .filter(StringUtils::isNotBlank)
                    .map(t ->  t.replace("+", "").replace("*", "").replace("?", ""))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining("|"));
        }

        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);

        //根据标题中是否含有搜索的词语来查询总条数，有可能查询出来为空表，此时的page=0
        Integer totalCount = questionMapper.countBySearch(questionQueryDTO);

        PaginationDTO paginationDTO = new PaginationDTO();

        //总页数
        Integer totalPage;
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1) {
            //若当前页面<1，为防止出错，则当前页=第一页
            page = 1;
        }
        if (page > totalPage) {
            //若当前页面>总页数，为防止出错，则当前页=最后页
            page = totalPage;
        }

        paginationDTO.setPagination(totalPage, page);

        //如果页面小于1（即查询出来为空表时页面=0），起始条数=0（防止offset计算结果<0）；否则起始数=size * (page - 1)，即当前页的第一条数据编号
        Integer offset = page < 1 ? 0 : size * (page - 1);

        questionQueryDTO.setSize(size);
        questionQueryDTO.setPage(offset);

        //将questionQueryDTO作为参数传递过去查询满足要求的问题分页列表
        List<Question> questions = questionMapper.selectBySearch(questionQueryDTO);
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    /**
     * 查询用户id = userId 的 page 页信息，页面大小为size
     * @param userId 用户id
     * @param page 分页查询的起始条
     * @param size 页面大小
     * @return
     */
    public PaginationDTO list(Long userId, Integer page, Integer size) {

        //查询userid用户的发布问题总数
        Question questionExample = new Question();
        questionExample.setCreator(userId);
        //有可能查询出来没数据，也就是空表
        Integer totalCount = questionMapper.countByExample(questionExample);

        PaginationDTO paginationDTO = new PaginationDTO();

        //总页数
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

        //当当前用户没提过问时，即page=0，offset=-5,为了避免这种情况，如果页面小于1（即查询出来为空表时页面=0），
        //则起始条数=0（防止offset计算结果<0）；否则起始数=size * (page - 1)，即当前页的第一条数据编号
        Integer offset = page < 1 ? 0 : size * (page - 1);
        Map param = new HashMap();
        param.put("userId", userId);
        param.put("offset", offset);
        param.put("size", size);
        List<Question> questions = questionMapper.selectByUserIdWithRowbounds(param);
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }


    /**
     * 根据问题id查询问题信息，并将创建该问题的用户信息组合为DTO一并返回。
     * @param id
     * @return
     */
    public QuestionDTO getById(Long id) {
        //根据问题id查询问题信息
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            //数据库中不存在该问题->你找的问题不在了，要不要换个试试？
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        //根据问题的创建者id查询用户信息
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    /**
     * 根据传入的问题id到数据库查找是否存在该问题编号，
     * 如果存在，则说明属于修改原问题，直接update更新操作
     * 不存在，则说明这是初次创建问题，直接insert插入操作
     * @param question
     */
    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            // 创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        } else {
            // 更新
            Question dbQuestion = questionMapper.selectByPrimaryKey(question.getId());
            if (dbQuestion == null) {
                //没找到问题->你找的问题不在了，要不要换个试试？
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            if (dbQuestion.getCreator().longValue() != question.getCreator().longValue()) {
                //数据库中的该问题创建者和目前正在编辑的用户不是同一个人->兄弟，是不是走错房间了？
                throw new CustomizeException(CustomizeErrorCode.INVALID_OPERATION);
            }

            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setId(question.getId());
            int updated = questionMapper.update(updateQuestion);
            if (updated != 1) {
                //更新id=updateQuestion.id的问题出错->你找的问题不在了，要不要换个试试？
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    /**
     * 增加问题的浏览数
     * @param id
     */
    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionMapper.incView(question);
    }

    /**
     * 查找与传入的问题具有相同标签的问题List
     * @param queryDTO
     * @return
     */
    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        //判断传入的问题DTO是否有标签，如果没有则返回空集合（即表示没有相关的问题）
        if (StringUtils.isBlank(queryDTO.getTag())) {
            return new ArrayList<>();
        }
        //若传入的问题有标签，则将其标签用正则的方式到数据库中进行模糊查询,返回的相关问题列表不应包含本问题
        String[] tags = StringUtils.split(queryDTO.getTag(), ",");
        //因为在+、*、？在正则sql查询中会出现异常，所以查询时简单删除了特殊符号
        String regexpTag = Arrays
                .stream(tags)
                .filter(StringUtils::isNotBlank)
                .map(t -> t.replace("+", "").replace("*", "").replace("?", ""))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);
        //此处正则模糊查询时可能出错
        List<Question> questions = questionMapper.selectRelated(question);

        //将模糊查询返回的相关问题（question）列表，转换为questionDTO列表
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
