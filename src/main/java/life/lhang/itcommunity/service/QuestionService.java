package life.lhang.itcommunity.service;

import life.lhang.itcommunity.dto.PaginationDTO;
import life.lhang.itcommunity.dto.QuestionDTO;
import life.lhang.itcommunity.dto.QuestionQueryDTO;
import life.lhang.itcommunity.mapper.QuestionMapper;
import life.lhang.itcommunity.mapper.UserMapper;
import life.lhang.itcommunity.mode.Question;
import life.lhang.itcommunity.mode.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 首页的问题列表
     * @param search
     * @param page
     * @param size
     * @return
     */
    public PaginationDTO list(String search, Integer page, Integer size) {

        //判断搜索框是否为空
        if (StringUtils.isNotBlank(search)) {
            //以空格切分成数组
            String[] tags = StringUtils.split(search, " ");
            search = Arrays.stream(tags).collect(Collectors.joining("|"));
        }

        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);

        //根据标题中是否含有搜索的词语来查询总条数
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

        //如果页面小于1，则从第0条开始查询；否则，从当前页的第一条数据开始查询
        Integer offset = page < 1 ? 0 : size * (page - 1);

        questionQueryDTO.setSize(size);
        questionQueryDTO.setPage(offset);

        //将questionQueryDTO作为参数传递过去查询满足要求的问题了列表
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

    /*
    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();

        Integer totalPage;

        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(questionExample);

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

        //size*(page-1)
        Integer offset = size * (page - 1);
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));
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
    */

    /**
     * 根据问题id查询问题信息，并将创建该问题的用户信息组合为DTO一并返回。
     * @param id
     * @return
     */
    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            //throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    /**
     *根据传入的问题id到数据库查找是否存在该问题编号，
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
                //没找到问题
                //throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            if (dbQuestion.getCreator().longValue() != question.getCreator().longValue()) {
                //数据库中的该问题创建者和目前正在编辑的用户不是同一个人
                //throw new CustomizeException(CustomizeErrorCode.INVALID_OPERATION);
            }

            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setId(question.getId());
            int updated = questionMapper.update(updateQuestion);
            if (updated != 1) {
                //throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }


    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionMapper.incView(question);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        //判断传入的问题DTO是否有标签，如果没有则返回空集合（即表示没有相关的问题）
        if (StringUtils.isBlank(queryDTO.getTag())) {
            return new ArrayList<>();
        }
        //若传入的问题有标签，则将其标签用正则的方式到数据库中进行模糊查询,返回的相关问题列表不应包含本问题
        String[] tags = StringUtils.split(queryDTO.getTag(), ",");
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);
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
