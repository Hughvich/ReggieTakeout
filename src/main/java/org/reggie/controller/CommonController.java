package org.reggie.controller;

import lombok.extern.slf4j.Slf4j;
import org.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传/下载
 *
 */

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    // 文件上传路径配置：在application.yml中
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传，用MultipartFile接收
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String > upload(MultipartFile file) {
        log.info("文件名： " + file.toString());

        // 判断目录对象是否存在。如不存在需要创建
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //切出文件后缀suffix：
        String originName = file.getOriginalFilename();
        String suffix = originName.substring(originName.lastIndexOf("."));
        //路径名，用UUID：
        String UUIDName = UUID.randomUUID() + suffix;
        // 临时文件转存到指定位置
        try {
            file.transferTo(new File(basePath + UUIDName));
            // 用原始文件名，可能会重复
//            file.transferTo(new File(basePath + file.getOriginalFilename()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(UUIDName);
    }

    /**
     * 文件下载
     * @param name 要下载的文件名
     * @param response 通过response获得输出流
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws FileNotFoundException {
        try {
            // 输入流，读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            // 输出流，回写浏览器，展示
            ServletOutputStream outputStream = response.getOutputStream();
            // 图片类型，设置回写的文件类型
            response.setContentType("image/jpg");
            // 输入流读到bytes数组里
            int len;
            byte[] bytes = new byte[1024];
            // 输入流读出来的bytes数组 往浏览器输入流 写，直到读完为止
            log.info("文件下载，页面回写：" + name);
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            fileInputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
