package com.nowcoder.community.controller;

import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author chen
 */
@Controller()
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage == null){
            model.addAttribute("error","您还没有选择图片！");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确!");
            return "/site/setting";
        }

        fileName = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath+"/"+fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败："+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常");
        }
        userService.updateHeader(hostHolder.getUser().getId(),domain+contextPath+"/user/header/"+fileName);
        return "redirect:/index";
    }

    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable String fileName, HttpServletResponse response){
        //服务器存放的路径
        fileName = uploadPath + "/" +fileName;

        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/"+suffix);
        try(FileInputStream fis = new FileInputStream(fileName)) {
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int b=0;
            while ((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败："+ e.getMessage());

        }

    }

    @LoginRequired
    @PostMapping("/changePassword")
    public String changePassword(Model model, @CookieValue("ticket") String ticket, String originalPassword, String newPassword){
        User user = hostHolder.getUser();
        String pwd = CommunityUtil.md5(originalPassword+user.getSalt());
        if (pwd.equals(user.getPassword())){
            userService.updatePassword(user.getId(),newPassword);
            userService.logout(ticket);
            return "redirect:/login";
        }
        model.addAttribute("pwdError","您输入的密码不正确！");
        return "/site/setting";
    }

    //个人主页
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable int userId,Model model){
        User user = userService.findUserById(userId);
        if (user ==null){
            throw  new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user",user);
        model.addAttribute("LikeCount",likeService.findUserLikeCount(userId));
        return "/site/profile";
    }
}
