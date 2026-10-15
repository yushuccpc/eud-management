package cn.iocoder.yudao.module.edu.controller.admin.counselor;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.edu.controller.admin.counselor.vo.CounselorAssignClassReqVO;
import cn.iocoder.yudao.module.edu.controller.admin.counselor.vo.CounselorPageReqVO;
import cn.iocoder.yudao.module.edu.controller.admin.counselor.vo.CounselorRespVO;
import cn.iocoder.yudao.module.edu.controller.admin.counselor.vo.CounselorSaveReqVO;
import cn.iocoder.yudao.module.edu.dal.dataobject.counselor.CounselorDO;
import cn.iocoder.yudao.module.edu.service.counselor.CounselorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 导员管理
 *
 * @author edu-manage
 */
@Tag(name = "管理后台 - 导员管理")
@RestController
@RequestMapping("/edu/counselor")
@Validated
public class CounselorController {

    @Resource
    private CounselorService counselorService;

    @PostMapping("/create")
    @Operation(summary = "创建导员",
            description = "同步创建系统登录账号（用户名=姓名，默认密码 qyjyxy+手机号），账号创建成功后才完成导员信息保存")
    @PreAuthorize("@ss.hasPermission('edu:counselor:create')")
    public CommonResult<Long> createCounselor(@Valid @RequestBody CounselorSaveReqVO createReqVO) {
        return success(counselorService.createCounselor(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改导员", description = "修改导员基础信息，同步更新系统登录账号昵称")
    @PreAuthorize("@ss.hasPermission('edu:counselor:update')")
    public CommonResult<Boolean> updateCounselor(@Valid @RequestBody CounselorSaveReqVO updateReqVO) {
        counselorService.updateCounselor(updateReqVO);
        return success(true);
    }

    @PutMapping("/assign-classes")
    @Operation(summary = "分配所带班级",
            description = "全量覆盖导员所带班级；保证一个班级只有一个主辅导员")
    @PreAuthorize("@ss.hasPermission('edu:counselor:update')")
    public CommonResult<Boolean> assignClasses(@Valid @RequestBody CounselorAssignClassReqVO assignReqVO) {
        counselorService.assignClasses(assignReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除导员", description = "删除前校验是否存在未移交班级，存在则禁止删除")
    @Parameter(name = "id", description = "导员编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('edu:counselor:delete')")
    public CommonResult<Boolean> deleteCounselor(@RequestParam("id") Long id) {
        counselorService.deleteCounselor(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得导员", description = "返回导员详情及其所带班级编号集合")
    @Parameter(name = "id", description = "导员编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('edu:counselor:query')")
    public CommonResult<CounselorRespVO> getCounselor(@RequestParam("id") Long id) {
        CounselorDO counselor = counselorService.getCounselor(id);
        CounselorRespVO respVO = BeanUtils.toBean(counselor, CounselorRespVO.class);
        if (counselor != null) {
            respVO.setClassIds(CollectionUtils.convertList(
                    counselorService.getCounselorClassList(counselor.getId()), relation -> relation.getClassId()));
        }
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得导员分页", description = "支持分页、排序、按姓名/工号/手机号/院系/所带班级过滤")
    @PreAuthorize("@ss.hasPermission('edu:counselor:query')")
    public CommonResult<PageResult<CounselorRespVO>> getCounselorPage(@Validated CounselorPageReqVO pageReqVO) {
        PageResult<CounselorDO> pageResult = counselorService.getCounselorPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CounselorRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得导员列表（用于下拉选择）")
    @PreAuthorize("@ss.hasPermission('edu:counselor:query')")
    public CommonResult<List<CounselorRespVO>> getCounselorList() {
        return success(BeanUtils.toBean(counselorService.getCounselorList(), CounselorRespVO.class));
    }

    // ========== 协作规范契约（供其他模块 / 前端） ==========

    @GetMapping("/get-primary-by-class")
    @Operation(summary = "获得班级主辅导员", description = "协作规范 CounselorApi.getPrimaryCounselor 契约")
    @Parameter(name = "classId", description = "班级编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('edu:counselor:query')")
    public CommonResult<CounselorRespVO> getPrimaryCounselor(@RequestParam("classId") Long classId) {
        CounselorDO counselor = counselorService.getPrimaryCounselor(classId);
        return success(BeanUtils.toBean(counselor, CounselorRespVO.class));
    }

    @GetMapping("/list-classes")
    @Operation(summary = "获得导员所带班级编号集合")
    @Parameter(name = "counselorId", description = "导员编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('edu:counselor:query')")
    public CommonResult<List<Long>> getCounselorClassIds(@RequestParam("counselorId") Long counselorId) {
        return success(CollectionUtils.convertList(
                counselorService.getCounselorClassList(counselorId), relation -> relation.getClassId()));
    }

}