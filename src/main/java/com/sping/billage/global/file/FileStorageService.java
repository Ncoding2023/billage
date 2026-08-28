package com.sping.billage.global.file;

import com.sping.billage.global.exception.BusinessException;
import com.sping.billage.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 이미지 파일을 로컬 디스크에 저장한다. 저장 위치는 application.properties 의 업로드 경로를 사용한다.
 */
@Slf4j
@Component
public class FileStorageService {

    public static final String URL_PREFIX = "/upload/";

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final Path uploadRoot;

    public FileStorageService(@Value("${com.example.upload.path}") String uploadPath) {
        this.uploadRoot = Paths.get(uploadPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException e) {
            throw new IllegalStateException("업로드 디렉터리를 생성할 수 없습니다: " + uploadRoot, e);
        }
        log.info("파일 업로드 경로: {}", uploadRoot);
    }

    public Path getUploadRoot() {
        return uploadRoot;
    }

    public StoredFile store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE, "빈 파일은 업로드할 수 없습니다.");
        }

        String originalFileName = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "unknown" : file.getOriginalFilename());
        String extension = extractExtension(originalFileName);
        validate(file.getContentType(), extension);

        String storedFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path target = uploadRoot.resolve(storedFileName).normalize();

        if (!target.startsWith(uploadRoot)) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE, "잘못된 파일 경로입니다.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("파일 저장 실패: {}", originalFileName, e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        return new StoredFile(originalFileName, storedFileName, URL_PREFIX + storedFileName);
    }

    /**
     * 여러 파일을 저장한다. 중간에 실패하면 이미 저장된 파일까지 되돌린다.
     */
    public List<StoredFile> storeAll(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        List<StoredFile> stored = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    stored.add(store(file));
                }
            }
        } catch (RuntimeException e) {
            stored.forEach(file -> delete(file.imagePath()));
            throw e;
        }
        return stored;
    }

    /**
     * 저장된 파일을 삭제한다. 이미 없거나 실패해도 요청 자체를 실패시키지 않는다.
     */
    public void delete(String imagePath) {
        if (!StringUtils.hasText(imagePath)) {
            return;
        }
        String fileName = imagePath.startsWith(URL_PREFIX)
                ? imagePath.substring(URL_PREFIX.length())
                : imagePath;
        Path target = uploadRoot.resolve(fileName).normalize();

        if (!target.startsWith(uploadRoot)) {
            log.warn("업로드 경로를 벗어난 삭제 요청을 무시한다: {}", imagePath);
            return;
        }
        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("파일 삭제 실패: {}", target, e);
        }
    }

    private void validate(String contentType, String extension) {
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE,
                    "이미지 파일만 업로드할 수 있습니다. (jpg, png, gif, webp)");
        }
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE,
                    "허용되지 않는 확장자입니다: " + extension);
        }
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE, "확장자가 없는 파일은 업로드할 수 없습니다.");
        }
        return fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
