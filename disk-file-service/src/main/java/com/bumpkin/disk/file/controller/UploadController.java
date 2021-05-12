package com.bumpkin.disk.file.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.dto.FileInfoDto;
import com.bumpkin.disk.file.entity.DiskFile;
import com.bumpkin.disk.file.entity.TChunkInfo;
import com.bumpkin.disk.file.sevice.ChunkService;
import com.bumpkin.disk.file.sevice.DiskFileService;
import com.bumpkin.disk.file.sevice.VirtualAddressService;
import com.bumpkin.disk.file.util.FileInfoUtil;
import com.bumpkin.disk.file.util.WebUtil;
import com.bumpkin.disk.file.vo.FileChunkVo;
import com.bumpkin.disk.result.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/07 20:13
 */
@Api(tags = "上传文件")
@Slf4j
@RequestMapping("/upload")
@RestController
public class UploadController {

    @Value("${fileRootPath}")
    public String fileRootPath;

    @Resource
    private ChunkService chunkService;

    @Autowired
    public DiskFileService diskFileService;

    @Autowired
    public VirtualAddressService virtualAddressService;

    @Autowired
    private WebUtil webUtil;

    @ApiOperation(value = "一次性上传")
    @PostMapping(value = "/fileUpload")
    public ResponseResult upload(@RequestParam(name = "file") MultipartFile file
            ,@RequestParam("path") String path, HttpServletRequest request) {
        if (path == null) {
            path = "/";
        }
        if (file.isEmpty()) {
            return ResponseResult.createErrorResult("请选择要上传的文件！");
        }
        // 获取用户
        DiskUser diskUser = webUtil.getUserByRequest(request);
        if (diskUser != null) {
            // 上传文件
            return diskFileService.upload(file, diskUser, path);
        }
        return ResponseResult.createErrorResult("用户不存在！");
    }

    @ApiOperation(value = "分块上传 有断点续传的功能")
    @PostMapping("/chunk")
    public String uploadChunk(TChunkInfo chunk) {
        String apiRlt = "200";

        MultipartFile file = chunk.getUpfile();
        log.info("file originName: {}, chunkNumber: {}", file.getOriginalFilename(), chunk.getChunkNumber());

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(FileInfoUtil.generatePath(fileRootPath, chunk));
            //文件写入指定路径
            Files.write(path, bytes);
            if(chunkService.saveChunk(chunk) < 0) {
                apiRlt = "415";
            }

        } catch (IOException e) {
            e.printStackTrace();
            apiRlt = "415";
        }
        return apiRlt;
    }

    @ApiOperation(value = "上传之前检查")
    @GetMapping("/chunk")
    public ResponseResult checkChunk(TChunkInfo chunk, HttpServletResponse response) {
        FileChunkVo fileChunkVo = new FileChunkVo();

        String file = fileRootPath + "/" + chunk.getIdentifier() + "/" + chunk.getFilename();

        //先判断整个文件是否已经上传过了，如果是，则告诉前端跳过上传，实现秒传
        if(FileInfoUtil.fileExists(file)) {
            fileChunkVo.setSkipUpload(true);
            fileChunkVo.setLocation(file);
            return ResponseResult.createSuccessResult(fileChunkVo, "完整文件已存在，直接跳过上传，实现秒传");
        }

        //如果完整文件不存在，则去数据库判断当前哪些文件块已经上传过了，把结果告诉前端，跳过这些文件块的上传，实现断点续传
        ArrayList<Integer> list = chunkService.checkChunk(chunk);
        if (list !=null && list.size() > 0) {
            fileChunkVo.setSkipUpload(false);
            fileChunkVo.setUploadedChunks(list);
            return ResponseResult.createSuccessResult(fileChunkVo, "部分文件块已存在，继续上传剩余文件块，实现断点续传");
        }
        return ResponseResult.createErrorResult("上传失败！");
    }

    @ApiOperation(value = "所有分块上传完成后合并")
    @PostMapping("/mergeFile")
    public String mergeFile(@RequestBody FileInfoDto fileInfoDto, HttpServletRequest request){

        String rlt = "FALURE";

        //前端组件参数转换为model对象
        DiskFile diskFile = new DiskFile();
        String uuid = IdUtil.fastSimpleUUID();
        diskFile.setFileId(uuid);
        diskFile.setSaveFileName(uuid + "." + StrUtil.subAfter(fileInfoDto.getName(), ".", false));
        diskFile.setOriginalName(fileInfoDto.getName());
        diskFile.setFileMd5(fileInfoDto.getUniqueIdentifier());
        diskFile.setId(fileInfoDto.getId());
        diskFile.setFileType(StrUtil.subAfter(fileInfoDto.getName(), ".", false));
        diskFile.setFileSize(fileInfoDto.getSize());
        diskFile.setCreateTime(new Date());

        DiskUser diskUser = webUtil.getUserByRequest(request);

        //进行文件的合并操作
        String filename = diskFile.getOriginalName();
        String file = fileRootPath + diskFile.getFileMd5() + "/" + filename;
        String folder = fileRootPath + diskFile.getFileMd5();
        String fileSuccess = FileInfoUtil.merge(file, folder, filename);

        diskFile.setId(uuid);
        diskFile.setFileLocalLocation(folder + "/");

        //文件合并成功后，保存记录至数据库
        if("200".equals(fileSuccess)) {
            if(diskFileService.getBaseMapper().insert(diskFile) > 0) {
                virtualAddressService.addFile(diskFile, diskUser.getUserId(), fileInfoDto.getPath());
                rlt = "SUCCESS";
            }
        }

        return rlt;
    }

    @GetMapping(value = "/test")
    public String test(HttpServletRequest request) {
//        return diskFileService.getFileRootPath();
//        String accessToken = request.getHeader("Authorization");
//        String s = StrUtil.subAfter(accessToken, " ", false);
        DiskUser diskUser = webUtil.getUserByRequest(request);
        log.warn(request.getHeader("Authorization"));
//        log.warn(s);
        log.warn(diskUser.toString());
        return diskUser.toString();
    }
}
