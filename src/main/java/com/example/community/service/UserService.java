package com.example.community.service;

import com.example.community.mapper.UserMapper;
import com.example.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//此类用于解决同一用户重复登录问题
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {
        User dbuser = userMapper.findByAccountId(user.getAccountId());
        if(dbuser==null){
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else {
            dbuser.setGmtModified(System.currentTimeMillis());
            dbuser.setName(user.getName());
            dbuser.setAvatarUrl(user.getAvatarUrl());
            dbuser.setToken(user.getToken());
            userMapper.update(dbuser);
        }
    }
}
