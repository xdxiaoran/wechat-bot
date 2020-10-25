package com.harry.wechat.dto;

import org.springframework.http.HttpStatus;

/**
 * @author Harry
 * @date 2020/9/26
 * Time: 18:04
 * Desc: BaseResponse
 */
public class BaseResponse {

    private Integer code;
    private String message;
    private Object data;

    public final static BaseResponse OK = new BaseResponse(HttpStatus.OK);

    public static BaseResponse fail(String message){
        return new BaseResponse(400,message);
    }

    public static BaseResponse OK(Object data) {
        return new BaseResponse(HttpStatus.OK, data);
    }

    public BaseResponse() {
    }

    public BaseResponse(HttpStatus status, Object object) {
        this.code = status.value();
        this.message = status.getReasonPhrase();
        this.data = object;
    }

    public BaseResponse(HttpStatus status) {
        this.code = status.value();
        this.message = status.getReasonPhrase();
    }

    public BaseResponse(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
