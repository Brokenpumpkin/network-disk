package com.bumpkin.disk.file.sevice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.entity.DiskFile;
import com.bumpkin.disk.file.vo.DiskFileVo;
import com.bumpkin.disk.file.vo.UserDirVo;
import com.bumpkin.disk.result.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/09 11:04
 */
public interface DiskFileService extends IService<DiskFile> {

    /**
     * 上传文件
     * @param file
     * @param diskUser
     * @param path
     * @return
     */
    ResponseResult upload(MultipartFile file, DiskUser diskUser, String path);

    /**
     * 下载文件
     * @param fileName
     * @param diskUser
     * @param path 文件所在虚拟路径
     * @return
     */
    void download(String fileName, DiskUser diskUser, String path, HttpServletResponse response) throws Exception;

    /**
     * 重命名文件
     * @param oldName
     * @param newName
     * @param diskUser
     * @param path
     * @return
     */
    Boolean userFileRename(String oldName, String newName, DiskUser diskUser, String path);

    /**
     * 新建文件夹
     * @param dirName
     * @param path
     * @return
     */
    Boolean userDirCreate(String dirName, String path, DiskUser diskUser);

    /**
     * 移动文件、文件夹
     * @param fileName
     * @param oldPath
     * @param newPath
     * @param diskUser
     * @return
     */
    Boolean userFileDirMove(String fileName, String oldPath, String newPath, DiskUser diskUser);

    /**
     * 列出用户文件
     * @param diskUser
     * @param path
     * @return
     */
    List<DiskFileVo> userFileList(DiskUser diskUser, String path);

    List<UserDirVo> userDirList(DiskUser diskUser, String path);

    /**
     * 搜索文件
     * @param keyword
     * @param diskUser
     * @return
     */
    List<DiskFileVo> search(String keyword, DiskUser diskUser);

    DiskFile checkMd5Exist(String md5ToStr);
    String getFileRootPath();
}
