package com.vip.file.service;

import com.vip.file.model.dto.AddFileDto;
import com.vip.file.model.dto.GetFileDto;
import com.vip.file.model.entity.CustomUploadFileParam;
import com.vip.file.model.entity.Files;
import com.vip.file.model.response.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 文件上传下载 服务类
 * </p>
 *
 * @author LEON
 * @since 2020-05-29
 */
public interface IFileService {
    /**
     * 小文件上传
     *
     * @param file 文件
     * @return {@link Result}<{@link String}>
     */
    Result<String> uploadFiles(MultipartFile file);

    /**
     * 获取文件输入流
     *
     * @param id id
     * @return {@link InputStream}
     */
    InputStream getFileInputStream(String id);

    /**
     * 获取指定文件详情
     *
     * @param id id
     * @return {@link Result}<{@link Files}>
     */
    Result<Files> getFileDetails(String id);

    /**
     * 分页获取文件信息
     *
     * @param pageNo   当前页
     * @param pageSize 分页大小
     * @return {@link Result}<{@link List}<{@link GetFileDto}>>
     */
    Result<List<GetFileDto>> getFileList(Integer pageNo, Integer pageSize);

    /**
     * 检查文件MD5
     *
     * @param md5      md5
     * @param fileName 文件名称
     * @return {@link Result}<{@link Object}>
     */
    Result<Object> checkFileMd5(String md5, String fileName, String filePath);

    /**
     * 断点续传
     *
     * @param param   参数
     * @param request 请求
     * @return {@link Result}<{@link Object}>
     */
    Result<Object> breakpointResumeUpload(CustomUploadFileParam param, HttpServletRequest request);

    /**
     * 添加文件
     *
     * @param dto dto
     * @return {@link Result}<{@link String}>
     */
    Result<String> addFile(AddFileDto dto);
}
