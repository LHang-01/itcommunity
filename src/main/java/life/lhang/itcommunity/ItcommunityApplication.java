package life.lhang.itcommunity;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages  = {"life.lhang.itcommunity.controller"})
// 添加对mapper包扫描https://blog.csdn.net/u013059432/article/details/80239075
public class ItcommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItcommunityApplication.class, args);
    }

}
