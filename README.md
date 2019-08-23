##资料
[spring guide(官方文档)](https://spring.io/guides/gs/serving-web-content/)

[thymeleafThymeleaf 是 Web 和独立环境的现代服务器端 Java 模板引擎，能够处理HTML，XML，JavaScript，CSS 甚至纯文本。
官方文档](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#setting-attribute-values)

[git官方文档（服务器上的 Git - 生成 SSH 公钥）](https://git-scm.com/book/zh/v1/%E6%9C%8D%E5%8A%A1%E5%99%A8%E4%B8%8A%E7%9A%84-Git-%E7%94%9F%E6%88%90-SSH-%E5%85%AC%E9%92%A5)

[一个用于前端开发的开源工具包Bootstrap](https://v3.bootcss.com/)

[一个非常实用的日期工具类moment.js，日期获取，格式化](http://momentjs.cn/)

[富文本插件Editor.md](https://pandao.github.io/editor.md/)

[Flyway是独立于数据库的应用、管理并跟踪数据库变更的数据库版本管理工具](https://flywaydb.org/getstarted/firststeps/maven#integrating-flyway)

[okhttp官网网址](https://square.github.io/okhttp/)、[github网址](https://github.com/square/okhttp)

[DTO代表服务层需要接收的数据和返回的数据，而VO代表展示层需要显示的数据](https://blog.csdn.net/zjrbiancheng/article/details/6253232)

[Lombok官网](https://projectlombok.org/)

[com.alibaba的fastjson](https://www.cnblogs.com/qiaoyeye/p/7730288.html)

[Spring Boot log](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html)

[腾讯云COS对象存储官方文档](https://cloud.tencent.com/document/product/436/10199)

##项目部署
###工具
- Git
- MAVEN
- JDK

###步骤
- yum update
- yum install git
- yum install maven
- mkdir APP
- cd APP
- git clone https://github.com/LHang-01/itcommunity.git
- mvn clean compile package

###数据库版本管理
- mvn flyway:migrate
