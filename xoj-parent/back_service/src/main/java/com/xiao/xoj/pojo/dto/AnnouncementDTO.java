package com.xiao.xoj.pojo.dto;

import com.xiao.xoj.pojo.entity.announce.Announcement;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/5/31 20:57
 * @description: TODO
 */
@Data
public class AnnouncementDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "比赛id不能为空")
    private Integer cid;

    private Announcement announcement;

}
