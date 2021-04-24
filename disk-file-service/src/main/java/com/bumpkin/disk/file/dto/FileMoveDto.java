package com.bumpkin.disk.file.dto;

import lombok.Data;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/24 20:46
 */
@Data
public class FileMoveDto {

    String fileName;

    String oldPath;

    String newPath;
}
