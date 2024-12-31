package com.xiao.xoj.utils;

import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.pojo.entity.contest.Contest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author 肖恩
 * @create 2023/6/1 16:36
 * @description: TODO
 */
@Component
public class ContestValidatorUtil {


    public void checkParams(Contest contest, boolean isAdd) {
        if (!isAdd && contest.getId() == null) {
            throw new StatusFailException("更新失败！请重试！");
        }

        if (StringUtils.isEmpty(contest.getTitle())) {
            throw new StatusFailException("考核标题不能为空！");
        }

        if (StringUtils.isEmpty(contest.getDescription())) {
            throw new StatusFailException("考核描述不能为空！");
        }

        if (contest.getAuth() == 1 && StringUtils.isEmpty(contest.getPwd())) {
            throw new StatusFailException("请设置考核密码！");
        }

        if (contest.getStartTime() == null) {
            throw new StatusFailException("请设置考核开始时间！");
        }

        if (contest.getEndTime() == null) {
            throw new StatusFailException("请设置考核结束时间！");
        }
    }
}
