package life.lhang.itcommunity.controller;

import life.lhang.itcommunity.dto.AccessTokenDTO;
import life.lhang.itcommunity.dto.GithubUser;
import life.lhang.itcommunity.mode.User;
import life.lhang.itcommunity.provider.GithubProvider;
import life.lhang.itcommunity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController{

    @Autowired
    private UserService userService;

    @Autowired
    private GithubProvider githubProvider;

    @Value("589910df180907716fed")
    private String clientId;

    @Value("222901d9ad93739955156be0f5b37e78cd25741b")
    private String clientSecret;

    @Value("http://localhost:11777/callback")
    private String redirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.codeToToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        //判断返回的用户信息是否为空
        if(githubUser !=null&&githubUser.getId()!=null){
            //用户不为空，则创建新用户或者更新用户信息
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatarUrl());
            userService.createOrUpdate(user);
            //并将用户的token放入session中，以便在下一次访问本网站时，取出cookie里面的token去数据库里面查找是否存在这个用户，如果存在，则实现用户的自动登录状态
            //一般自动登录是用的jessesion，但是本项目中用token代替。
            Cookie cookie = new Cookie("token", token);
            cookie.setMaxAge(60 * 60 * 24 * 30 * 6);
            response.addCookie(cookie);
            return "redirect:/";
        }else{
            //log.error("callback get github error,{}", githubUser);
            // 登录失败，重新登录
            return "redirect:/";
        }

    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response) {
        //退出登录时，将session和cookie里面的用户信息删除
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

}
