package com.sping.billage.global.file;

/**
 * 디스크에 저장된 파일 정보.
 *
 * @param originalFileName 사용자가 업로드한 원본 파일명
 * @param storedFileName   중복을 피하기 위해 새로 부여한 저장 파일명
 * @param imagePath        클라이언트가 접근할 수 있는 경로 (예: /upload/xxx.png)
 */
public record StoredFile(String originalFileName, String storedFileName, String imagePath) {
}
