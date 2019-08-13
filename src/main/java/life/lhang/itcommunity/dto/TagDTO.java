package life.lhang.itcommunity.dto;

import lombok.Data;

import java.util.List;

@Data
public class TagDTO {
    //标签类别
    private String categoryName;
    //标签集合
    private List<String> tags;
}
