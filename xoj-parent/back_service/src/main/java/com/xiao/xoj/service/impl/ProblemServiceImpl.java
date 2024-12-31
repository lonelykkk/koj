package com.xiao.xoj.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.mapper.ContestMapper;
import com.xiao.xoj.pojo.dto.ProblemDto;
import com.xiao.xoj.pojo.entity.contest.Contest;
import com.xiao.xoj.pojo.entity.contest.ContestProblem;
import com.xiao.xoj.pojo.entity.problem.*;
import com.xiao.xoj.mapper.ProblemMapper;
import com.xiao.xoj.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.xoj.shiro.AccountProfile;
import com.xiao.xoj.utils.Constants;
import org.apache.shiro.SecurityUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-03-19
 */
@Service
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements ProblemService {

    @Resource
    private ProblemTagService problemTagService;

    @Resource
    private ProblemCaseService problemCaseService;

    @Resource
    private ProblemDescriptionService problemDescriptionService;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private ContestMapper contestMapper;

    @Resource
    private ContestProblemService contestProblemService;

    private final static Pattern EOL_PATTERN = Pattern.compile("[^\\S\\n]+(?=\\n)");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addProblem(ProblemDto problemDto) {
        // 校验题目参数
        Problem problem = problemDto.getProblem();
        if (problem.getTimeLimit() <= 0) {
            throw new StatusFailException("题目的时间限制不能小于0！");
        }
        if (problem.getStackLimit() <= 0) {
            throw new StatusFailException("题目的栈内存限制不能小于0！");
        }
        if (problem.getMemoryLimit() <= 0) {
            throw new StatusFailException("题目的内存限制不能小于0！");
        }

        // 检查题目ID是否已经存在
        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("problem_id", problem.getProblemId().toUpperCase());
        Problem tmpProblem = this.getOne(queryWrapper);
        if (tmpProblem != null) {
            throw new StatusFailException("题目ID已经存在，请更换！");
        }

        // 获取创建题目的管理员信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        // 设置测试样例的版本号
        problem.setCaseVersion(String.valueOf(System.currentTimeMillis()));
        // 设置创建者
        problem.setAuthor(accountProfile.getUsername());
        problem.setIsUploadCase(!problemDto.getIsUploadZipTestCase()); // 0:zip  1:手动

        // 判断题目类型，设置题目分数
        if (problem.getType().intValue() == Constants.Contest.TYPE_OI.getCode()) {
            problem.setIoScore(100); // io类型都设置为100
        } else {
            problem.setIoScore(0);
        }

        // 设置题目的展示ID，并将problem保存到数据库中
        if (StrUtil.isBlank(problem.getProblemId())) {
            problem.setProblemId(IdUtil.simpleUUID());
            this.save(problem);
            String problemId = "P" + problem.getId();
            UpdateWrapper<Problem> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("problem_id", problemId);
            updateWrapper.eq("id", problem.getId());
            this.update(null, updateWrapper);
            problem.setProblemId(problemId);
        } else {
            String problemId = problem.getProblemId().toUpperCase();
            problem.setProblemId(problemId);
            this.save(problem);
        }

        // 检查题目基本信息是否插入成功
        Integer pid = problem.getId();
        if (pid == null) {
            throw new StatusFailException("题目ID为 " + problem.getProblemId() + " 的题目信息保存失败！");
        }

        // 添加题目标签：ProblemTag
        List<Tag> tagList = problemDto.getTagList();
        if (tagList != null && tagList.size() > 0) {
            List<ProblemTag> problemTags = new ArrayList<>();
            // 设置pid和tid
            for (Tag tag : tagList) {
                ProblemTag pt = new ProblemTag();
                pt.setProblemId(pid);
                pt.setTagId(tag.getId());
                problemTags.add(pt);
            }
            boolean addProblemTagResult = problemTagService.saveOrUpdateBatch(problemTags);
            if (!addProblemTagResult) {
                throw new StatusFailException("添加题目标签失败，请重新尝试！");
            }
        }

        // 添加题目描述
        ProblemDescription problemDescription = new ProblemDescription();
        if (StrUtil.isBlank(problemDto.getDescription())) {
            throw new StatusFailException("题目描述不能为空，请填写提米描述！");
        }
        problemDescription.setDescription(problemDto.getDescription());
        problemDescription.setProblemId(pid);
        boolean addProblemDes = problemDescriptionService.save(problemDescription);
        if (!addProblemDes) {
            throw new StatusFailException("题目描述添加失败");
        }

        // 添加题目测试用例，ProblemCase
        // 是否上传zip
        boolean addProblemCaseResult = true;
        if (problemDto.getIsUploadZipTestCase()) { // 是
            // 上传了zip文件
            // 获取评测数据文件名
            List<ProblemCase> problemCases = this.getProblemCases(problemDto.getUploadFileDir(), problemDto.getZipFileDir(), pid);

            // 保存到数据库中
            addProblemCaseResult = problemCaseService.saveOrUpdateBatch(problemCases);

            // 获取代理bean对象执行异步方法 ---> 根据测试文件初始info
            applicationContext.getBean(ProblemServiceImpl.class).initUploadTestcase(
                    problem.getCaseVersion(),
                    pid,
                    problemDto.getUploadFileDir(),
                    problemDto.getZipFileDir(),
                    problemCases
            );
        } else {
            // 手动输入评测数据
            // 数据检查
            List<String> inputList = problemDto.getInputSamples();
            List<String> outputList = problemDto.getOutputSamples();
            if (inputList == null || outputList == null || inputList.size() == 0 || inputList.size() != outputList.size()) {
                throw new StatusFailException("手动输入的评测数据有误，请重新输入");
            }
            List<ProblemCase> problemCases = new ArrayList<>();
            for (int i = 0; i < inputList.size(); i ++) {
                ProblemCase ps = new ProblemCase();
                ps.setProblemId(pid);
                ps.setInput((i + 1) + ".in"); // 输入文件名
                ps.setOutput((i + 1) + ".out"); // 输出文件名
                ps.setScore(0); // 全部设置为0
                problemCases.add(ps);
            }

            // 保存到数据库中
            addProblemCaseResult = problemCaseService.saveOrUpdateBatch(problemCases);

            // 获取代理bean对象执行异步方法 ---> 根据测试文件初始info
            applicationContext.getBean(ProblemServiceImpl.class).initHandTestcase(
                    problem.getCaseVersion(),
                    pid,
                    problemCases
            );
        }

        if (!addProblemCaseResult) {
            throw new StatusFailException("评测数据添加失败！");
        }

        return true;
    }

    // todo 需要修改
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProblem(ProblemDto problemDto) {
        // 参数校验
        Problem problem = problemDto.getProblem();
        if (problem.getId() == null) {
            throw new StatusFailException("题目id不能为空！");
        }
        if (problem.getTimeLimit() <= 0) {
            throw new StatusFailException("题目的时间限制不能小于0！");
        }
        if (problem.getStackLimit() <= 0) {
            throw new StatusFailException("题目的栈内存限制不能小于0！");
        }
        if (problem.getMemoryLimit() <= 0) {
            throw new StatusFailException("题目的内存限制不能小于0！");
        }

        // 检查题目ID是否已经存在
        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("problem_id", problem.getProblemId().toUpperCase());
        Problem tmpProblem = this.getOne(queryWrapper);
        // 如果problem_id存在且与修改前的id不同，则表示修改失败
        if (tmpProblem != null && tmpProblem.getId() != problem.getId().intValue()) {
            throw new StatusFailException("题目ID已经存在，请更换！");
        }

        // 记录修改题目的用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        problem.setModifiedUser(accountProfile.getUsername());


        // 查询与原来题目相关联的数据
        Map<String, Object> map = new HashMap<>();
        Integer pid = problem.getId();
        map.put("problem_id", pid);

        List<ProblemCase> oldProblemCase = (List<ProblemCase>) problemCaseService.listByMap(map);
        List<ProblemTag> oldProblemTag = (List<ProblemTag>) problemTagService.listByMap(map);

        Map<Integer, Integer> mapOldPC = new HashMap<>();
        Map<Integer, Integer> mapOldPT = new HashMap<>();
        List<Integer> needDeletePC = new LinkedList<>();

        // 记录原有的数据
        oldProblemCase.stream().forEach(problemCase -> {
            needDeletePC.add(problemCase.getId());
            mapOldPC.put(problemCase.getId(), 0);
        });
        oldProblemTag.stream().forEach(problemTag -> {
            mapOldPT.put(problemTag.getTagId(), problemTag.getId());
        });


        // 与上传的数据进行对比，进行添加或删除
        // 处理problem_tag表的更新和删除
        for (Tag tag : problemDto.getTagList()) {
            if (!mapOldPT.containsKey(tag.getId())) { // 新添加的标签
                problemTagService.save(new ProblemTag().setProblemId(pid).setTagId(tag.getId()));
            } else { // 更新
                mapOldPT.remove(tag.getId());
            }
        }
        // 删除problem_tag
        boolean deleteProblemTagResult = true;
        List<Integer> pTagIdList = new ArrayList<>();
        for (Integer key : mapOldPT.keySet()) {
            pTagIdList.add(mapOldPT.get(key));
        }
        if (pTagIdList.size() > 0) {
            deleteProblemTagResult = problemTagService.removeByIds(pTagIdList);
        }
        if (!deleteProblemTagResult) {
            throw new StatusFailException("题目标签更新失败");
        }


        // 处理problem_case表的更新和删除
        // 相当于从新上传测试数据，需要将之前的相关数据清除
        if (problemDto.getIsUpdateTestcase()) {
            // 清除原来的数据
            String testcaseDir = Constants.File.TESTCASE_BASE_FOLDER.getPath() + File.separator + "problem_" + pid;
            FileUtil.del(testcaseDir);
            problemCaseService.removeByMap(map);
            // 设置新的版本号
            problem.setCaseVersion(String.valueOf(System.currentTimeMillis()));
            boolean addProblemCaseResult = true;
            if (problemDto.getIsUploadZipTestCase()) {
                // 上传了zip文件
                System.out.println("上传了zip文件");
                // 获取评测数据文件名
                List<ProblemCase> problemCases = this.getProblemCases(problemDto.getUploadFileDir(), problemDto.getZipFileDir(), pid);
                addProblemCaseResult = problemCaseService.saveOrUpdateBatch(problemCases);
                // 获取代理bean对象执行异步方法 ---> 根据测试文件初始info
                applicationContext.getBean(ProblemServiceImpl.class).initUploadTestcase(
                        problem.getCaseVersion(),
                        pid,
                        problemDto.getUploadFileDir(),
                        problemDto.getZipFileDir(),
                        problemCases
                );
            } else {
                // 手动输入评测数据
                System.out.println("手动输入评测数据");
                // 数据检查
                List<String> inputList = problemDto.getInputSamples();
                List<String> outputList = problemDto.getOutputSamples();
                if (inputList == null || outputList == null || inputList.size() == 0 || inputList.size() != outputList.size()) {
                    throw new StatusFailException("手动输入的评测数据有误，请重新输入");
                }
                List<ProblemCase> problemCases = new ArrayList<>();
                for (int i = 0; i < inputList.size(); i ++) {
                    String inputS = inputList.get(i); // 输入的数据
                    String outputS = outputList.get(i); // 输出的数据
                    ProblemCase ps = new ProblemCase();
                    ps.setProblemId(pid);
                    ps.setInput(inputS);
                    ps.setOutput(outputS);
                    ps.setScore(0); // 全部设置为0
                    problemCases.add(ps);
                }
                // 获取代理bean对象执行异步方法 ---> 根据测试文件初始info
                applicationContext.getBean(ProblemServiceImpl.class).initHandTestcase(
                        problem.getCaseVersion(),
                        pid,
                        problemCases
                );
            }

            if (!addProblemCaseResult) {
                throw new StatusFailException("评测数据添加失败！");
            }
        }

        // 更新problem_description
        ProblemDescription oldDesc = problemDescriptionService.getOne(new QueryWrapper<ProblemDescription>().eq("problem_id", pid));
        if (!oldDesc.getDescription().equals(problemDto.getDescription())) {
            oldDesc.setDescription(problemDto.getDescription());
            boolean isOk = problemDescriptionService.updateById(oldDesc);
            if (!isOk) {
                throw new StatusFailException("题目描述更新失败！");
            }
        }

        // 将最后的数据添加到表中
        boolean isOk = this.updateById(problem);
        if (!isOk) {
            throw new StatusFailException("题目更新失败");
        }
        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProblem(Integer pid) {
        boolean isOk = this.removeById(pid);
        boolean isOkDesc = problemDescriptionService.removeById(pid);
        boolean isOkTestcase = problemCaseService.remove(new QueryWrapper<ProblemCase>().eq("problem_id", pid));
        //boolean isOkTag = problemTagService.remove(new QueryWrapper<ProblemTag>().eq("problem_id", pid));
        if (!isOk || !isOkDesc || !isOkTestcase) {
            throw new StatusFailException("删除失败");
        }
        // 删除与该pid相关的数据
        FileUtil.del(Constants.File.TESTCASE_BASE_FOLDER.getPath() + File.separator + "problem_" + pid);
        return true;
    }


    @Override
    public IPage<Problem> getAllProblem(Integer limit, Integer currPage, String keyword, Integer auth, Integer cid) {
        if (currPage == null || currPage < 1) currPage = 1;
        if (limit == null || limit < 1) limit = 10;

        IPage<Problem> iPage = new Page<>(currPage, limit);

        Contest contest = null;
        if (cid != null) {
            contest = contestMapper.selectById(cid);
        }

        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        if (auth != null && auth != 0) {
            // 如何cid不为空，只能查询公开的题目，且题目类型要与考核的类型相同
            if (contest != null) {
                queryWrapper.eq("auth", 1)
                        .eq("type", contest.getType());
            } else {
                queryWrapper.eq("auth", auth);
            }
        }
        if (!StringUtils.isEmpty(keyword)) {
            final String key = keyword.trim();
            queryWrapper.and(wrapper -> wrapper.like("title", key).or()
                    .like("author", key).or()
                    .like("problem_id", key));
        }


        IPage<Problem> problemIPage = this.page(iPage, queryWrapper);
        // 已添加的题目不需要显示
        if (contest != null) {
            QueryWrapper<ContestProblem> contestProblemQueryWrapper = new QueryWrapper<>();
            contestProblemQueryWrapper.eq("cid", contest.getId());
            List<ContestProblem> cpList = contestProblemService.list(contestProblemQueryWrapper);

            Set<Integer> exitProblem = new HashSet<>();
            cpList.forEach(cp -> exitProblem.add(cp.getPid()));
            List<Problem> filtratedProblem = new ArrayList<>();
            problemIPage.getRecords().forEach(p -> {
                if (!exitProblem.contains(p.getId()))
                    filtratedProblem.add(p);
            });

            problemIPage.setRecords(filtratedProblem);
        }

        return problemIPage;
    }

    @Override
    public Problem getProblem(Integer pid) {
        Problem problem = this.getById(pid);
        if (problem == null) {
            throw new StatusFailException("查询失败！");
        }
        return problem;
    }


    /**
     * @des： 初始化上传文件的测试数据，写成json文件
     *
     * @param caseVersion
     * @param pid
     * @param uploadFileDir
     * @param zipFileDir
     * @param problemCases
     * @return
     */
    @Async
    public void initUploadTestcase(String caseVersion,
                                   Integer pid,
                                   String uploadFileDir,
                                   String zipFileDir,
                                   List<ProblemCase> problemCases) {
        System.out.println("zip 开始初始化");

        // 将之前的临时文件夹里面的评测文件全部复制到指定文件夹，并覆盖之前的文件
        String testcaseDir = Constants.File.TESTCASE_BASE_FOLDER.getPath() + File.separator + "problem_" + pid;
        if (!StringUtils.isEmpty(zipFileDir)) {
            FileUtil.clean(testcaseDir); // 清空评测数据最后保存的文件夹
            FileUtil.copyFilesFromDir(new File(zipFileDir), new File(testcaseDir), true);
        }

        // 将题目信息转换为json文件格式保存
        JSONObject result = new JSONObject();
        result.set("mode", Constants.JudgeMode.DEFAULT.getMode()); // 全部设置为default
        result.set("version", caseVersion);
        result.set("testcaseSize", problemCases.size());

        JSONArray jsonArray = new JSONArray();
        List<String> fileNames = FileUtil.listFileNames(zipFileDir);
        for (ProblemCase ps : problemCases) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("caseId", ps.getId());
            jsonObject.set("inputName", ps.getInput());
            jsonObject.set("outputName", ps.getOutput());
            jsonObject.set("score", ps.getScore());

            fileNames.remove(ps.getInput());
            fileNames.remove(ps.getOutput());

            // 重新写入输入输出文件，解决Windows(\r\n)，Mac(\r)，Unix(\n)的换行问题
            // 读入输入文件
            FileReader inputFilePath = new FileReader(testcaseDir + File.separator + ps.getInput(), CharsetUtil.UTF_8);
            String input = inputFilePath.readString()
                    .replace("\r\n", "\n") // 避免window系统的换行问题
                    .replace("\r", "\n"); // 避免mac系统的换行问题
            FileWriter inputFileWriter = new FileWriter(testcaseDir + File.separator + ps.getInput(), CharsetUtil.UTF_8);
            inputFileWriter.write(input);

            // 读入输出文件
            FileReader outputFilePath = new FileReader(testcaseDir + File.separator + ps.getOutput(), CharsetUtil.UTF_8);
            String output = outputFilePath.readString()
                    .replace("\r\n", "\n") // 避免window系统的换行问题
                    .replace("\r", "\n"); // 避免mac系统的换行问题
            FileWriter outputFileWriter = new FileWriter(testcaseDir + File.separator + ps.getOutput(), CharsetUtil.UTF_8);
            outputFileWriter.write(output);

            // 初始化数据
            // 原数据MD5
            jsonObject.set("outputMd5", DigestUtils.md5DigestAsHex(output.getBytes(StandardCharsets.UTF_8)));
            // 原数据大小
            jsonObject.set("outputSize", output.getBytes(StandardCharsets.UTF_8).length);
            // 去掉全部空格的MD5，用来判断pe
            jsonObject.set("allStrippedOutputMd5", DigestUtils.md5DigestAsHex(output.replaceAll("\\s+", "").getBytes(StandardCharsets.UTF_8)));
            // 默认去掉文末空格的MD5
            jsonObject.set("EOFStrippedOutputMd5", DigestUtils.md5DigestAsHex(rtrim(output).getBytes(StandardCharsets.UTF_8)));

            jsonArray.add(jsonObject);
        }
        result.set("testCases", jsonArray);

        // 将JSON对象转为文件保存
        FileWriter infoWriter = new FileWriter(testcaseDir + File.separator + "info", CharsetUtil.UTF_8);
        infoWriter.write(JSONUtil.toJsonStr(result));

        // 删除临时文件夹
        FileUtil.del(uploadFileDir);

        // 删除非测试数据文件，info文件
        fileNames.remove("info");
        if (!CollectionUtils.isEmpty(fileNames)) {
            for (String fn : fileNames) {
                FileUtil.del(testcaseDir + File.separator + fn);
            }
        }
    }


    /**
     * @des： 初始化手动输入上传的测试数据，写成json文件
     *
     * @param caseVersion
     * @param pid
     * @param problemCases
     * @return
     */
    @Async
    public void initHandTestcase(String caseVersion, Integer pid, List<ProblemCase> problemCases) {

        // 测试用例保存的目录，格式为：problem_pid
        String testcaseDir = Constants.File.TESTCASE_BASE_FOLDER.getPath() + File.separator + "problem_" + pid;

        // 将题目信息转换为json文件格式保存
        JSONObject result = new JSONObject();
        result.set("mode", Constants.JudgeMode.DEFAULT.getMode()); // 全部设置为default
        result.set("version", caseVersion);
        result.set("testcaseSize", problemCases.size());

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < problemCases.size(); i ++) {
            ProblemCase ps = problemCases.get(i);
            JSONObject jsonObject = new JSONObject();

            String inputName = (i + 1) + ".in";
            String outputName = (i + 1) + ".out";
            jsonObject.set("inputName", inputName);
            jsonObject.set("outputName", outputName);
            jsonObject.set("caseId", ps.getId());
            jsonObject.set("score", ps.getScore());

            // 生成输入文件
            String input = ps.getInput()
                    .replace("\r\n", "\n") // 避免window系统的换行问题
                    .replace("\r", "\n"); // 避免mac系统的换行问题
            FileWriter inputFileWriter = new FileWriter(testcaseDir + File.separator + inputName, CharsetUtil.UTF_8);
            inputFileWriter.write(input);

            // 生成输出文件
            String output = ps.getOutput()
                    .replace("\r\n", "\n") // 避免window系统的换行问题
                    .replace("\r", "\n"); // 避免mac系统的换行问题
            FileWriter outputFileWriter = new FileWriter(testcaseDir + File.separator + outputName, CharsetUtil.UTF_8);
            outputFileWriter.write(output);

            // 初始化数据
            // 原数据MD5
            jsonObject.set("outputMd5", DigestUtils.md5DigestAsHex(output.getBytes(StandardCharsets.UTF_8)));
            // 原数据大小
            jsonObject.set("outputSize", output.getBytes(StandardCharsets.UTF_8).length);
            // 去掉全部空格的MD5，用来判断pe
            jsonObject.set("allStrippedOutputMd5", DigestUtils.md5DigestAsHex(output.replaceAll("\\s+", "").getBytes(StandardCharsets.UTF_8)));
            // 默认去掉文末空格的MD5
            jsonObject.set("EOFStrippedOutputMd5", DigestUtils.md5DigestAsHex(rtrim(output).getBytes(StandardCharsets.UTF_8)));

            ps.setInput(inputName);
            ps.setOutput(outputName);

            jsonArray.add(jsonObject);
        }
        result.set("testCases", jsonArray);


        // 将JSON对象转为文件保存
        FileWriter infoWriter = new FileWriter(testcaseDir + File.separator + "info", CharsetUtil.UTF_8);
        infoWriter.write(JSONUtil.toJsonStr(result));

    }


    /**
     * @des： 获取上传的评测文件的名称
     *
     * @param uploadFileDir 临时存放 压缩包及解压之后文件 的文件夹
     * @param zipFileDir    解压之后的文件夹
     * @param pid           题目ID
     * @return
     */
    public List<ProblemCase> getProblemCases(String uploadFileDir, String zipFileDir, Integer pid) {
        // 获取解压目录下的测试用例文件信息
        File[] files = new File(zipFileDir).listFiles();
        if (files == null || files.length == 0) {
            throw new StatusFailException("上传的评测数据为空，请重新上传！");
        }

        Map<String, String> inputMap = new HashMap<>();
        Map<String, String> outputMap = new HashMap<>();

        // 遍历files
        for (File f : files) {
            String tmpName = f.getName(); // 文件名
            if (tmpName.endsWith("in")) {
                String preName = tmpName.substring(0, tmpName.lastIndexOf(".in"));
                inputMap.put(preName, tmpName);
            } else if (tmpName.endsWith("input")) {
                String preName = tmpName.substring(0, tmpName.lastIndexOf(".input"));
                inputMap.put(preName, tmpName);
            } else if (tmpName.endsWith("out")) {
                String preName = tmpName.substring(0, tmpName.lastIndexOf(".out"));
                outputMap.put(preName, tmpName);
            } else if (tmpName.endsWith("output")) {
                String preName = tmpName.substring(0, tmpName.lastIndexOf(".output"));
                outputMap.put(preName, tmpName);
            } else {
                FileUtil.del(uploadFileDir); // 删除解压的文件夹
                throw new StatusFailException("压缩包内文件格式不正确，该文件为[" + tmpName + "]");
            }
        }

        // 检查是否一一对应
        List<ProblemCase> problemCases = new ArrayList<>();
        for (String key : inputMap.keySet()) {
            String inputFileName = inputMap.get(key); // 输入文件名
            String outputFileName = outputMap.getOrDefault(key, null); // 输出文件名
            if (outputFileName == null) {
                FileUtil.del(uploadFileDir); // 删除解压的文件夹
                throw new StatusFailException("缺少与[" + inputFileName + "]对应的输出文件");
            }
            ProblemCase ps = new ProblemCase();
            ps.setProblemId(pid);
            ps.setInput(inputFileName);
            ps.setOutput(outputFileName);
            ps.setScore(0); // 全部设置为0
            problemCases.add(ps);
        }
        return problemCases;
    }


    /**
     * @des： 去除每行末尾的空白符
     *
     * @param value
     * @return
     */
    public static String rtrim(String value) {
        if (value == null) return null;
        return EOL_PATTERN.matcher(StrUtil.trimEnd(value)).replaceAll("");
    }
}
