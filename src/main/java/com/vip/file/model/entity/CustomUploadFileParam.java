package com.vip.file.model.entity;

import cn.novelweb.tool.upload.local.pojo.UploadFileParam;
import lombok.Data;

@Data
public class CustomUploadFileParam extends UploadFileParam {
    private String filePath;
}
