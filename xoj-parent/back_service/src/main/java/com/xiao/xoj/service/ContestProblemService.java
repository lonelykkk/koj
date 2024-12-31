package com.xiao.xoj.service;

import com.xiao.xoj.pojo.entity.contest.ContestProblem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.xoj.pojo.vo.ContestProblemVO;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-30
 */
public interface ContestProblemService extends IService<ContestProblem> {

    List<ContestProblemVO> getContestProblem(Integer cid,
                                             Date startTime,
                                             Date endTime);
}
