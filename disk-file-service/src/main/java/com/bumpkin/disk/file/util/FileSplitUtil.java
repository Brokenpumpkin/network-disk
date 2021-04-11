package com.bumpkin.disk.file.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/09 21:22
 */
@Slf4j
public class FileSplitUtil {

    /**
     * 分割：根据size的大小智能判断切割成多少份，然后返回md5的数组
     *
     * @param filePath 文件路劲
     * @param size     文件每部分分割的大小
     * @param fileTemp 临时文件的目录F:\zcTest\:应该以临时文件目录加用户名
     * @return
     * @throws Exception
     */
    public static String[] splitBySizeSubSection(String filePath, int size, String fileTemp) throws Exception {
        long tempSize = size * 1024 * 1024;
        File fileDirTemp = new File(fileTemp);
        if (!fileDirTemp.exists()) {
            fileDirTemp.mkdirs();
        }
        log.warn("fileTemp:" + fileTemp);
        log.warn("filePath:" + filePath);
        File oldFile = new File(filePath);
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(oldFile));
        //byte类型
        long length = oldFile.length();
        //        分块的数量
        int number = (int) Math.ceil(length / (size * 1.0) / (1024 * 1.0) / (1024 * 1.0));
        String[] md5Array = new String[number];
        for (int i = 0; i < number; i++) {
            if (i == number - 1) {
                tempSize = oldFile.length() - (number - 1) * tempSize;
            }
            String newFilePath = fileTemp + randNumber() + ".file";
            File newFile = new File(newFilePath);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(newFile));
            byte[] buf = new byte[(int) tempSize];
            in.read(buf);
            out.write(buf);
            out.close();
//            DigestUtil.md5(new File(newFilePath));
//            DigestUtil.md5Hex(new File(newFilePath));
            md5Array[i] = MD5Util.getFileMD5ToString(new File(newFilePath));
            log.warn("md5:" + md5Array[i]);
        }
        //删除临时文件
        FileUtil.del(fileTemp);
        return md5Array;
    }

    /**
     * 随机数
     *
     * @return
     */
    public static String randNumber() {
        double number = Math.random();
        String str = String.valueOf(number);
        str = str.replace(".", "");
        return str;
    }
}
