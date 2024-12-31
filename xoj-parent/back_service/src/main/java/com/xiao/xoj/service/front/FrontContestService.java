package com.xiao.xoj.service.front;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.dto.RegisterContestDTO;
import com.xiao.xoj.pojo.vo.*;

import java.util.HashMap;
import java.util.List;

public interface FrontContestService {
    IPage<ContestVO> getContestList(Integer limit, Integer currentPage, String keyword);

    HashMap<String, Object> getContestInfo(Integer cid);

    boolean registerContest(RegisterContestDTO registerContestDTO);

    List<ContestProblemVO> getContestProblem(Integer cid);

    ProblemInfoVO getContestProblemDetails(Integer cid, Integer cpid);

    IPage<JudgeVO> getContestSubmissionList(Integer limit, Integer currentPage, Boolean onlyMine, Integer cid);

    IPage<AnnouncementVO> getContestAnnouncement(Integer limit, Integer currentPage, Integer cid);

    IPage<JudgeVO> getContestUserSubmission(Integer limit, Integer currentPage, Integer cid);

    List<ContestVO> getRecentContest();
}
