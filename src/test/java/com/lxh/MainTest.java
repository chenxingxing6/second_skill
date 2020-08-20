package com.lxh;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.lxh.seckill.SeckillApplication;
import com.lxh.seckill.dao.UserMapper;
import com.lxh.seckill.entity.User;
import com.lxh.seckill.service.UserService;
import com.lxh.seckill.service.ipml.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * created by lanxinghua@2dfire.com on 2020/8/20
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SeckillApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MainTest {
    @InjectMocks
    private UserServiceImpl userServiceImpl;
    @Mock // userMapper注入到UserServiceImpl中
    private UserMapper userMapper;
    @Mock // 为接口提供虚拟实现
    private UserService userService;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test01(){
        List<User> result1 = Lists.newArrayList();
        User user1 = new User();
        user1.setUserName("userName1");
        result1.add(user1);
        Mockito.when(userService.selectAll()).thenReturn(result1);

        List<User> result2 = Lists.newArrayList();
        User user2 = Mockito.mock(User.class);
        user2.setUserName("userName2");
        result2.add(user2);
        Mockito.when(userMapper.selectAll()).thenReturn(result2);
        List<User> users1 = userService.selectAll();
        users1.stream().forEach(e->{
            System.out.println(JSON.toJSONString(e));
        });

        List<User> users2 = userServiceImpl.selectAll();
        System.out.println(users2.size());
    }
}
