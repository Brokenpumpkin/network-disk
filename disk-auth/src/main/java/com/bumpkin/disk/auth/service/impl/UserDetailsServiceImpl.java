package com.bumpkin.disk.auth.service.impl;

import com.bumpkin.disk.auth.service.DiskUserService;
import com.bumpkin.disk.entities.DiskUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/12 19:57
 */
@Component("UserDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private DiskUserService diskUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DiskUser diskUser = diskUserService.getUserByUsername(username);
        if(diskUser == null){
            throw new RuntimeException("该账号不存在！");
        }
        return new User(username, diskUser.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));

    }
}
