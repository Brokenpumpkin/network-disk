package com.bumpkin.disk.file.dto;

import lombok.Data;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/24 16:52
 */
@Data
public class FileRenameDto {

    String oldName;

    String newName;

    String path;
}
