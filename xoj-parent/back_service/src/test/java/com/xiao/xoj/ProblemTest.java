package com.xiao.xoj;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiao.xoj.pojo.entity.problem.ProblemDescription;
import com.xiao.xoj.service.ProblemDescriptionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author 肖恩
 * @create 2023/4/19 22:04
 * @description: TODO
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProblemTest {

    @Resource
    private ProblemDescriptionService problemDescriptionService;

    @Test
    public void test01(){
        Integer pid = 2;
        QueryWrapper<ProblemDescription> pdWrapper = new QueryWrapper<>();
        pdWrapper.eq("problem_id", pid);
        ProblemDescription problemDescription = problemDescriptionService.getOne(pdWrapper);
        if (problemDescription != null)
            System.out.println(problemDescription.getDescription());

    }


}
