package com.xiao.xoj;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.pojo.entity.problem.ProblemCase;
import com.xiao.xoj.utils.Constants;
import org.junit.Test;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author 肖恩
 * @create 2023/3/21 20:40
 * @description: TODO
 */
public class FileTest {



    private final static Pattern EOL_PATTERN = Pattern.compile("[^\\S\\n]+(?=\\n)");

    // 去除每行末尾的空白符
    public static String rtrim(String value) {
        if (value == null) return null;
        return EOL_PATTERN.matcher(StrUtil.trimEnd(value)).replaceAll("");
    }

    @Test
    public void testStrUti() {
        String s1 = "";
        String s2 = "123";
        System.out.println(StringUtils.isEmpty(s1)); // true
        System.out.println(StringUtils.isEmpty(s2)); // false
    }


    @Test
    public void initTestcase() {
        System.out.println("zip 开始初始化");

        String caseVersion = "123";
        int pid = 2;
        String uploadFileDir = "D:\\MyCode\\MyProject\\Graduation\\code\\xoj-parent\\back_service\\file\\tmp\\test2";
        String zipFileDir = "D:\\MyCode\\MyProject\\Graduation\\code\\xoj-parent\\back_service\\file\\tmp\\test2\\tt";
        List<ProblemCase> problemCases = new ArrayList<>();
        for (int i = 1; i < 3; i ++) {
            ProblemCase ps = new ProblemCase();
            ps.setScore(0);
            ps.setProblemId(pid);
            ps.setId(i);
            ps.setOutput(i + ".output");
            ps.setInput(i + ".input");
            problemCases.add(ps);
        }


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
        System.out.println("初始化成功");
    }

    @Test
    public void testCopyFileOrDir() {
//        String from = "D:\\MyCode\\MyProject\\Graduation\\code\\xoj-parent\\back_service\\file\\tmp\\test";
//        String to = "D:\\MyCode\\MyProject\\Graduation\\code\\xoj-parent\\back_service\\file\\tmp\\test2";
//        FileUtil.copyFilesFromDir(new File(from), new File(to), true);
//        FileUtil.clean(to);

    }



    @Test
    public void testUserZip() {
//        String s = "0123";
//        System.out.println(s.substring(0,1));
//        System.out.println(s.lastIndexOf("2"));
//        System.out.println(s.endsWith("23"));
//        System.out.println(s.substring(0, s.lastIndexOf("23")));

    }

    @Test
    public void testGetTestCaseFile() {
//        //获取上传的评测文件的名称
//        String zipFileDir = "D:\\MyCode\\MyProject\\Graduation\\code\\xoj-parent\\back_service\\file\\tmp\\0cee7464f216408189bbc1851bf4716e\\新建文件夹";
//        String uploadFileDir = "D:\\MyCode\\MyProject\\Graduation\\code\\xoj-parent\\back_service\\file\\tmp\\0cee7464f216408189bbc1851bf4716e";
//        Integer pid =  1;
//        File[] files = new File(zipFileDir).listFiles();
//        if (files == null || files.length == 0) {
//            throw new StatusFailException("上传的评测数据为空，请重新上传！");
//        }
//        Map<String, String> inputMap = new HashMap<>();
//        Map<String, String> outputMap = new HashMap<>();
//        // 变量files
//        for (java.io.File f : files) {
//            String tmpName = f.getName(); // 文件名
//            if (tmpName.endsWith("in")) {
//                String preName = tmpName.substring(0, tmpName.lastIndexOf(".in"));
//                inputMap.put(preName, tmpName);
//            } else if (tmpName.endsWith("input")) {
//                String preName = tmpName.substring(0, tmpName.lastIndexOf(".input"));
//                inputMap.put(preName, tmpName);
//            } else if (tmpName.endsWith("out")) {
//                String preName = tmpName.substring(0, tmpName.lastIndexOf(".out"));
//                outputMap.put(preName, tmpName);
//            } else if (tmpName.endsWith("output")) {
//                String preName = tmpName.substring(0, tmpName.lastIndexOf(".output"));
//                outputMap.put(preName, tmpName);
//            } else {
//                FileUtil.del(uploadFileDir); // 删除解压的文件夹
//                throw new StatusFailException("压缩包内文件格式不正确，该文件为[" + tmpName + "]");
//            }
//        }
//        List<ProblemCase> problemCases = new ArrayList<>();
//        // 检查是否一一对应
//        for (String key : inputMap.keySet()) {
//            String inputFileName = inputMap.get(key); // 输入文件名
//            String outputFileName = outputMap.getOrDefault(key, null); // 输出文件名
//            if (outputFileName == null) {
//                FileUtil.del(uploadFileDir); // 删除解压的文件夹
//                throw new StatusFailException("缺少与[" + inputFileName + "]对应的输出文件");
//            }
//            ProblemCase ps = new ProblemCase();
//            ps.setProblemId(pid);
//            ps.setInput(inputFileName);
//            ps.setOutput(outputFileName);
//            ps.setScore(0); // 全部设置为0
//            problemCases.add(ps);
//        }
//        for (ProblemCase ps : problemCases) {
//            System.out.println(ps.getProblemId() + " - " + ps.getInput() + " - " + ps.getOutput());
//        }
//        //System.out.println(problemCases);

    }


    @Test
    public void testEasyExcel() {
        
    }
}
