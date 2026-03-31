package org.terminate.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.terminate.common.Result;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileController {

    // Define upload directory relative to project root
    private static final String UPLOAD_DIR = "uploads";

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            // Ensure upload directory exists
            String projectRoot = System.getProperty("user.dir");
            Path uploadPath = Paths.get(projectRoot, UPLOAD_DIR);
            
            // Log the path for debugging
            System.out.println("Uploading file to: " + uploadPath.toAbsolutePath());
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            // Add timestamp to filename to avoid collisions and cache issues
            String newFilename = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + extension;

            // Save file
            Path filePath = uploadPath.resolve(newFilename);
            file.transferTo(filePath.toFile());

            // Return URL (assuming mapped via WebMvcConfigurer)
            // Ensure no double slashes
            String fileUrl = "/api/files/" + newFilename;
            return Result.success(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            // Return full stack trace or message for debugging
            return Result.error("系统繁忙: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
