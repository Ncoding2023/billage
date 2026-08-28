package com.sping.billage.security;

import com.sping.billage.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 필터 단계에서 발생한 인증/인가 오류를 { success, data, message } 형태로 직접 기록한다.
 * (Controller 진입 전이라 @RestControllerAdvice 가 동작하지 않는다)
 */
final class SecurityResponseWriter {

    private SecurityResponseWriter() {
    }

    static void write(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = response.getWriter();
        writer.write("{\"success\":false,\"data\":null,\"message\":\"" + errorCode.getMessage() + "\"}");
        writer.flush();
    }
}
