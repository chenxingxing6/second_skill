package com.lxh.seckill.controller;

import com.lxh.seckill.common.Const;
import com.lxh.seckill.entity.User;
import com.lxh.seckill.param.LoginParam;
import com.lxh.seckill.redis.RedisService;
import com.lxh.seckill.redis.UserKey;
import com.lxh.seckill.result.Result;
import com.lxh.seckill.service.UserService;
import com.lxh.seckill.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * Created by: HuangFuBin
 * Date: 2018/7/9
 * Time: 12:37
 * Such description:
 */
@Controller
@RequestMapping("/user")
public class LoginController {

    @Autowired
    RedisService redisService;
    @Autowired
    UserService userService;
    @RequestMapping("/login")
    @ResponseBody
    public Result<User> doLogin(HttpServletResponse response, HttpSession session , @Valid LoginParam loginParam) {
        Result<User> login = userService.login(loginParam);
        if (login.isSuccess()){
            CookieUtil.writeLoginToken(response,session.getId());
            redisService.set(UserKey.getByName , session.getId() ,login.getData(), Const.RedisCacheExtime.REDIS_SESSION_EXTIME );
        }
        return login;
    }

    @RequestMapping("/logout")
    public String doLogout(HttpServletRequest request, HttpServletResponse response) {
        String token = CookieUtil.readLoginToken(request);
        CookieUtil.delLoginToken(request , response);
        redisService.del(UserKey.getByName , token);
        return "login";
    }
}
