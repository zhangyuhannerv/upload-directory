package com.vip.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vip.file.model.dto.GetFileDto;
import com.vip.file.model.entity.Files;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author LEON
 * @since 2020-06-09
 */
public interface FilesMapper extends BaseMapper<Files> {
    /**
     * 获取文件列表
     *
     * @return {@link List}<{@link GetFileDto}>
     */
    List<GetFileDto> selectFileList();

    /**
     * 判断文件是否已存在
     *
     * @param fileName 文件名称
     * @return boolean
     */
    boolean fileIsExist(@Param("fileName") String fileName);
}
