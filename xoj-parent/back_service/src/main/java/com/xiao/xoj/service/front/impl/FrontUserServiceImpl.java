package com.xiao.xoj.service.front.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.common.exception.StatusForbiddenException;
import com.xiao.xoj.common.exception.StatusSystemErrorException;
import com.xiao.xoj.mapper.ContestCorrectionMapper;
import com.xiao.xoj.mapper.UserInfoMapper;
import com.xiao.xoj.pojo.dto.*;
import com.xiao.xoj.pojo.entity.contest.ContestCorrection;
import com.xiao.xoj.pojo.entity.user.Role;
import com.xiao.xoj.pojo.entity.user.UserInfo;
import com.xiao.xoj.pojo.entity.user.UserRole;
import com.xiao.xoj.pojo.vo.*;
import com.xiao.xoj.service.UserRoleService;
import com.xiao.xoj.service.email.EmailService;
import com.xiao.xoj.service.front.FrontUserService;
import com.xiao.xoj.shiro.AccountProfile;
import com.xiao.xoj.utils.Constants;
import com.xiao.xoj.utils.JwtUtils;
import com.xiao.xoj.utils.RedisUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author 肖恩
 * @create 2023/6/8 23:41
 * @description: TODO
 */
@Service
public class FrontUserServiceImpl implements FrontUserService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private JwtUtils jwtUtils;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Autowired
    private EmailService emailService;

    @Resource
    private ContestCorrectionMapper contestCorrectionMapper;

    @Override
    public UserInfoVO login(LoginDTO loginDTO) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        // 去除账号密码的首尾空格
        loginDTO.setUsername(loginDTO.getUsername().trim());
        loginDTO.setPassword(loginDTO.getPassword().trim());

        String key = Constants.Account.TRY_LOGIN_NUM.getCode() + loginDTO.getUsername();
        Integer tryLoginCount = (Integer) redisUtils.get(key);

        if (tryLoginCount != null && tryLoginCount >= 20) {
            throw new StatusFailException("对不起！登录失败次数过多！您的账号有风险，半个小时内暂时无法登录！");
        }

        UserRolesVO userRolesVO = userRoleService.getUserRoles(null, loginDTO.getUsername());
        if (userRolesVO == null) {
            throw new StatusFailException("账号错误！请重新尝试！");
        }

        if (!userRolesVO.getPassword().equals(SecureUtil.md5(loginDTO.getPassword()))) {
            if (tryLoginCount == null) {
                redisUtils.set(key, 1, 60 * 30); // 三十分钟不尝试，该限制会自动清空消失
            } else {
                redisUtils.set(key, tryLoginCount + 1, 60 * 30);
            }
            throw new StatusFailException("用户名或密码错误！请注意大小写！");
        }

        if (userRolesVO.getIsDisabled()) {
            throw new StatusFailException("该账户已被封禁！请联系管理员处理！");
        }

        // 生成token
        String token = jwtUtils.generateToken(userRolesVO.getId());
        // 将token放入响应头中
        response.setHeader("Authorization", token);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");

        // 登录成功，清除限制
        if (tryLoginCount != null) {
            redisUtils.del(key);
        }

        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtil.copyProperties(userRolesVO, userInfoVO, "roles");
        userInfoVO.setRoleList(userRolesVO.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList()));

        return userInfoVO;
    }


    @Override
    public RegisterCodeVO getRegisterCode(String email) {
        email = email.trim();

        // 邮箱格式校验
        boolean isEmail = Validator.isEmail(email);
        if (!isEmail) {
            throw new StatusFailException("邮箱格式不正确！");
        }

        // 请求次数限制，用户每分钟只能请求一次
        String lockKey = Constants.Email.REGISTER_EMAIL_LOCK.getValue() + email;
        if (redisUtils.hasKey(lockKey)) {
            throw new StatusFailException("对不起！您操作的频率过快，请在" + redisUtils.getExpire(lockKey) + "秒后再次尝试！");
        }

        // 邮箱是否已经被注册
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        UserInfo exitUserInfo = userInfoMapper.selectOne(queryWrapper);
        if (exitUserInfo != null) {
            throw new StatusFailException("该邮箱已经被注册！请更换邮箱！");
        }

        // 生成6位验证码并设置失效时间
        String number = RandomUtil.randomNumbers(6);
        redisUtils.set(Constants.Email.REGISTER_KEY_PREFIX.getValue() + email, number, 5 * 60); // 默认生效时间为5分钟
        redisUtils.set(Constants.Email.REGISTER_EMAIL_LOCK.getValue() + email, 1, 60);
        emailService.sendCode(email, number);

        RegisterCodeVO registerCodeVO = new RegisterCodeVO();
        registerCodeVO.setEmail(email);
        registerCodeVO.setExpire(5 * 60);

        return registerCodeVO;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(RegisterDTO registerDto) {

        registerDto.setUsername(registerDto.getUsername().trim());
        registerDto.setPassword(registerDto.getPassword().trim());
        registerDto.setEmail(registerDto.getEmail().trim());

        String codeKey = Constants.Email.REGISTER_KEY_PREFIX.getValue() + registerDto.getEmail();
        if (!redisUtils.hasKey(codeKey)) {
            throw new StatusFailException("验证码已经过期！请重新发送！");
        }

        if (!redisUtils.get(codeKey).equals(registerDto.getCode())) {
            throw new StatusFailException("验证码错误");
        }

        UserInfo userInfo = new UserInfo();
        String userId = IdUtil.simpleUUID();
        userInfo.setId(userId);
        userInfo.setUsername(registerDto.getUsername());
        userInfo.setPassword(SecureUtil.md5(registerDto.getPassword()));
        userInfo.setEmail(registerDto.getEmail());

        // 往user_info表中插入数据
        int addUser = userInfoMapper.insert(userInfo);

        // 往user_role表中插入数据
        boolean addUserRole = userRoleService.save(new UserRole().setUserId(userId).setRoleId(3));

        if (addUser != 1 || !addUserRole) {
            throw new StatusFailException("注册失败！请重新尝试！");
        }
        return true;
    }


    @Override
    public void logout() {
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        jwtUtils.cleanToken(accountProfile.getId());
        SecurityUtils.getSubject().logout();
    }


    @Override
    public UserInfoVO getUserInfo() {
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        UserRolesVO userRolesVO = userRoleService.getUserRoles(accountProfile.getId(), null);
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtil.copyProperties(userRolesVO, userInfoVO, "roles");
        userInfoVO.setRoleList(userRolesVO.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList()));
        return userInfoVO;
    }


    @Override
    public CheckUsernameOrEmailVO checkUsernameOrEmail(CheckUsernameOrEmailDTO checkUsernameOrEmailDTO) {

        String email = null;
        String username = null;
        if (!StringUtils.isEmpty(checkUsernameOrEmailDTO.getEmail())) {
            email = checkUsernameOrEmailDTO.getEmail().trim();
        }
        if (!StringUtils.isEmpty(checkUsernameOrEmailDTO.getUsername())) {
            username = checkUsernameOrEmailDTO.getUsername().trim();
        }

        boolean rightEmail = false;
        boolean rightUsername = false;

        if (!StringUtils.isEmpty(email)) {
            email = email.trim();
            boolean isEmail = Validator.isEmail(email);
            if (!isEmail) {
                rightEmail = false;
            } else {
                QueryWrapper<UserInfo> wrapper = new QueryWrapper<UserInfo>().eq("email", email);
                UserInfo user = userInfoMapper.selectOne(wrapper);
                if (user != null) {
                    rightEmail = true;
                } else {
                    rightEmail = false;
                }
            }
        }

        if (!StringUtils.isEmpty(username)) {
            username = username.trim();
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<UserInfo>().eq("username", username);
            UserInfo user = userInfoMapper.selectOne(wrapper);
            if (user != null) {
                rightUsername = true;
            } else {
                rightUsername = false;
            }
        }

        CheckUsernameOrEmailVO checkUsernameOrEmailVo = new CheckUsernameOrEmailVO();
        checkUsernameOrEmailVo.setEmail(rightEmail);
        checkUsernameOrEmailVo.setUsername(rightUsername);
        return checkUsernameOrEmailVo;
    }


    @Override
    public UserInfoVO changeUserInfo(UserInfoVO userInfoVO) {

        String realname = userInfoVO.getRealname();
        String nickname = userInfoVO.getNickname();
        String number = userInfoVO.getNumber();
        String classe = userInfoVO.getClasse();
        if (!StringUtils.isEmpty(realname) && realname.length() > 50) {
            throw new StatusFailException("真实姓名的长度不能超过50位");
        }
        if (!StringUtils.isEmpty(nickname) && nickname.length() > 20) {
            throw new StatusFailException("昵称的长度不能超过20位");
        }
        if (!StringUtils.isEmpty(number) && number.length() != 10) {
            throw new StatusFailException("学号格式错误！（长度为10，例：6020201678）");
        }
        if (!StringUtils.isEmpty(classe) && classe.length() != 6) {
            throw new StatusFailException("班级格式错误！（长度为6，例：软件2001）");
        }

        // 获取当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", accountProfile.getId())
                .set("avatar", userInfoVO.getAvatar())
                .set("realname", realname)
                .set("nickname", nickname)
                .set("classe", classe)
                .set("number", number)
                .set("sex", userInfoVO.getSex())
                .set("sign", userInfoVO.getSign());

        UserInfo userInfo = userInfoMapper.selectById(accountProfile.getId());
        int update = userInfoMapper.update(userInfo, updateWrapper);
        if (update == 1) {
            // 更新session
            UserRolesVO newAccountProfile = userRoleService.getUserRoles(accountProfile.getId(), null);
            BeanUtil.copyProperties(newAccountProfile, accountProfile);
            UserInfoVO newUserInfoVO = new UserInfoVO();
            BeanUtil.copyProperties(newAccountProfile, newUserInfoVO, "roles");
            userInfoVO.setRoleList(newAccountProfile.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
            return newUserInfoVO;
        } else {
            throw new StatusFailException("更新个人信息失败！");
        }

    }

    @Override
    public String changePassword(ChangePasswordDTO changePasswordDTO) {
        String oldPassword = changePasswordDTO.getOldPassword().trim();
        String newPassword = changePasswordDTO.getNewPassword().trim();

        if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword)) {
            throw new StatusFailException("错误：原始密码或新密码不能为空！");
        }
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            throw new StatusFailException("新密码长度应该为6~20位！");
        }

        // 获取当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        String lockKey = Constants.Account.CODE_CHANGE_PASSWORD_LOCK.getCode() + accountProfile.getId();
        String tryCountKey = Constants.Account.CODE_CHANGE_PASSWORD_FAIL.getCode() + accountProfile.getId();

        if (redisUtils.hasKey(lockKey)) {
            long expire = redisUtils.getExpire(lockKey);
            Date now = new Date();
            long minute = expire / 60;
            long second = expire % 60;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date afterDate = new Date(now.getTime() + expire * 1000);
            String msg = "由于您多次修改密码失败，修改密码功能已锁定，请在" + minute + "分" + second + "秒后(" + formatter.format(afterDate) + ")再进行尝试！";
            return msg;
        }


        UserInfo userInfo = userInfoMapper.selectById(accountProfile.getId());
        // 判断密码是否正确
        if (userInfo.getPassword().equals(SecureUtil.md5(changePasswordDTO.getOldPassword()))) {
            UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("password", SecureUtil.md5(newPassword))// 数据库用户密码全部用md5加密
                    .eq("id", accountProfile.getUid());
            int isOk = userInfoMapper.update(userInfo, updateWrapper);
            if (isOk == 1) {
                String msg = "修改密码成功！您将于5秒钟后退出进行重新登录操作！";
                // 清空记录
                redisUtils.del(tryCountKey);
                return msg;
            } else {
                throw new StatusSystemErrorException("系统错误：修改密码失败！");
            }
        } else {
            Integer count = (Integer) redisUtils.get(tryCountKey);
            if (count == null) {
                redisUtils.set(tryCountKey, 1, 60 * 30); // 三十分钟不尝试，该限制会自动清空消失
                count = 0;
            } else if (count < 5) {
                redisUtils.incr(tryCountKey, 1);
            }
            count++;
            if (count == 5) {
                redisUtils.del(tryCountKey); // 清空统计
                redisUtils.set(lockKey, "lock", 60 * 30); // 设置锁定更改
            }
            return "原始密码错误！您已累计修改密码失败" + count + "次...";
        }
    }

    @Override
    public ChangeCodeVO getChangeCode(String email) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        UserInfo emailExist = userInfoMapper.selectOne(queryWrapper);
        if (emailExist != null) {
            throw new StatusFailException("该邮箱已经被使用！请更换邮箱！");
        }


        String lockKey = Constants.Email.RESET_EMAIL_LOCK.getValue() + email;
        if (redisUtils.hasKey(lockKey)) {
            throw new StatusFailException("对不起！您操作的频率过快，请在" + redisUtils.getExpire(lockKey) + "秒后再次尝试！");
        }

        String code = RandomUtil.randomNumbers(6);
        redisUtils.set(Constants.Email.RESET_EMAIL_PREFIX.getValue() + email, code, 5 * 60); // 验证码有效期：5分钟
        redisUtils.set(Constants.Email.RESET_EMAIL_LOCK.getValue() + email, 1, 60);
        emailService.sendChangeEmailCode(email, code);

        ChangeCodeVO changeCodeVO = new ChangeCodeVO();
        changeCodeVO.setEmail(email);
        changeCodeVO.setExpire(5 * 60);
        return changeCodeVO;
    }

    @Override
    public UserInfoVO changeEmail(ChangeEmailDTO changeEmailDTO) {
        changeEmailDTO.setPassword(changeEmailDTO.getPassword().trim());
        changeEmailDTO.setNewEmail(changeEmailDTO.getNewEmail().trim());
        changeEmailDTO.setCode(changeEmailDTO.getCode().trim());

        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        String lockKey = Constants.Account.CODE_CHANGE_EMAIL_LOCK.getCode() + accountProfile.getId();
        String tryCountKey = Constants.Account.CODE_CHANGE_EMAIL_FAIL.getCode() + accountProfile.getId();

        if (redisUtils.hasKey(lockKey)) {
            long expire = redisUtils.getExpire(lockKey);
            Date now = new Date();
            long minute = expire / 60;
            long second = expire % 60;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date afterDate = new Date(now.getTime() + expire * 1000);
            String msg = "由于您多次修改邮箱失败，修改邮箱功能已锁定，请在" + minute + "分" + second + "秒后(" + simpleDateFormat.format(afterDate) + ")再进行尝试！";
            throw new StatusForbiddenException(msg);
        }

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", changeEmailDTO.getNewEmail());
        UserInfo emailExist = userInfoMapper.selectOne(queryWrapper);
        if (emailExist != null) {
            throw new StatusFailException("该邮箱已经被使用！请更换邮箱！");
        }

        UserInfo userInfo = userInfoMapper.selectById(accountProfile.getId());
        if (userInfo.getPassword().equals(SecureUtil.md5(changeEmailDTO.getPassword()))) {
            UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", accountProfile.getId())
                    .set("email", changeEmailDTO.getNewEmail());
            int isOk = userInfoMapper.update(userInfo, updateWrapper);
            if (isOk == 1) {
                UserRolesVO userRolesVO = userRoleService.getUserRoles(accountProfile.getId(), accountProfile.getUsername());
                UserInfoVO userInfoVO = new UserInfoVO();

                BeanUtil.copyProperties(userRolesVO, userInfoVO, "roles");
                userInfoVO.setRoleList(userRolesVO.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

                redisUtils.del(tryCountKey, lockKey);

                return userInfoVO;
            } else {
                throw new StatusSystemErrorException("系统错误！");
            }
        } else {
            Integer count = (Integer) redisUtils.get(tryCountKey);
            if (count == null) {
                redisUtils.set(tryCountKey, 1, 60 * 30);
                count = 0;
            } else if (count < 5) {
                redisUtils.incr(tryCountKey, 1);
            }
            count ++;
            if (count == 5) {
                redisUtils.del(tryCountKey);
                redisUtils.set(lockKey, "lock", 60 * 30); // 修改邮箱失败五次之后，被锁定30分钟
            }

            throw new StatusFailException("密码错误！您已累计修改邮箱失败" + count + "次...");
        }

    }

    @Override
    public IPage<UserContestInfoVO> getContestList(Integer limit, Integer currentPage) {
        if (limit == null || limit < 1) limit = 10;
        if (currentPage == null || currentPage < 1) currentPage = 1;
        IPage<UserContestInfoVO> page = new Page<>(currentPage, limit);

        // 获取当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        IPage<UserContestInfoVO> userContestInfoPage = contestCorrectionMapper.getUserContestInfo(page, accountProfile.getId());

        // 设置是否批改标记
        for (UserContestInfoVO ucInfo : userContestInfoPage.getRecords()) {
            QueryWrapper<ContestCorrection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("cid", ucInfo.getId())
                    .eq("user_id", accountProfile.getId());
            ContestCorrection contestCorrection = contestCorrectionMapper.selectOne(queryWrapper);
            if (contestCorrection == null) {
                ucInfo.setIsCorrection(false);
            } else {
                ucInfo.setIsCorrection(true);
                ucInfo.setContestCorrection(contestCorrection);
            }
        }

        return userContestInfoPage;
    }
}
