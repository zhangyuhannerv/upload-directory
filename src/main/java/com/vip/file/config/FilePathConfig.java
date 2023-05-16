package com.vip.file.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 注入属性类
 *
 * @author wgb
 * @date 2021/10/16 10:46
 */
@Configuration
@EnableConfigurationProperties(FilePathProperties.class)
public class FilePathConfig {
}
