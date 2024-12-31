package com.xiao.xoj.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.pojo.entity.about.StudioInfo;
import com.xiao.xoj.mapper.StudioInfoMapper;
import com.xiao.xoj.service.StudioInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-06-11
 */
@Service
public class StudioInfoServiceImpl extends ServiceImpl<StudioInfoMapper, StudioInfo> implements StudioInfoService {

    @Resource
    private StudioInfoMapper studioInfoMapper;


    @Override
    public IPage<StudioInfo> getMemberList(Integer limit, Integer currentPage, String keyword) {
        if (limit == null || limit < 1) limit = 10;
        if (currentPage == null || currentPage < 1) currentPage = 1;
        IPage<StudioInfo> page = new Page<>(currentPage, limit);
        return studioInfoMapper.getMemberList(page, keyword);
    }


    @Override
    public boolean uploadMembersInfo(MultipartFile file) {

        return false;
    }


    @Override
    public boolean addStudioInfo(StudioInfo studioInfo) {
        // 参数校验
        if (StringUtils.isEmpty(studioInfo.getName().trim())) {
            throw new StatusFailException("姓名不能为空！");
        }
        if (studioInfo.getWhichSession() == null) {
            throw new StatusFailException("入学时间不能为空！");
        }
        if (StringUtils.isEmpty(studioInfo.getClasses().trim())) {
            throw new StatusFailException("班级不能为空！");
        }

        return this.save(studioInfo);
    }


    @Override
    public boolean updateStudioInfo(StudioInfo studioInfo) {
        // 参数校验
        if (StringUtils.isEmpty(studioInfo.getName().trim())) {
            throw new StatusFailException("姓名不能为空！");
        }
        if (studioInfo.getWhichSession() == null) {
            throw new StatusFailException("入学时间不能为空！");
        }
        if (StringUtils.isEmpty(studioInfo.getClasses().trim())) {
            throw new StatusFailException("班级不能为空！");
        }
        if (studioInfo.getId() == null || this.getById(studioInfo.getId()) == null) {
            throw new SecurityException("更新失败！请刷新重试！");
        }

        return this.updateById(studioInfo);
    }


    @Override
    public boolean deleteStudioInfo(Integer sid) {
        StudioInfo studioInfo = this.getById(sid);
        if (studioInfo == null) {
            throw new SecurityException("删除失败！该信息不存在！");
        }
        return this.removeById(sid);
    }


}
