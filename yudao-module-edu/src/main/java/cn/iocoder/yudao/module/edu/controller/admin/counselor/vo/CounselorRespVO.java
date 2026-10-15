package cn.iocoder.yudao.module.edu.controller.admin.counselor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 导员 Response VO")
@Data
public class CounselorRespVO {

    @Schema(description = "导员编号", example = "1024")
    private Long id;

    @Schema(description = "工号", example = "CD2026001")
    private String code;

    @Schema(description = "姓名", example = "王老师")
    private String name;

    @Schema(description = "联系方式", example = "13800138000")
    private String mobile;

    @Schema(description = "职称", example = "讲师")
    private String title;

    @Schema(description = "所属院系编号（关联 system_dept.id）", example = "100")
    private Long departmentId;

    @Schema(description = "同步创建的系统登录用户编号", example = "1024")
    private Long userId;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举", example = "0")
    private Integer status;

    @Schema(description = "备注", example = "负责2023级学生日常管理")
    private String remark;

    @Schema(description = "所带班级编号集合（查询详情时填充）")
    private List<Long> classIds;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}