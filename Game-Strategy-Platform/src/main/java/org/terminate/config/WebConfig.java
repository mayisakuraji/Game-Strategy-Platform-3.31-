package org.terminate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get absolute path to uploads directory
        String projectRoot = System.getProperty("user.dir");
        Path uploadPath = Paths.get(projectRoot, "uploads");

        // Map /api/files/** to local file system
        // Note: Spring Boot requires "file:" protocol for local paths
        registry.addResourceHandler("/api/files/**")
                .addResourceLocations("file:" + uploadPath.toAbsolutePath() + "/");
    }
}
