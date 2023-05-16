package com.vip.file.model.response;

/**
 * 统一Service层返回数据对象构建类
 *
 * @author vip
 * @date 2019/11/27 16:44
 */
public class Results {
    /**
     * 创建一个成功的返回结果
     *
     * @param data 返回数据
     * @return {@link Result}<{@link T}>
     */
    public static <T> Result<T> newSuccessResult(T data) {
        return newSuccessResult(data, null);
    }

    /**
     * 创建一个成功的返回结果
     *
     * @param data        数据
     * @param description 描述
     * @return {@link Result}<{@link T}>
     */
    public static <T> Result<T> newSuccessResult(T data, String description) {
        Result<T> r = new Result<T>();
        r.setErrorCode(ErrorCode.NO_ERROR);
        r.setData(data);
        r.setDescription(description);
        return r;
    }

    /**
     * 创建一个成功的返回结果
     *
     * @param data        数据
     * @param description 描述
     * @param extra       额外的
     * @return {@link Result}<{@link T}>
     */
    public static <T> Result<T> newSuccessResult(T data, String description, Object extra) {
        Result<T> r = new Result<T>();
        r.setErrorCode(ErrorCode.NO_ERROR);
        r.setData(data);
        r.setDescription(description);
        r.setExtra(extra);
        return r;
    }

    /**
     * 创建一个失败的返回结果
     *
     * @param errorCode   错误码
     * @param description 描述
     * @return {@link Result}<{@link T}>
     */
    public static <T> Result<T> newFailResult(ErrorCode errorCode, String description) {
        Result<T> r = new Result<T>();
        r.setDescription(description);
        r.setErrorCode(errorCode);
        return r;
    }

    /**
     * 创建一个失败的返回结果
     *
     * @param errorCode   错误码
     * @param description 描述
     * @param data        数据
     * @return {@link Result}<{@link T}>
     */
    public static <T> Result<T> newFailResult(ErrorCode errorCode, T data, String description) {
        Result<T> r = new Result<T>();
        r.setDescription(description);
        r.setErrorCode(errorCode);
        r.setData(data);
        return r;
    }
}

