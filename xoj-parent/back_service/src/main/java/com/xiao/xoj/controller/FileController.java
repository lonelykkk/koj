package com.xiao.xoj.controller;

import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.dto.ProblemCaseDTO;
import com.xiao.xoj.pojo.vo.ProblemCaseVO;
import com.xiao.xoj.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 肖恩
 * @since 2023-03-19
 * @description 文件管理
 */
@Api("文件管理")
@RestController
@RequestMapping("/xoj/file")
public class FileController {

    @Autowired
    private FileService fileService;


    @ApiOperation(value = "上传测试用例zip文件, 返回临时存储的 文件夹地址 和 压缩包解压后的文件夹地址")
    @PostMapping("upload-testcase-zip")
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<Map<String, String>> uploadTestcaseZip(@RequestParam("file") MultipartFile file) {
        Map<String, String> resMap = fileService.uploadTestcaseZip(file);
        return ResultData.success(resMap, "评测数据添加成功");
    }


    // todo 修改题目时，分为两部分：题目基本信息 和 测试用例信息，修改时需要分开修改
    // todo 修改测试用例时会跳到一个新的页面，该页面有所有的测试用例
    @ApiOperation(value = "获取测试用例")
    @GetMapping("get-testcase-list")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<List<ProblemCaseVO>> getTestcaseList(@RequestParam(value = "pid") Integer pid,
                                                     @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                     @RequestParam(value = "limit", required = false) Integer limit) {

        List<ProblemCaseVO> problemCaseVOs = fileService.getTestcaseList(pid, currentPage, limit);
        return ResultData.success(problemCaseVOs);
    }


    @ApiOperation(value = "删除测试用例")
    @DeleteMapping("delete-testcase")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<Void> deleteTestcase(@RequestBody List<Integer> pcIds) {
        boolean isOk = fileService.deleteTestcase(pcIds);
        if (!isOk) {
            return ResultData.fail("删除失败！请重试！");
        }
        return ResultData.success("删除成功！");
    }


    @ApiOperation(value = "修改测试用例（只有手动上传的测试用例可以修改）")
    @GetMapping("update-testcase")
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<Void> updateTestcase(@RequestBody ProblemCaseDTO problemCaseDTO) {
        boolean isOk = fileService.updateTestCase(problemCaseDTO);
        if (!isOk) {
            return ResultData.fail("修改失败!");
        }
        return ResultData.success("删除成功!");
    }


    /**
     * @des: 图片上传
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "图片上传")
    @PostMapping("uploadImg")
    public ResultData<String> uploadImg(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadImg(file);
        return ResultData.success(url, null);
    }


    // todo 导入题目，zip文件

    // todo 导入Excel表格，工作室成员信息

}

