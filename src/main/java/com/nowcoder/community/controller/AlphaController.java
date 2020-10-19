package com.nowcoder.community.controller;

import com.nowcoder.community.util.CommunityUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.plaf.SpinnerUI;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.http.HttpResponse;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello()
    {
        return "Hello";
    }
    @RequestMapping("/http")
    public void http(HttpServletRequest req, HttpServletResponse rps){
        //获取请求
        System.out.println(req.getMethod());
        System.out.println(req.getServletPath());
        Enumeration enumeration = req.getHeaderNames();
        while (enumeration.hasMoreElements()){
            String name = (String) enumeration.nextElement();
            String value = req.getHeader(name);
            System.out.println(name+":"+value);
        }
        System.out.println(req.getParameter("code"));

        //返回响应数据
        rps.setContentType("text/html;charset=utf-8");
        try(PrintWriter writer = rps.getWriter()) {
            writer.write("牛客网");
        }catch (IOException e){
            e.printStackTrace();
        }


    }

    @GetMapping("/students")
    @ResponseBody
    public String getStudents(@RequestParam(required = false,defaultValue = "1") int curPages, int limit)
    {
        System.out.println(curPages);
        System.out.println(limit);
        return "students";
    }

    @GetMapping(path = "/student/{id}")
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    @PostMapping(path = "/student")
    @ResponseBody
    public String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    @GetMapping(path = "/teacher")
    public ModelAndView getTeacher(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","张三");
        modelAndView.addObject("age","123");
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    @GetMapping(path = "/school")
    public String getSchool(Model model){
        model.addAttribute("name","北邮");

        return "/demo/view";
    }

    @GetMapping(path = "/emp")
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> mp = new HashMap<>();
        mp.put("name","张三");
        mp.put("age",12);
        return mp;
    }
    @GetMapping(path = "/emps")
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> mps = new ArrayList<Map<String,Object>>();

        Map<String,Object> mp = new HashMap<>();
        mp.put("name","张三");
        mp.put("age",12);
        mps.add(mp);

        mp.put("name","李四");
        mp.put("age",12);
        mps.add(mp);
        return mps;
    }

    @GetMapping("/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse response){

        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        cookie.setPath("/community/alpha");
        //设置cookie的生存时间（不设置关闭浏览器就会失效）秒
        cookie.setMaxAge(60*10);

        response.addCookie(cookie);
        return "SetCookie";
    }

    @GetMapping("/cookie/get")
    @ResponseBody
    public String getCookie(@CookieValue("code") String cookie,HttpServletRequest request){
        System.out.println();
        return "get cookie";
    }

    @GetMapping("/session/set")
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id","1");
        session.setAttribute("name","Test");
        return "set Session";
    }
    @GetMapping("/session/get")
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    //ajax示例
    @PostMapping("/ajax")
    @ResponseBody
    public String testAjax(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功！");
    }
}
