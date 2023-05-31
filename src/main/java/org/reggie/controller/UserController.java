package org.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.reggie.common.R;
import org.reggie.pojo.User;
import org.reggie.service.UserService;
import org.reggie.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机验证码
     * @param user json里有一个phone参数，手机号
     * @param session 生成的验证码保存到session与用户提供的验证码比对校验
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        // 获取手机号，判断非空
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //生成4位验证码，并保存到session
            String  code = ValidateCodeUtils.generateValidateCode(4).toString();
            session.setAttribute(phone, code); //key: phone, value: code
            log.info("验证码：" + code);
            //调用阿里云API的SMS短信验证码发送服务
//            SMSUtils.sendMessage("瑞吉外卖","",phone,code);
            return R.success("短信验证码发送成功");
        }

        return R.error("短信发送失败");
    }

    /**
     * 处理收到验证码的登录请求，
     * @param map 接收phone和code
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info("登录手机号和验证码：" + map.toString());
        // 获取手机号，判断非空
        String phone = map.get("phone").toString();
        // 获取页面提交的验证码
        String code = map.get("code").toString();

        // 从Session中获取保存的验证码，并比对
        Object codeSession = session.getAttribute(phone);
        if (codeSession != null && codeSession.equals(code)) {
            // 登陆成功后查询并判断是否在用户表里，否则是新用户，保存在用户表，自动注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());

            return R.success(user);
        }

        return R.error("登录失败");
    }
}
