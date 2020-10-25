package com.harry.wechat.config;

import com.harry.wechat.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Harry
 * @date 2020/10/9
 * Time: 10:16
 * Desc: GlobalExceptionHandler
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public BaseResponse defaultExceptionHander(HttpServletRequest req, Exception e) throws Exception {
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }

        log.error("<visit url {} failed, {} {} >", req.getRequestURL(), e.getStackTrace()[0].toString(), e);
        return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getStackTrace()[0]);
    }

}
