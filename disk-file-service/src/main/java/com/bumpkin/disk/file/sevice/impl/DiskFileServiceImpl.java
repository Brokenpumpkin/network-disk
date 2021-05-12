package com.bumpkin.disk.file.sevice.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.dao.DiskFileMapper;
import com.bumpkin.disk.file.entity.DiskFile;
import com.bumpkin.disk.file.entity.LinkSecret;
import com.bumpkin.disk.file.entity.VirtualAddress;
import com.bumpkin.disk.file.sevice.DiskFileService;
import com.bumpkin.disk.file.sevice.LinkSecretService;
import com.bumpkin.disk.file.sevice.VirtualAddressService;
import com.bumpkin.disk.file.util.*;
import com.bumpkin.disk.file.vo.DiskFileVo;
import com.bumpkin.disk.file.vo.UserDirVo;
import com.bumpkin.disk.result.ResponseResult;
import com.bumpkin.disk.utils.EntityUtil;
import com.bumpkin.disk.utils.FileEncAndDecUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    public String fileRootPath;

    @Autowired
    public VirtualAddressService virtualAddressService;

    @Autowired
    public LinkSecretService linkSecretService;

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
            // 文件不加密直接存储
//            FileUtils.copyInputStreamToFile(file.getInputStream(), encodeFile);

            // 文件加密
            assert diskUser != null;
            Key key = FileEncAndDecUtil.toKey(diskUser.getPassword());
            FileEncAndDecUtil.encFile(tempFile, encodeFile, key, diskUser.getSalt().getBytes(StandardCharsets.UTF_8));

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
    public void download(String fileName, DiskUser diskUser, String path, HttpServletResponse response) throws Exception {
        VirtualAddress virtualAddress = virtualAddressService.getDiskFileByFileNameAndParentPathAndUserId(fileName, path, diskUser.getUserId());
        if (virtualAddress != null) {
            String fileId = virtualAddress.getFileId();
            DiskFile diskFile = this.baseMapper.selectById(fileId);
            String fileLocalLocation = diskFile.getFileLocalLocation();

            // 文件解密
            Key key = FileEncAndDecUtil.toKey(diskUser.getPassword());
            File encfile = new File(fileLocalLocation + diskFile.getSaveFileName());
            File decFile = new File(fileLocalLocation + diskFile.getOriginalName());
            FileEncAndDecUtil.decFile(encfile, decFile, key, diskUser.getSalt().getBytes(StandardCharsets.UTF_8));

            byte[] bytes = FileUtils.readFileToByteArray(decFile);
            MagicMatch magicMatch = Magic.getMagicMatch(decFile, true, false);

            response.setStatus(200);
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Access-Control-Expose-Headers", "fileName");
            response.setHeader("fileName", URLEncoder.encode(fileName, "UTF-8"));
            response.setHeader("Access-Control-Expose-Headers", "type");
            response.setHeader("type", magicMatch.getMimeType());
            response.setHeader("Accept-Ranges", "bytes");
            //new String(fileName.getBytes(), StandardCharsets.UTF_8)
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.getOutputStream().write(bytes);
        }
    }

    @Transactional
    @Override
    public Boolean userFileRename(String oldName, String newName, DiskUser diskUser, String path) {
        VirtualAddress virtualAddress = virtualAddressService.getDiskFileByFileNameAndParentPathAndUserId(oldName, path, diskUser.getUserId());
        if (virtualAddress != null) {
            virtualAddress.setUserFileName(newName);
            virtualAddress.setUpdateTime(EntityUtil.getUpdateEntity().getUpdateTime());
            UpdateWrapper<VirtualAddress> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_file_name", oldName);
            virtualAddressService.update(virtualAddress, updateWrapper);
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
    public List<UserDirVo> userDirList(DiskUser diskUser, String path) {
        if (!path.contains("/")) {
            path = "/" + diskUser.getUsername();
        }
        QueryWrapper<VirtualAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("full_parent_path", path);
        queryWrapper.eq("is_dir", 1);
        queryWrapper.eq("is_delete", 0);
        List<VirtualAddress> virtualAddresses = virtualAddressService.getBaseMapper().selectList(queryWrapper);
        List<UserDirVo> userDirVoList = null;
        if (virtualAddresses != null) {
            userDirVoList = new ArrayList<>();
            for (VirtualAddress v: virtualAddresses) {
                UserDirVo userDirVo = new UserDirVo();
                userDirVo.setId(v.getId());
                userDirVo.setName(v.getUserFileName());
                userDirVo.setPath(v.getFullParentPath());
                userDirVoList.add(userDirVo);
            }
        }
        return userDirVoList;
    }

    @Override
    public List<DiskFileVo> search(String keyword, DiskUser diskUser) {
        QueryWrapper<VirtualAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", diskUser.getUserId());
        queryWrapper.like("user_file_name", keyword);
        queryWrapper.eq("is_delete", 0);
        List<VirtualAddress> virtualAddressList = virtualAddressService.getBaseMapper().selectList(queryWrapper);
        List<DiskFileVo> diskFileVoList = new ArrayList<>();
        for (VirtualAddress v : virtualAddressList) {
            DiskFileVo diskFileVo = new DiskFileVo();
            BeanUtils.copyProperties(v,diskFileVo);
            diskFileVo.setFileSize(FileSizeUtil.getNetFileSizeDescription(v.getFileSize()));
            diskFileVoList.add(diskFileVo);
        }
        return diskFileVoList;
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
