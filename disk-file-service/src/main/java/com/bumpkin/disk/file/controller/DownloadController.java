package com.bumpkin.disk.file.controller;

import com.bumpkin.disk.file.sevice.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/07 20:13
 */
@RequestMapping("/Download")
@RestController
public class DownloadController {

    @Autowired
    public FileService fileService;
}
