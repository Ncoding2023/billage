package com.sping.billage.config;

import com.sping.billage.global.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 업로드된 이미지를 /upload/** 로 서비스한다.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final FileStorageService fileStorageService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(FileStorageService.URL_PREFIX + "**")
                .addResourceLocations(fileStorageService.getUploadRoot().toUri().toString());
    }
}
