package org.terminate.exception;

import org.terminate.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        logger.warn("Business Exception: {}", e.getMessage());
        Result<String> result = new Result<>();
        result.setCode(e.getCode());
        result.setMessage(e.getMessage());
        return result;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Result<String> handleBadCredentialsException(BadCredentialsException e) {
        logger.warn("Authentication Failed: {}", e.getMessage());
        Result<String> result = new Result<>();
        result.setCode(401);
        result.setMessage("用户名或密码错误");
        return result;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<String> handleAccessDeniedException(AccessDeniedException e) {
        logger.warn("Access Denied: {}", e.getMessage());
        Result<String> result = new Result<>();
        result.setCode(403);
        result.setMessage("权限不足，无法访问");
        return result;
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        logger.error("System Error", e);
        return Result.error("系统繁忙，请稍后重试");
    }
}
