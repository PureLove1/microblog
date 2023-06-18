package com.microblog.controller;

import com.microblog.common.Result;
import com.microblog.pojo.FastDFSFile;
import com.microblog.util.FastDfsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import static com.microblog.constant.StatusCode.*;


/**
 * @author 贺畅
 * @date 2022/11/28
 */
@RestController
@CrossOrigin//支持跨域
//跨域:
//不同的域名A 访问 域名B 的数据就是跨域
// 端口不同 也是跨域  loalhost:18081----->localhost:18082
// 协议不同 也是跨域  http://www.jd.com  --->  https://www.jd.com
// 域名不同 也是跨域  http://www.jd.com  ---> http://www.taobao.com
// 协议一直,端口一致,域名一致就不是跨域
// http://www.jd.com:80 --->http://www.jd.com:80 不是跨域
@Slf4j
@RequestMapping("/file")
public class FileController {
    /**
     * 支持的文件类型
     */
    private static HashSet<String> filenameExtensions = new HashSet();
    private static HashSet<String> videoExtensions = new HashSet();
    static {
        filenameExtensions.add("png");
        filenameExtensions.add("jpg");
        filenameExtensions.add("jpeg");
        videoExtensions.add("mp4");
    }
    /**
     * 返回 图片的全路径
     *
     * @param file 页面的文件对象
     * @return
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam(value = "file") MultipartFile file) {
        log.info("开始文件上传");
        try {
            //获取文件扩展名
            String filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            if (!filenameExtensions.contains(filenameExtension)){
                log.error("上传的文件类型错误{}",filenameExtension);
                return Result.error("不支持的文件类型",USER_UPLOAD_FILE_TYPE_UNMATCHED_ERROR);
            }

            //1. 创建图片文件对象(封装)
            FastDFSFile fastdfsfile = new FastDFSFile(
                    //原来的文件名
                    file.getOriginalFilename(),
                    //文件本身的字节数组
                    file.getBytes(),
                    //获取文件扩展名
                    filenameExtension
            );

            //2. 调用工具类实现图片上传
            String[] upload = FastDfsClient.upload(fastdfsfile);

            //3. 拼接图片的全路径返回
            log.info("文件上传成功，返回图片请求地址"+FastDfsClient.getTrackerUrl()+"/"+upload[0]+"/"+upload[1]);
            return Result.ok(FastDfsClient.getTrackerUrl()+"/"+upload[0]+"/"+upload[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.error(LocalDateTime.now() + "上传文件过程中出错");
        return Result.error("文件上传过程中出错！",SYSTEM_EXECUTION_ERROR);
    }

    /**
     * 返回 图片的全路径
     *
     * @param file 页面的文件对象
     * @return
     */
    @PostMapping("/upload/video")
    public Result uploadVideo(@RequestParam(value = "file") MultipartFile file) {
        try {
            //获取文件扩展名
            String filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            if (!videoExtensions.contains(filenameExtension)){
                log.error("上传的文件类型错误{}",filenameExtension);
                return Result.error("不支持的文件类型",USER_UPLOAD_FILE_TYPE_UNMATCHED_ERROR);
            }

            //1. 创建图片文件对象(封装)
            FastDFSFile fastdfsfile = new FastDFSFile(
                    //原来的文件名
                    file.getOriginalFilename(),
                    //文件本身的字节数组
                    file.getBytes(),
                    //获取文件扩展名
                    filenameExtension
            );

            //2. 调用工具类实现图片上传
            String[] upload = FastDfsClient.upload(fastdfsfile);

            //3. 拼接图片的全路径返回
            log.info("文件上传成功，返回视频请求地址"+FastDfsClient.getTrackerUrl()+"/"+upload[0]+"/"+upload[1]);
            return Result.ok(FastDfsClient.getTrackerUrl()+"/"+upload[0]+"/"+upload[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.error(LocalDateTime.now() + "上传文件过程中出错");
        return Result.error("文件上传过程中出错！",SYSTEM_EXECUTION_ERROR);
    }

    @DeleteMapping("/delete")
    public Result deleteFile(){
        return null;
    }

}
