package com.xiao.xoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.common.exception.StatusForbiddenException;
import com.xiao.xoj.mapper.ContestMapper;
import com.xiao.xoj.mapper.JudgeMapper;
import com.xiao.xoj.mapper.UserInfoMapper;
import com.xiao.xoj.pojo.entity.contest.Contest;
import com.xiao.xoj.pojo.entity.contest.ContestCorrection;
import com.xiao.xoj.mapper.ContestCorrectionMapper;
import com.xiao.xoj.pojo.entity.judge.Judge;
import com.xiao.xoj.pojo.entity.user.UserInfo;
import com.xiao.xoj.pojo.vo.ContestCorrectionVO;
import com.xiao.xoj.pojo.vo.ContestStudentInfoVO;
import com.xiao.xoj.service.ContestCorrectionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.xoj.service.ContestRegisterService;
import com.xiao.xoj.service.email.EmailService;
import com.xiao.xoj.shiro.AccountProfile;
import com.xiao.xoj.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-07-14
 */
@Slf4j(topic = "xoj")
@Service
public class ContestCorrectionServiceImpl extends ServiceImpl<ContestCorrectionMapper, ContestCorrection> implements ContestCorrectionService {

    @Resource
    private ContestRegisterService contestRegisterService;

    @Resource
    private ContestCorrectionMapper contestCorrectionMapper;

    @Resource
    private JudgeMapper judgeMapper;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private EmailService emailService;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private ContestMapper contestMapper;

    private final static String NOW_MODIFIED_USER = "NOW_MODIFIED_USER:";

    private final static Lock lock = new ReentrantLock();

    @Override
    public IPage<ContestStudentInfoVO> getContestStudentInfo(Integer limit, Integer currentPage, String keyword, Integer cid) {

        if (limit == null || limit < 1) limit = 10;
        if (currentPage == null || currentPage < 1) currentPage = 1;

        IPage<ContestStudentInfoVO> page = new Page<>(currentPage, limit);
        IPage<ContestStudentInfoVO> studentInfoPage = contestCorrectionMapper.getContestStudentInfo(page, keyword, cid);

        // 设置批改信息
        Map<String, ContestStudentInfoVO> map = new HashMap<>();
        List<String> studentIds = new LinkedList<>();
        List<ContestStudentInfoVO> studentInfoList = studentInfoPage.getRecords();
        for (ContestStudentInfoVO contestStudentInfoVO : studentInfoList) {
            contestStudentInfoVO.setIsCorrection(false);
            map.put(contestStudentInfoVO.getId(), contestStudentInfoVO);
            studentIds.add(contestStudentInfoVO.getId());
        }

        QueryWrapper<ContestCorrection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", cid).in("user_id", studentIds);
        List<ContestCorrection> correctionList = this.list(queryWrapper);
        for (ContestCorrection contestCorrection : correctionList) {
            if (map.containsKey(contestCorrection.getUserId())) {
                ContestStudentInfoVO contestStudentInfoVO = map.get(contestCorrection.getUserId());
                contestStudentInfoVO.setIsCorrection(true);
                contestStudentInfoVO.setContestCorrection(contestCorrection);
            }
        }

        studentInfoPage.setRecords(studentInfoList);
        return studentInfoPage;
    }

