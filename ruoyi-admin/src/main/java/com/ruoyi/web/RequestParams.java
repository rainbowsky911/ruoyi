package com.ruoyi.web;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 请求参数封装
 */
@Data
@Accessors(chain = true)
public class RequestParams {
    private String key;

    //请求参数
    private Map<String, Object> params;

    private int pageNum;

    private int pageSize;

    private String title;

    //排序 0 按留言数量排序
    private int sortType;
}
