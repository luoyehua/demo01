package com.test.controller;

import com.test.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;

/**
 * 从数据库中动态查询用户信息:
 * 自定义认证和授权
 * 取代的是配置文件中的认证管理器中的写死的用户账号和密码还有权限取代这个代码
 * <security:user name="admin" authorities="ROLE_ADMIN" password="{noop}123456"></security:user>
 * 按照springSecurity的要求提供一个实现UserDetailsService接口的实现类
 */
public class SpringSecurityUserService implements UserDetailsService {
    @Autowired
    private BCryptPasswordEncoder encoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //第一步根据用户名查找用户:从数据库中查出来,权限也是查出来,角色也是查出来
        User user = findUserByName(username);
        //判断用户的账号是否存在;要是不存在的话就直接返回
        if (user == null) {
            return null;
        }

        //如果存在,就是给这个账户设置相应的权限:就是赋予权限的过程
        ArrayList<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("add"));
        list.add(new SimpleGrantedAuthority("delete"));
        list.add(new SimpleGrantedAuthority("update"));
        list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        //创建一个spring的user对象,返回
        org.springframework.security.core.userdetails.User userdetails =
                new org.springframework.security.core.userdetails.User(username, user.getPassword(), list);
        return userdetails;
    }

    public User findUserByName(String username) {
        if ("admin".equals(username)) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(encoder.encode("123456"));//此时还是未加密的时候
            return user;

        }
        return null;
    }
}
