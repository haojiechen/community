package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author chen
 */
@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private Producer kaptchaProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/register")
    public String getRegisterPage(){
        return "/site/register";
    }
    @GetMapping("/login")
    public String getLoginPage(){
        return "/site/login";
    }


    @PostMapping("/register")
    public String register(Model model, User user){
        Map<String,Object> map = userService.register(user);
        if (map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活");
            model.addAttribute("url","/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    @GetMapping("/activation/{id}/{code}")
    public String activation(Model model,@PathVariable("id") int id,@PathVariable("code") String code){
        int result = userService.activation(id,code);
        if (result == ACTIVATION_SUCCESS)
        {
            model.addAttribute("msg","激活成功，您的账号可以正常使用");
            model.addAttribute("url","/login");
        }else if(result == ACTIVATION_REPEAT)
        {
            model.addAttribute("msg","无效操作，该账号已经激活过了!!");
            model.addAttribute("url","/index");
        }else{
            model.addAttribute("msg","激活失败，您提供的激活码不正确！");
            model.addAttribute("url","/index");
        }
        return "/site/operate-result";
    }
//    @GetMapping("/kaptcha")
//    public void getKaptcha(HttpServletResponse response, HttpSession session){
//        //生成验证码
//        String text= kaptchaProducer.createText();
//        BufferedImage image = kaptchaProducer.createImage(text);
//        //将验证码存入session
//        session.setAttribute("kaptcha",text);
//        //将图片输出给浏览器
//        response.setContentType("image/png");
//        try {
//           OutputStream outputStream = response.getOutputStream();
//            ImageIO.write(image,"png",outputStream);
//
//        } catch (IOException e) {
//            logger.error("响应验证码失败",e.getMessage());
//            e.printStackTrace();
//        }
//    }

    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response){
        //生成验证码
        String text= kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //生成验证码的归属凭证
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie =  new Cookie("kapthcaOwner" , kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);

        //将验证码存入Redis
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(kaptchaKey,text,60, TimeUnit.SECONDS);

        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
           OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"png",outputStream);

        } catch (IOException e) {
            logger.error("响应验证码失败",e.getMessage());
            e.printStackTrace();
        }
    }

//    @PostMapping("/login")
//    public String login(String username,String password,String code,boolean rememberMe,
//                        Model model, HttpSession session, HttpServletResponse response){
//        String kaptcha = (String) session.getAttribute("kaptcha");
//        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
//            model.addAttribute("codeMsg","验证码不正确！");
//            return "/site/login";
//        }
//
//        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
//        Map<String,Object> map = userService.login(username,password,expiredSeconds);
//        if (map.containsKey("ticket")){
//            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
//            cookie.setPath(contextPath);
//            cookie.setMaxAge(expiredSeconds);
//            response.addCookie(cookie);
//            return "redirect:/index";
//        }else{
//            model.addAttribute("usernameMsg",map.get("usernameMsg"));
//            model.addAttribute("passwordMsg", map.get("passwordMsg"));
//
//            return "/site/login";
//        }
//    }
    @PostMapping("/login")
    public String login(String username,String password,String code,boolean rememberMe,
                        Model model, HttpServletResponse response,@CookieValue("kapthcaOwner") String kaptchaOwner){
        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)){
            String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确！");
            return "/site/login";
        }

        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map = userService.login(username,password,expiredSeconds);
        if (map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));

            return "/site/login";
        }
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);

        return "redirect:/login";
    }
}


