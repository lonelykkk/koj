package com.xiao.xoj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.entity.about.StudioInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-06-11
 */
public interface StudioInfoService extends IService<StudioInfo> {

    IPage<StudioInfo> getMemberList(Integer limit, Integer currentPage, String keyword);

    boolean uploadMembersInfo(MultipartFile file);

    boolean addStudioInfo(StudioInfo studioInfo);

    boolean updateStudioInfo(StudioInfo studioInfo);

    boolean deleteStudioInfo(Integer sid);
}
