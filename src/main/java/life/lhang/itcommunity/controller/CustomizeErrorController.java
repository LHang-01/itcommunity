package life.lhang.itcommunity.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 类ErrorController方式可以处理所有的异常，包括未进入控制器的错误，比如404,401等错误
 */
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class CustomizeErrorController implements ErrorController {

    @Override
    public String getErrorPath() {
        return "error";
    }

    /**
     * @RequestMapping 注解中produces 属性作用：设置返回值类型和返回值的字符编码
     */
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)  //TEXT_HTML_VALUE = "text/html"
    public ModelAndView errorHtml(HttpServletRequest request, Model model) {
        //获取报错状态码
        HttpStatus status = getStatus(request);

        if (status.is4xxClientError()) {
            //4xx
            model.addAttribute("message", "你这个请求错了吧，要不然换个姿势？");
        }
        if (status.is5xxServerError()) {
            //5xx
            model.addAttribute("message", "服务冒烟了，要不然你稍后再试试！！！");
        }

        return new ModelAndView("error");
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            //(500, "Internal Server Error")
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
