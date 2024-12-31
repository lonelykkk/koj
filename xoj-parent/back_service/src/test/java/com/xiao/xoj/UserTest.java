package com.xiao.xoj;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiao.xoj.pojo.entity.contest.ContestCorrection;
import com.xiao.xoj.pojo.entity.resource.Resource;
import com.xiao.xoj.pojo.entity.user.Role;
import com.xiao.xoj.pojo.entity.user.UserInfo;
import com.xiao.xoj.pojo.entity.user.UserRole;
import com.xiao.xoj.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/6/9 17:07
 * @description: TODO
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ContestCorrectionService contestCorrectionService;


    @Test
    public void mybatisTest() {
        QueryWrapper<ContestCorrection> queryWrapper = new QueryWrapper<>();
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("a43946877f194bceabdcc95fd4d29bed");
        queryWrapper.eq("cid", 1)
                    .in("user_id", list);

        List<ContestCorrection> res = contestCorrectionService.list(queryWrapper);
        for (ContestCorrection contestCorrection : res) {
            System.out.println(contestCorrection);
        }
    }


    @Test
    public void initInfo() {
        Role root = new Role();
        root.setName("root");
        root.setDescription("超级管理员");
        Role admin = new Role();
        admin.setName("admin");
        admin.setDescription("管理员");
        Role user = new Role();
        user.setName("user");
        user.setDescription("普通用户");

        UserInfo userInfo = new UserInfo();
        userInfo.setId("1");
        userInfo.setUsername("root");
        userInfo.setPassword("root");
        userInfo.setNickname("testNickname");
        userInfo.setAvatar("https://img-home.csdnimg.cn/images/20201124032511.png");
        userInfo.setSign("test");
        userInfo.setRealname("xiaoen");
        userInfo.setNumber("6020201678");
        userInfo.setClasse("test");
        userInfo.setSex(1);
        userInfo.setEmail("124312@qq.com");

        List<Role> roleList = new ArrayList<Role>();
        roleList.add(root);
        roleList.add(admin);
        roleList.add(user);
        boolean isSaveRole = roleService.saveBatch(roleList);
        boolean isSaveUser = userInfoService.save(userInfo);

        if (isSaveRole && isSaveUser) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userInfo.getId());
            userRole.setRoleId(root.getId());
            userRoleService.save(userRole);
            System.out.println("初始化信息成功");
        }
    }

    @Test
    public void md5Test() {
        Resource resource = resourceService.getById(1);
        resource.setId(null);
        for (int i = 0; i < 10; i ++) {
            resource.setName(resource.getName() + i);
            resourceService.save(resource);
        }
        System.out.println(SecureUtil.md5("123456"));
    }

}
