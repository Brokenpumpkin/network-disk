package com.bumpkin.disk.file.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/10 23:24
 */
@Slf4j
public class FileUtil {

    /**
     * 文件移动
     *
     * @param oldName 要移动的文件
     * @param newName 新的路径
     */
    public static boolean renameFile(String oldName, String newName) {
        // 路径
        if (!oldName.equals(newName)) {
            File oldfile = new File(oldName);
            File newfile = new File(newName);
            // 重命名文件不存在
            if (!oldfile.exists()) {
                return false;
            }
            // 若在该目录下已经有一个文件和新文件名相同，则不允许重命名
            if (newfile.exists()) {
                log.warn(newName + "已经存在！");
                return false;
            } else {
                return oldfile.renameTo(newfile);
            }
        } else {
            log.warn("移动路径没有变化相同...");
            return false;
        }
    }
}
