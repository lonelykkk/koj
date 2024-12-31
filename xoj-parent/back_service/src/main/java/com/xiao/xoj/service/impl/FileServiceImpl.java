package com.xiao.xoj.service.impl;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.common.exception.StatusSystemErrorException;
import com.xiao.xoj.config.OssConfig;
import com.xiao.xoj.mapper.FileMapper;
import com.xiao.xoj.pojo.dto.ProblemCaseDTO;
import com.xiao.xoj.pojo.entity.file.File;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.pojo.entity.problem.ProblemCase;
import com.xiao.xoj.pojo.vo.ProblemCaseVO;
import com.xiao.xoj.service.FileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.xoj.service.ProblemCaseService;
import com.xiao.xoj.service.ProblemService;
import com.xiao.xoj.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-03-19
 */
@Slf4j(topic = "xoj.file")
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Autowired
    private ProblemCaseService problemCaseService;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private OssConfig ossConfig;

    @Override
    public Map<String, String> uploadTestcaseZip(MultipartFile file) {

        // 检查文件后缀是否为 zip
        String originalName = file.getOriginalFilename();
        String suffix = originalName.substring(originalName.lastIndexOf(".") + 1);
        if (!"zip".toUpperCase().equals(suffix.toUpperCase())) {
            throw new StatusFailException("请上传zip格式的测试数据压缩包");
        }

        // 创建唯一的文件夹来保存zip文件
        String fileDirId = IdUtil.simpleUUID(); // 生成唯一的uuid
        String fileDir = Constants.File.TESTCASE_TMP_FOLDER.getPath() + java.io.File.separator + fileDirId;
        String filePath = fileDir + java.io.File.separator + originalName; // 文件保存在这里，临时保存

        // 如果不存在文件夹，就创建
        FileUtil.mkdir(fileDir);
        try {
            // 将内容中的文件保存到磁盘中
            file.transferTo(new java.io.File(filePath));
        } catch (IOException e) {
            log.error("评测数据文件上传异常,文件目录：{}", fileDir);
            throw new StatusSystemErrorException("服务器异常：评测数据上传失败！");
        }

        // 解压zip文件到指定文件夹，并添加自定义编码
        ZipUtil.unzip(filePath, fileDir, CharsetUtil.CHARSET_GBK);
        // 删除zip文件
        FileUtil.del(filePath);

        // 检查文件夹是否存在
        String newFileDir = fileDir + java.io.File.separator + originalName.substring(0, originalName.lastIndexOf(".")); // 解压后的文件夹（没有了zip）
        java.io.File testcaseFile = new java.io.File(newFileDir);
        java.io.File[] files = testcaseFile.listFiles();

        if (files == null || files.length <= 0) {
            FileUtil.del(fileDir); // 删除解压的文件夹
            throw new StatusFailException("压缩包内不能为空");
        }

        // 检查文件夹内的文件是否符合要求
        HashMap<String, String> inMap = new HashMap<>();
        HashMap<String, String> outMap = new HashMap<>();
        // 遍历，检查in(input)与out(output)是否一一对应，如果不对应，将文件全部删除
        // 将文件名分别放入对应的map中
        for (java.io.File f : files) {
            String tmpName = f.getName(); // 文件名
            if (tmpName.endsWith("in")) {
                String preName = tmpName.substring(0, tmpName.lastIndexOf(".in"));
                inMap.put(preName, tmpName);
            } else if (tmpName.endsWith("input")) {
                String preName = tmpName.substring(0, tmpName.lastIndexOf(".input"));
                inMap.put(preName, tmpName);
            } else if (tmpName.endsWith("out")) {
                String preName = tmpName.substring(0, tmpName.lastIndexOf(".out"));
                outMap.put(preName, tmpName);
            } else if (tmpName.endsWith("output")) {
                String preName = tmpName.substring(0, tmpName.lastIndexOf(".output"));
                outMap.put(preName, tmpName);
            } else {
                FileUtil.del(fileDir); // 删除解压的文件夹
                throw new StatusFailException("压缩包内文件格式不正确，该文件为[" + tmpName + "]");
            }

        }
        // 检查是否一一对应
        for (String key : inMap.keySet()) {
            String inputFileName = inMap.get(key); // 输入文件名
            String outputFileName = outMap.getOrDefault(key, null); // 输出文件名
            if (outputFileName == null) {
                FileUtil.del(fileDir); // 删除解压的文件夹
                throw new StatusFailException("缺少与[" + inputFileName + "]对应的输出文件");
            }
        }

        Map<String, String> map = new HashMap<>();
        map.put("zipFileDir",newFileDir); // 压缩包文件夹
        map.put("fileDir",fileDir); // 系统生成的文件夹，存放压缩包的文件夹
        return map;
    }


    @Override
    public String uploadImg(MultipartFile file) {
        //获取阿里云存储相关常量
        String endPoint = OssConfig.END_POINT;
        String accessKeyId = OssConfig.ACCESS_KEY_ID;
        String accessKeySecret = OssConfig.ACCESS_KEY_SECRET;
        String bucketName = OssConfig.BUCKET_NAME;

        String uploadUrl = null;
        try {
            //判断oss实例是否存在：如果不存在则创建，如果存在则获取
            OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
            if (!ossClient.doesBucketExist(bucketName)) {
                //创建bucket
                ossClient.createBucket(bucketName);
                //设置oss实例的访问权限：公共读
                ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
            }
            //获取上传文件流
            InputStream inputStream = file.getInputStream();

            //构建日期路径：avatar/2019/02/26/文件名
            String filePath = new DateTime().toString("yyyy/MM/dd");
            //文件名：uuid.扩展名
            String original = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString();
            String fileType = original.substring(original.lastIndexOf("."));
            String newName = fileName + fileType;
            String fileUrl =  "img/" + filePath + "/" + newName;
            // 添加ContentType
            String contentType = getContentType(fileType);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(contentType);
            //文件上传至阿里云
            ossClient.putObject(bucketName, fileUrl, inputStream, objectMetadata);
            // 关闭OSSClient。
            ossClient.shutdown();
            //获取url地址
            uploadUrl = "http://" + bucketName + "." + endPoint + "/" + fileUrl;
        } catch (IOException e) {
            throw new StatusFailException("图片上传失败");
        }
        return uploadUrl;
    }


    @Override
    public List<ProblemCaseVO> getTestcaseList(Integer pid, Integer currentPage, Integer limit) {
        // 获取题目信息
        Problem problem = problemService.getById(pid);
        if (problem == null) {
            throw new StatusFailException("题目不存在！");
        }

        // 查询文件信息
        QueryWrapper<ProblemCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("problem_id", pid);
        List<ProblemCase> problemCases = problemCaseService.list(queryWrapper);

        List<ProblemCaseVO> problemCaseVOS = new ArrayList<>();
        if (!problem.getIsUploadCase()) { // 测试用例是上传zip

            // 对查询出来的数据进行封装
            for (ProblemCase pc : problemCases) {
                ProblemCaseVO problemCaseVO = new ProblemCaseVO();
                problemCaseVO.setProblemCase(pc);
                problemCaseVOS.add(problemCaseVO);
            }
        } else { // 测试用例是手动上传的

            // 查询文件信息
            problemCaseVOS = this.getUploadFileInfo(problemCases, pid);
        }

        return problemCaseVOS;
    }



    private List<ProblemCaseVO> getUploadFileInfo(List<ProblemCase> problemCases, Integer pid) {
        List<ProblemCaseVO> fileInfos = new ArrayList<>();

        // 测试用例保存的文件夹
        String filePath = Constants.File.TESTCASE_BASE_FOLDER.getPath() + java.io.File.separator + "problem_" + pid;

        java.io.File file = new java.io.File(filePath);
        if (file.exists() && file.isDirectory()) {
            java.io.File[] files = file.listFiles();

            // 遍历该目录下的文件，除info文件
            HashMap<String, String> fileInfo = new HashMap<>();
            for (java.io.File f : files) {
                String fileName = f.getName();
                if (fileName.endsWith("in") || fileName.endsWith("input")) {
                    // 读取测试用例的输入内容
                    FileReader fileReader = new FileReader(f.getAbsolutePath(), CharsetUtil.UTF_8);
                    fileInfo.put(fileName, fileReader.readString());
                } else if (fileName.endsWith(".out") || fileName.endsWith(".output")){
                    // 读取测试用例的输出内容
                    FileReader fileReader = new FileReader(f.getAbsolutePath(), CharsetUtil.UTF_8);
                    fileInfo.put(fileName, fileReader.readString());
                } else {
                    // 读取到info文件
                    continue;
                }
            }

            // 封装数据
            for (ProblemCase pc : problemCases) {
                ProblemCaseVO problemCaseVO = new ProblemCaseVO();
                problemCaseVO.setProblemCase(pc);
                problemCaseVO.setInput(fileInfo.get(pc.getInput()));
                problemCaseVO.setOutput(fileInfo.get(pc.getOutput()));
                fileInfos.add(problemCaseVO);
            }
        } else {
            throw new StatusFailException("获取失败！测试用例不存在！");
        }


        return fileInfos;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTestcase(List<Integer> pcIds) {
        if (pcIds.size() <= 0) {
            throw new StatusFailException("参数不能为空！");
        }

        // 获取需要删除的测试用例文件信息
        List<ProblemCase> problemCases = problemCaseService.listByIds(pcIds);

        // 获取题目信息
        Problem problem = problemService.getById(problemCases.get(0).getProblemId());

        // 删除表中数据
        boolean isOk = problemCaseService.removeByIds(pcIds);

        // 获取info文件信息，转换为JSON对象
        String testcaseDir = Constants.File.TESTCASE_BASE_FOLDER.getPath() + java.io.File.separator + "problem_" + problem.getId();
        FileReader fileReader = new FileReader(testcaseDir + java.io.File.separator + "info");
        JSONObject infoObject = new JSONObject(fileReader.readString());

        // 修改info文件删除对应的测试用文件
        JSONArray testCases = (JSONArray) infoObject.get("testCases");
        infoObject.set("testcaseSize", (Integer)infoObject.get("testcaseSize") - problemCases.size());
        HashMap<Integer, Integer> testCaseMap = new HashMap<>();
        for (int i = 0; i < testCases.size(); i ++) {
            JSONObject jsonObject = (JSONObject) testCases.get(i);
            testCaseMap.put((Integer) jsonObject.get("caseId"), i);
        }

        for (ProblemCase pc : problemCases) {
            if (testCaseMap.containsKey(pc.getId())) {
                JSONObject jsonObject = (JSONObject) testCases.get(testCaseMap.get(pc.getId()));
                // 删除输入文件
                FileUtil.del(testcaseDir + java.io.File.separator + jsonObject.get("inputName"));
                // 删除输出文件
                FileUtil.del(testcaseDir + java.io.File.separator + jsonObject.get("outputName"));
                testCases.remove(testCaseMap.get(pc.getId()));
            }
        }

        // 将JSON对象转为文件保存
        infoObject.set("testCases", testCases);
        FileWriter infoWriter = new FileWriter(testcaseDir + java.io.File.separator + "info", CharsetUtil.UTF_8);
        infoWriter.write(JSONUtil.toJsonStr(infoObject));

        return isOk;
    }


    @Override
    public boolean updateTestCase(ProblemCaseDTO problemCaseDTO) {
        return false;
    }


    public static String getContentType(String FilenameExtension) {
        if (".bmp".equalsIgnoreCase(FilenameExtension)) {
            return "image/bmp";
        }
        if (".gif".equalsIgnoreCase(FilenameExtension)) {
            return "image/gif";
        }
        if (".jpeg".equalsIgnoreCase(FilenameExtension)
                || ".jpg".equalsIgnoreCase(FilenameExtension)
                || ".png".equalsIgnoreCase(FilenameExtension)) {
            return "image/jpg";
        }
        if (".html".equalsIgnoreCase(FilenameExtension)) {
            return "text/html";
        }

        if (".mp3".equalsIgnoreCase(FilenameExtension)) {
            return "audio/mp3";
        }
        if (".mp4".equalsIgnoreCase(FilenameExtension)) {
            return "video/mp4";
        }
        if (".pdf".equalsIgnoreCase(FilenameExtension)) {
            return "application/pdf";
        }
        return "application/octet-stream";
    }
}
