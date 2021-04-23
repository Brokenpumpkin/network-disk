package com.bumpkin.disk.file.sevice.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.dao.DiskFileMapper;
import com.bumpkin.disk.file.entity.DiskFile;
import com.bumpkin.disk.file.entity.VirtualAddress;
import com.bumpkin.disk.file.sevice.DiskFileService;
import com.bumpkin.disk.file.sevice.VirtualAddressService;
import com.bumpkin.disk.file.util.FileSizeUtil;
import com.bumpkin.disk.file.util.MD5Util;
import com.bumpkin.disk.file.util.MultipartFileUtil;
import com.bumpkin.disk.file.vo.DiskFileVo;
import com.bumpkin.disk.result.ResponseResult;
import com.bumpkin.disk.utils.EntityUtil;
import com.bumpkin.disk.utils.FileEncAndDecUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/09 11:04
 */
@Slf4j
@Service
public class DiskFileServiceImpl extends ServiceImpl<DiskFileMapper, DiskFile> implements DiskFileService {

    @Value("${fileRootPath}")
    public String fileRootPath;//static

    @Autowired
    public VirtualAddressService virtualAddressService;


//    @Value("${fileRootPath}")
//    public void setFileRootPath(String fileRootPath) {
//        FileServiceImpl.fileRootPath = fileRootPath;
//    }

    @Transactional
    @Override
    public ResponseResult upload(MultipartFile file, DiskUser diskUser, String path) {
        String userId = "0";
        if (diskUser != null) {
            userId = diskUser.getUserId();
        }
        // 文件真实存储地址路径
        String saveFilePath = fileRootPath;
        log.warn("1 saveFilePath:" + saveFilePath);
        try {
            String fileUuid = IdUtil.fastSimpleUUID();
            File tempFile = MultipartFileUtil.multipartFileToFile(file);
            String originalFilename = file.getOriginalFilename();
            String saveFileName = fileUuid + "." + StrUtil.subAfter(originalFilename, ".", false);
            String md5ToStr = MD5Util.getFileMD5ToString(tempFile);
            DiskFile diskFile = checkMd5Exist(md5ToStr);

            if (diskFile != null) {
                virtualAddressService.addFile(diskFile, userId, path);
                return ResponseResult.createSuccessResult("上传成功！");
            }
            assert originalFilename != null;
            File encodeFile = new File(saveFilePath, saveFileName);
            FileUtils.copyInputStreamToFile(file.getInputStream(), encodeFile);

            //文件加密
//            assert diskUser != null;
//            Key key = FileEncAndDecUtil.toKey(diskUser.getPassword());
//            FileEncAndDecUtil.encFile(tempFile, encodeFile, key, diskUser.getSalt());

            DiskFile newFile = new DiskFile();
            newFile.setId(fileUuid);
            newFile.setFileId(fileUuid);
            newFile.setFileLocalLocation(saveFilePath);
            newFile.setFileSize((int) FileUtil.size(tempFile));
            newFile.setFileMd5(md5ToStr);
            newFile.setFileType(StrUtil.subAfter(originalFilename, ".", false));
            newFile.setOriginalName(originalFilename);
            newFile.setSaveFileName(saveFileName);
            newFile.setCreateTime(EntityUtil.getNewEntity().getCreateTime());
            this.baseMapper.insert(newFile);
            virtualAddressService.addFile(newFile, userId, path);

            MultipartFileUtil.delteTempFile(tempFile);
            return ResponseResult.createSuccessResult("上传成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.createErrorResult("上传失败！");
    }

    @Override
    public void download(String fileName, DiskUser diskUser, String path, HttpServletResponse response) {
//        if (fileName.isEmpty()) {
//            return ResponseResult.createErrorResult("文件名字为空！");
//        }
        //todo 未测试
        VirtualAddress virtualAddress = virtualAddressService.getDiskFileByFileNameAndParentPathAndUserId(fileName, path, diskUser.getUserId());
        if (virtualAddress != null) {
            String fileId = virtualAddress.getFileId();
            DiskFile diskFile = this.baseMapper.selectById(fileId);
            String fileLocalLocation = diskFile.getFileLocalLocation();
            File file = new File(fileLocalLocation);
            BufferedInputStream bis = null;
            try (InputStream inputStream = new FileInputStream(file)) {
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO-8859-1"));
                byte[] buff = new byte[1024];
                OutputStream os;
                os = response.getOutputStream();
                bis = new BufferedInputStream(inputStream);
                int i = bis.read(buff);
                while (i != -1) {
                    os.write(buff, 0, buff.length);
                    os.flush();
                    i = bis.read(buff);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public Boolean userFileRename(String oldName, String newName, DiskUser diskUser, String path) {
        VirtualAddress virtualAddress = virtualAddressService.getDiskFileByFileNameAndParentPathAndUserId(oldName, path, diskUser.getUserId());
        if (virtualAddress != null) {
            String fileId = virtualAddress.getFileId();
            DiskFile diskFile = this.baseMapper.selectById(fileId);
            diskFile.setOriginalName(newName);
            return true;
        }
        return false;
    }

    @Override
    public Boolean userDirCreate(String dirName, String path, DiskUser diskUser) {
        return virtualAddressService.addDir(dirName, path, diskUser);
    }

    @Override
    public Boolean userFileDirMove(String fileName, String oldPath, String newPath, DiskUser diskUser) {
        return virtualAddressService.fileDirVirtualAddressMove(fileName, oldPath, newPath, diskUser);
    }

    @Override
    public String fileShareCodeEncode(String filePathAndName) {

        return null;
    }

    @Override
    public List<DiskFileVo> userFileList(DiskUser diskUser, String path) {

        List<VirtualAddress> virtualAddresses = virtualAddressService.getFileByUserAndParentPath(diskUser, path);
        List<DiskFileVo> diskFileVoList = new ArrayList<>();
        for (VirtualAddress v : virtualAddresses) {
            DiskFileVo diskFileVo = new DiskFileVo();
            BeanUtils.copyProperties(v,diskFileVo);
            diskFileVo.setFileSize(FileSizeUtil.getNetFileSizeDescription(v.getFileSize()));
            diskFileVoList.add(diskFileVo);
        }
        return diskFileVoList;
    }

    @Override
    public List<DiskFileVo> search(String key, String userName, String path) {

        return null;
    }

    @Override
    public String getFileRootPath() {
        return fileRootPath;
    }

    private DiskFile checkMd5Exist(String md5ToStr) {
        QueryWrapper<DiskFile> wrapper = new QueryWrapper<>();
        wrapper.eq("file_md5", md5ToStr);
        return this.baseMapper.selectOne(wrapper);
    }
}
