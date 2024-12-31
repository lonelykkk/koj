package com.xiao.xoj;

import cn.hutool.core.util.StrUtil;
import com.xiao.xoj.pojo.entity.contest.ContestProblem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 肖恩
 * @create 2023/6/1 15:20
 * @description: TODO
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ContestTest {


    @Test
    public void DisplayIdSortTest() {
        List<ContestProblem> list = new LinkedList<>();
        ContestProblem c1 = new ContestProblem().setDisplayId("1").setDisplayTitle("a title");
        ContestProblem c2 = new ContestProblem().setDisplayId("2").setDisplayTitle("b title");
        ContestProblem c3 = new ContestProblem().setDisplayId("3").setDisplayTitle("c title");
        ContestProblem c4 = new ContestProblem().setDisplayId("4").setDisplayTitle("d title");
        list.add(c3);
        list.add(c4);
        list.add(c1);
        list.add(c2);

        List<ContestProblem> res = list.stream().sorted(Comparator.comparing(ContestProblem::getDisplayId)).collect(Collectors.toList());

        res.forEach(e -> {
            System.out.println(e.getDisplayId() + "---" + e.getDisplayTitle());
        });
    }

    @Test
    public void StrUtilTest() {
        String s1 = "123";
        System.out.println("res = " + StrUtil.isBlank(s1)); // false

    }

}
