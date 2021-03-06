package com.wenbing.mvpdemo.retrofit.error;

import android.net.ParseException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;


/**
 * @desc 异常拦截器
 * @auther zwb
 * create at 2017/2/9 14:07
 */

public class ExceptionEngine {

    //对应HTTP的状态码
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    public static ApiException handleException(Throwable e) {
        ApiException ex;
        if (e instanceof HttpException) {             //HTTP错误
            HttpException httpException = (HttpException) e;
            ex = new ApiException(e, ERROR.HTTP_ERROR);
            switch (httpException.code()) {
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    ex.setMessage("系统错误，请联系管理员！");  //均视为网络错误
                    break;
            }
            return ex;
        } else if (e instanceof ServerException) {    //服务器返回的错误
            ServerException resultException = (ServerException) e;
            ex = new ApiException(resultException, resultException.getCode());
            ex.setMessage(resultException.getMsg());
            return ex;
        } else if (e instanceof JSONException || e instanceof ParseException) {
            ex = new ApiException(e, ERROR.PARSE_ERROR);
            ex.setMessage("数据解析失败");            //均视为解析错误
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ApiException(e, ERROR.NETWORD_ERROR);
            ex.setMessage("网络连接失败");  //均视为网络错误
            return ex;
        } else if (e instanceof UnknownHostException) {
            ex = new ApiException(e, ERROR.NETWORD_ERROR);
            ex.setMessage("网络连接异常");
            return ex;
        } else if (e instanceof SocketTimeoutException) {
            ex = new ApiException(e, ERROR.NETWORD_ERROR);
            ex.setMessage("网络连接超时");
            return ex;
        } else if (e instanceof ConnectTimeoutException) {
            ex = new ApiException(e, ERROR.NETWORD_ERROR);
            ex.setMessage("网络连接超时！");
            return ex;
        } else if (e instanceof IOException) {
            ex = new ApiException(e, ERROR.NETWORD_ERROR);
            ex.setMessage("网络连接异常!");
            return ex;
        } else {
            ex = new ApiException(e, ERROR.UNKNOWN);
            e.printStackTrace();
            ex.setMessage("未知错误");          //未知错误
            return ex;
        }
    }
}