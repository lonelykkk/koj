package com.xiao.xoj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.entity.contest.ContestCorrection;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.xoj.pojo.vo.ContestCorrectionVO;
import com.xiao.xoj.pojo.vo.ContestStudentInfoVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-07-14
 */
public interface ContestCorrectionService extends IService<ContestCorrection> {

    IPage<ContestStudentInfoVO> getContestStudentInfo(Integer limit, Integer currentPage, String keyword, Integer cid);

    boolean addContestCorrection(ContestCorrection contestCorrection);

    ContestCorrectionVO getContestCorrectionInfo(Integer cid, String userId);

    boolean updateContestCorrection(ContestCorrection contestCorrection);

    boolean deleteContestCorrectionInfo(Integer contestCorrectionId);

    boolean sendCorrectionEmail(Integer contestId);
}
