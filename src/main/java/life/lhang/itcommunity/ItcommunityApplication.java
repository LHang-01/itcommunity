package life.lhang.itcommunity;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
@MapperScan("life.lhang.itcommunity.mapper")
// 添加对mapper包扫描https://blog.csdn.net/u013059432/article/details/80239075
//scanBasePackages  = {"life.lhang.itcommunity.controller"}
public class ItcommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItcommunityApplication.class, args);
    }

}