    @Override
    public ContestCorrectionVO getContestCorrectionInfo(Integer cid, String userId) {
        // 获取当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        ContestCorrectionVO contestCorrectionVO = new ContestCorrectionVO();

        // 判断当前是否有管理员修改
        String flagKey = NOW_MODIFIED_USER + cid + ":" + userId; // NOW_MODIFIED_USER:考核id:新生id

        lock.lock(); // 使用悲观锁
        try {
            boolean hasAdminMod = redisUtils.hasKey(flagKey);

            if (hasAdminMod) { // 管理员在修改，并且不是当前用户，无法修改
                if (!redisUtils.get(flagKey).equals(accountProfile.getUsername())) {
                    contestCorrectionVO.setCanMod(false);
                    contestCorrectionVO.setUsername((String) redisUtils.get(flagKey));
                }
            } else { // 没有管理员修改
                redisUtils.set(flagKey, accountProfile.getUsername(), 1800); // 半个小时后过期，其他管理员可以修改
                contestCorrectionVO.setCanMod(true);
            }
        } finally {
            lock.unlock();
        }


        QueryWrapper<ContestCorrection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("cid", cid);
        ContestCorrection contestCorrection = this.getOne(queryWrapper);
        if (contestCorrection != null) {
            contestCorrectionVO.setContestCorrection(contestCorrection);
        }

        List<Judge> judges = judgeMapper.getProblemFinalCode(cid, userId);
        contestCorrectionVO.setCodes(judges);

        return contestCorrectionVO;
    }

    @Override
    public boolean addContestCorrection(ContestCorrection contestCorrection) {
        // 获取当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        // 判断是否可以添加
        String flagKey = NOW_MODIFIED_USER + contestCorrection.getCid() + ":" + contestCorrection.getUserId();
        boolean nowHasUser = redisUtils.hasKey(flagKey);
        if (nowHasUser) {
            String nowModUser = (String) redisUtils.get(flagKey);
            if (!nowModUser.equals(accountProfile.getUsername())) {
                throw new StatusForbiddenException("您当前无法添加，" + nowModUser + "正在操作该份考核");
            }
        }

        if (StringUtils.isEmpty(contestCorrection.getContent())) {
            throw new StatusFailException("批改内容不能为空！");
        }

        contestCorrection.setModifiedBy(accountProfile.getRealname());

        boolean isOk = this.save(contestCorrection);
        if (isOk) {
            redisUtils.del(flagKey);
        }
        return isOk;
    }

    @Override
    public boolean updateContestCorrection(ContestCorrection contestCorrection) {
        // 获取当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        // 判断是否可以添加
        String flagKey = NOW_MODIFIED_USER + contestCorrection.getCid() + ":" + contestCorrection.getUserId();
        boolean nowHasUser = redisUtils.hasKey(flagKey);
        if (nowHasUser) {
            String nowModUser = (String) redisUtils.get(flagKey);
            if (!nowModUser.equals(accountProfile.getUsername())) {
                throw new StatusForbiddenException("您当前无法添加，" + nowModUser + "正在操作该份考核");
            }
        }

        if (StringUtils.isEmpty(contestCorrection.getContent())) {
            throw new StatusFailException("批改内容不能为空！");
        }

        contestCorrection.setModifiedBy(accountProfile.getRealname());

        return this.updateById(contestCorrection);
    }

    @Override
    public boolean deleteContestCorrectionInfo(Integer contestCorrectionId) {
        ContestCorrection contestCorrection = this.getById(contestCorrectionId);
        if (contestCorrection == null) {
            throw new StatusFailException("删除失败！该批改信息不存在！");
        }
        return this.removeById(contestCorrectionId);
    }

    @Override
    public boolean sendCorrectionEmail(Integer contestId) {
        // 获取本次考核的批改信息
        QueryWrapper<ContestCorrection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", contestId);
        List<ContestCorrection> corrections = this.list(queryWrapper);

        // 发送邮件通知
        for (ContestCorrection contestCorrection : corrections) {
            // 获取用户信息 和 考核标题
            UserInfo userInfo = userInfoMapper.selectById(contestCorrection.getUserId());
            Contest contest = contestMapper.selectById(contestCorrection.getCid());
            String content = contest.getTitle() + ": 您的本次考核评价已发送！请及时查看！";
            boolean isSendOk = emailService.sendContestCorrectionInfo(userInfo.getEmail(), content);
            if (!isSendOk) {
                log.error("邮件发送失败！考核信息：{} ，用户名：{} ，用户邮箱：{}", contest.getTitle(), userInfo.getUsername(), userInfo.getEmail());
            }
        }
        return true;
    }
}
