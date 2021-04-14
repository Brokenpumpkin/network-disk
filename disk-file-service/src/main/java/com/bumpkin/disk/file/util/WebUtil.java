package com.bumpkin.disk.file.util;

import com.bumpkin.disk.entities.DiskUser;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public class WebUtil {

    public static String getUserNameByRequest(HttpServletRequest request) {
        DiskUser diskUser = (DiskUser) request.getSession().getAttribute("user");
        String userName = "null";
        if(diskUser == null || diskUser.getUsername() == null) {
            return userName;
        }
        return diskUser.getUsername();
    }
    public static DiskUser getUserByRequest(HttpServletRequest request) {
        return (DiskUser) request.getSession().getAttribute("user");
    }
}
