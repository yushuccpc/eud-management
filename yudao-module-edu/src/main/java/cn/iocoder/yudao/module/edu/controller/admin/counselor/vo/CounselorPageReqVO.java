package cn.iocoder.yudao.module.edu.controller.admin.counselor.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 导员分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CounselorPageReqVO extends PageParam {

    @Schema(description = "姓名", example = "王老师")
    private String name;

    @Schema(description = "工号", example = "CD2026001")
    private String code;

    @Schema(description = "联系方式", example = "13800138000")
    private String mobile;

    @Schema(description = "所属院系编号（关联 system_dept.id）", example = "100")
    private Long departmentId;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举", example = "0")
    private Integer status;

    @Schema(description = "班级编号（按所带班级过滤）", example = "1")
    private Long classId;

}