package life.lhang.itcommunity.dto;

import lombok.Data;

/**
 * 加载首页或点击搜索按钮后，控制层->服务层的传递参数封装成此对象
 */
@Data
public class QuestionQueryDTO {
    //搜索框内容
    private String search;
    //当前页面
    private Integer page;
    //页面大小
    private Integer size;
}
