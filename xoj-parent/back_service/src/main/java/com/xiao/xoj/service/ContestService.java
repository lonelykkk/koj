package com.xiao.xoj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.dto.AnnouncementDTO;
import com.xiao.xoj.pojo.dto.ContestProblemDTO;
import com.xiao.xoj.pojo.entity.contest.Contest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.pojo.vo.AnnouncementVO;
import com.xiao.xoj.pojo.vo.ContestVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-30
 */
public interface ContestService extends IService<Contest> {

    IPage<Contest> getContestList(Integer limit, Integer currentPage, String keyword);

    Contest getContest(Integer cid);

    boolean addContest(Contest contest);

    boolean deleteContest(Integer cid);

    boolean updateContest(Contest contest);

    IPage<Problem> getProblemList(Integer limit, Integer currentPage, String keyword, Long cid);

    boolean addProblemFromPublic(ContestProblemDTO contestProblemDto);

    boolean updateProblem(ContestProblemDTO contestProblemDto);

    boolean removeProblem(Integer cid, Integer pid);

    IPage<AnnouncementVO> getAnnouncementList(Integer limit, Integer currentPage, Integer cid);

    boolean addAnnouncement(AnnouncementDTO announcementDto);

    boolean deleteAnnouncement(Integer aid);

    boolean updateAnnouncement(AnnouncementDTO announcementDto);

}
