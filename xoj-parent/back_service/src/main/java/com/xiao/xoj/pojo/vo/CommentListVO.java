package com.xiao.xoj.pojo.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author 肖恩
 * @create 2023/5/25 9:23
 * @description: TODO
 */
@Data
public class CommentListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private IPage<CommentVO> commentList;

    private HashMap<Integer, Boolean> commentLikeMap;

}