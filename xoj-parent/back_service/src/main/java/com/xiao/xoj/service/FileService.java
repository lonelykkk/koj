package com.xiao.xoj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.xoj.pojo.dto.ProblemCaseDTO;
import com.xiao.xoj.pojo.entity.file.File;
import com.xiao.xoj.pojo.vo.ProblemCaseVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-03-19
 */
public interface FileService extends IService<File> {

    Map<String, String> uploadTestcaseZip(MultipartFile file);

    String uploadImg(MultipartFile file);

    List<ProblemCaseVO> getTestcaseList(Integer pid, Integer currentPage, Integer limit);

    boolean deleteTestcase(List<Integer> pcIds);

    boolean updateTestCase(ProblemCaseDTO problemCaseDTO);
}
