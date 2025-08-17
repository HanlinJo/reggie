package com.xh0.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xh0.reggie.common.R;
import com.xh0.reggie.entity.User;
import com.xh0.reggie.service.UserService;
import com.xh0.reggie.utils.MailUtils;
import com.xh0.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user , HttpSession session){
        String email = user.getEmail();
        if(StringUtils.hasText(email)){
            String code =ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code = {}", code);
            //调用发送代码
            //...
//            MailUtils.sendMail(email,"你好，这是一封测试邮件,无需回复，验证码为: "+code,"测试邮件");//填写接收邮箱※
            System.out.println("发送成功");
            session.setAttribute(email,code);
            redisTemplate.opsForValue().set(email,code,5, TimeUnit.MINUTES);
            return R.success("验证码已发送到邮箱");
        }
        return R.error("验证码发送失败");
    }
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        String email = map.get("email").toString();
        String code = map.get("code").toString();

//        Object codeInSession =  session.getAttribute(email);
        Object codeInSession = redisTemplate.opsForValue().get(email);
        if (codeInSession != null && codeInSession.equals(code)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getEmail,email);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            redisTemplate.delete(email);
            return R.success(user);
        }
        return R.error("登录失败");
    }
    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session){
        session.removeAttribute("user");
        return R.error("退出成功");
    }
}
