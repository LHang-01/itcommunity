package life.lhang.itcommunity.advice;

import com.alibaba.fastjson.JSON;
import life.lhang.itcommunity.dto.ResultDTO;
import life.lhang.itcommunity.exception.CustomizeErrorCode;
import life.lhang.itcommunity.exception.CustomizeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *  统一的异常处理方法：一般会将dao,service层的异常均向上抛到controller层。
 *  springboot提供了@ControllerAdvice注解，用于统一处理controller层异常；
 *  此外：springboot还提供了ErrorController类来处理所有的异常，包括未进入控制器的错误，比如404,401等错误
 *  springboot官方文档：https://docs.spring.io/spring-boot/docs/2.1.7.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-error-handling
 *
 * 在SpringBoot应用中使用统一异常处理的两种实现方式：（参考https://www.e-learn.cn/content/qita/2345995）
 * 第一种：使用@ControllerAdvice和@ExceptionHandler注解(https://blog.csdn.net/kinginblue/article/details/70186586)
 *               注解@ControllerAdvice表示这是一个控制器增强类，当控制器发生异常且符合类中定义的拦截异常类，将会被拦截。
 *               注解ExceptionHandler定义拦截的异常类
 *         特点：注解@ControllerAdvice方式只能处理控制器抛出的异常。此时请求已经进入控制器中。
 *
 * 第二种: 使用ErrorController类来实现。
 *               getErrorPath()返回的路径，服务器将会重定向到该路径对应的处理类，本例中为error方法。
 *         特点：类ErrorController方式可以处理所有的异常，包括未进入控制器的错误，比如404,401等错误
 *
 * 如果应用中两者共同存在，则@ControllerAdvice方式处理控制器抛出的异常，类ErrorController方式未进入控制器的异常。
 *
 * 此外：@ControllerAdvice方式可以定义多个拦截方法，拦截不同的异常类，并且可以获取抛出的异常信息，自由度更大。
 *
 */
@ControllerAdvice
@Slf4j
public class CustomizeExceptionHandler {
    //用@ExceptionHandler注解修饰的方法，会将对应的异常交给对应的方法处理，即会将控制层所有异常让handle去处理
    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable e, Model model, HttpServletRequest request, HttpServletResponse response) {
        String contentType = request.getContentType();
        if ("application/json".equals(contentType)) {
            ResultDTO resultDTO;
            // 返回 JSON
            if (e instanceof CustomizeException) {
                //如果异常不属于我自定义/预测的异常，设置相应的异常信息
                resultDTO = ResultDTO.errorOf((CustomizeException) e);
            } else {
                //异常属于我自定义/预测的异常，设置相应的异常信息返回给前台，并且需要将其打印到日志中
                log.error("handle error", e);
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
            }
            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ioe) {
            }
            return null;
        } else {
            // 错误页面跳转
            if (e instanceof CustomizeException) {
                //异常不是我自定义/预测的异常，而是如service、dao等向上抛出的异常（非预测），设置抛出的异常信息，返回给前台
                model.addAttribute("message", e.getMessage());
            } else {
                //如果异常属于我自定义/预测的异常，需要将其打印到日志中
                log.error("handle error", e);
                model.addAttribute("message", CustomizeErrorCode.SYS_ERROR.getMessage());
            }
            return new ModelAndView("error");
        }
    }
}
