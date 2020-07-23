package com.lxh.seckill.service;

import com.lxh.seckill.entity.User;
import com.lxh.seckill.param.LoginParam;
import com.lxh.seckill.result.Result;

import java.util.List;

/**
 * My Blog : www.hfbin.cn
 * github: https://github.com/hfbin
 * Created by: HuangFuBin
 * Date: 2018/7/10
 * Time: 12:00
 * Such description:
 */
public interface UserService {
    Result<User> login(LoginParam loginParam);

    List<User> selectAll();
}
