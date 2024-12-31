package com.xiao.xoj.pojo.vo;

import com.xiao.xoj.pojo.entity.resource.Resource;
import com.xiao.xoj.pojo.entity.resource.ResourceTag;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/6/11 16:20
 * @description: TODO
 */
@Data
public class ResourceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ResourceTag resourceTag;

    private List<Resource> resourceList;

}
