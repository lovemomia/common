package cn.momia.api.base.http;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class MomiaHttpResponse {
    private static class ErrorCode {
        public static final int SUCCESS = 0;
        public static final int FAILED = 1;

        public static final int TOKEN_EXPIRED = 100001;

        public static final int BAD_REQUEST = 400;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int METHOD_NOT_ALLOWED = 405;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }

    private static final String SUCCESS_MSG = "success";
    private static final String FAILED_MSG = "failed";


    public static final MomiaHttpResponse SUCCESS = new MomiaHttpResponse(SUCCESS_MSG);
    public static final MomiaHttpResponse FAILED = new MomiaHttpResponse(ErrorCode.FAILED, FAILED_MSG);

    public static final MomiaHttpResponse TOKEN_EXPIRED = new MomiaHttpResponse(ErrorCode.TOKEN_EXPIRED, "用户token过期，需要重新登录");

    public static final MomiaHttpResponse BAD_REQUEST = new MomiaHttpResponse(ErrorCode.BAD_REQUEST, "参数不正确");
    public static final MomiaHttpResponse FORBIDDEN = new MomiaHttpResponse(ErrorCode.FORBIDDEN, "禁止访问");
    public static final MomiaHttpResponse NOT_FOUND = new MomiaHttpResponse(ErrorCode.NOT_FOUND, "页面不存在");
    public static final MomiaHttpResponse METHOD_NOT_ALLOWED = new MomiaHttpResponse(ErrorCode.METHOD_NOT_ALLOWED, "无效的请求方法");
    public static final MomiaHttpResponse INTERNAL_SERVER_ERROR = new MomiaHttpResponse(ErrorCode.INTERNAL_SERVER_ERROR, "服务器内部错误");

    public static MomiaHttpResponse SUCCESS(Object data) {
        return new MomiaHttpResponse(data);
    }

    public static MomiaHttpResponse FAILED(String errmsg) {
        return new MomiaHttpResponse(ErrorCode.FAILED, errmsg);
    }

    public static MomiaHttpResponse formJson(JSONObject jsonObject) {
        MomiaHttpResponse responseMessage = new MomiaHttpResponse();
        responseMessage.errno = jsonObject.getInteger("errno");
        responseMessage.errmsg = jsonObject.getString("errmsg");
        responseMessage.data = jsonObject.get("data");

        return responseMessage;
    }

    private int errno = ErrorCode.FAILED;
    private String errmsg;
    private Object data;
    private long time = new Date().getTime();

    private MomiaHttpResponse() {}

    private MomiaHttpResponse(Object data) {
        this(ErrorCode.SUCCESS, SUCCESS_MSG);
        this.data = data;
    }

    private MomiaHttpResponse(int errno, String errmsg) {
        this.errno = errno;
        this.errmsg = errmsg;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean successful() {
        return errno == ErrorCode.SUCCESS;
    }

    public boolean tokenExpired() {
        return errno == ErrorCode.TOKEN_EXPIRED;
    }
}
