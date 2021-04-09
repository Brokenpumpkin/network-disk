package com.bumpkin.disk.file.util;

import com.bumpkin.disk.entities.User;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public class WebUtil {

    public static String getUserNameByRequest(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "null";
//        userName = user.getUserName();
//        if (userName == null) {
//            userName = "null";
//        }
        if(user == null || user.getUserName() == null) {
            return userName;
        }
        return user.getUserName();
    }
}
