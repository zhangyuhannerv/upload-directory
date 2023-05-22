package com.vip.file.service.impl;

import cn.novelweb.tool.upload.local.LocalUpload;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vip.file.constant.SysConstant;
import com.vip.file.mapper.FilesMapper;
import com.vip.file.model.dto.AddFileDto;
import com.vip.file.model.dto.GetFileDto;
import com.vip.file.model.entity.CustomUploadFileParam;
import com.vip.file.model.entity.Files;
import com.vip.file.model.response.ErrorCode;
import com.vip.file.model.response.Result;
import com.vip.file.model.response.Results;
import com.vip.file.service.IFileService;
import com.vip.file.utils.EmptyUtils;
import com.vip.file.utils.NovelWebUtils;
import com.vip.file.utils.UuidUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 文件上传下载 服务实现类
 * </p>
 *
 * @author LEON
 * @since 2020-05-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements IFileService {

    private final FilesMapper filesMapper;
    @Value("${file.save-path:/data-center/files/vip-file-manager}")
    private String savePath;
    @Value("${file.conf-path:/data-center/files/vip-file-manager/conf}")
    private String confFilePath;

    @Override
    public Result<List<GetFileDto>> getFileList(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        File dir = new File(savePath);
        File[] files = dir.listFiles();
        List<GetFileDto> list = new ArrayList<>();
        GetFileDto dto;
        for (File file : files) {
            if (file.getName().startsWith(".")) {
                continue;
            }

            dto = new GetFileDto();
            dto.setFilePath(file.getAbsolutePath().replace(savePath, ""));
            dto.setFileName(file.getName());
            if (file.isDirectory()) {
                dto.setSuffix(SysConstant.DIR_TYPE);
            } else {
                dto.setSuffix(dto.getFileName().substring(dto.getFileName().lastIndexOf(".") + 1));
            }

            try {
                BasicFileAttributes attrs = java.nio.file.Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                dto.setCreatedTime(new Date(attrs.creationTime().toMillis()));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

            list.add(dto);
        }
        PageInfo<GetFileDto> pageInfo = new PageInfo<>(list);
        return Results.newSuccessResult(pageInfo.getList(), "查询成功", pageInfo.getTotal());
    }

    @Override
    public Result<String> uploadFiles(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            // 文件名非空校验
            if (EmptyUtils.basicIsEmpty(fileName)) {
                return Results.newFailResult(ErrorCode.FILE_ERROR, "文件名不能为空");
            }
            // 大文件判定
            if (file.getSize() > SysConstant.MAX_SIZE) {
                return Results.newFailResult(ErrorCode.FILE_ERROR, "文件过大，请使用大文件传输");
            }
            // 生成新文件名
            String suffixName = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : null;
            String newName = UuidUtils.uuid() + suffixName;
            // 重命名文件
            File newFile = new File(savePath, newName);
            // 如果该存储路径不存在则新建存储路径
            if (!newFile.getParentFile().exists()) {
                newFile.getParentFile().mkdirs();
            }
            // 文件写入
            file.transferTo(newFile);
            // 保存文件信息
            Files files = new Files().setFilePath(newName).setFileName(fileName).setSuffix(suffixName == null ? null : suffixName.substring(1));
            filesMapper.insert(files);
            return Results.newSuccessResult(files.getId(), "上传完成");
        } catch (Exception e) {
            log.error("上传协议文件出错", e);
        }
        return Results.newFailResult(ErrorCode.FILE_ERROR, "上传失败");
    }

    @Override
    public Result<Object> checkFileMd5(String md5, String fileName, String filePath) {
        try {
            // 要保存的文件路径
            String path = savePath + File.separator + filePath;

            // 要保存的配置文件路径
            String path1 = confFilePath + File.separator + filePath;

            cn.novelweb.tool.http.Result result = LocalUpload.checkFileMd5(md5, fileName, path1, path);
            return NovelWebUtils.forReturn(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Results.newFailResult(ErrorCode.FILE_UPLOAD, "上传失败");
    }

    @Override
    public Result breakpointResumeUpload(CustomUploadFileParam param, HttpServletRequest request) {
        try {
            // 要保存的文件路径
            String path = savePath + File.separator + param.getFilePath();
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 要保存的配置文件路径
            String path1 = confFilePath + File.separator + param.getFilePath();
            File dir1 = new File(path1);
            if (!dir1.exists()) {
                dir1.mkdirs();
            }

            // 这里的 chunkSize(分片大小) 要与前端传过来的大小一致
            cn.novelweb.tool.http.Result result = LocalUpload.fragmentFileUploader(param, path1, path, 5242880L, request);
            return NovelWebUtils.forReturn(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Results.newFailResult(ErrorCode.FILE_UPLOAD, "上传失败");
    }

    @Override
    public Result<String> addFile(AddFileDto dto) {
        Files file = new Files();
        BeanUtils.copyProperties(dto, file);
        if (filesMapper.fileIsExist(dto.getFileName())) {
            return Results.newSuccessResult(null, "添加成功");
        }
        filesMapper.insert(file.setFilePath(dto.getFileName()));
        return Results.newSuccessResult(null, "添加成功");
    }

    @Override
    public InputStream getFileInputStream(String id) {
        try {
            Files files = filesMapper.selectById(id);
            File file = new File(savePath + File.separator + files.getFilePath());
            return new FileInputStream(file);
        } catch (Exception e) {
            log.error("获取文件输入流出错", e);
        }
        return null;
    }

    @Override
    public Result<Files> getFileDetails(String id) {
        Files files = filesMapper.selectById(id);
        return Results.newSuccessResult(files, "查询成功");
    }
}
