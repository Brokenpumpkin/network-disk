package com.bumpkin.disk.file.sevice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bumpkin.disk.file.entity.File;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/09 11:04
 */
public interface FileService extends IService<File> {

    Boolean upload(MultipartFile file, String userName, String path);
    String getFileRootPath();
}
