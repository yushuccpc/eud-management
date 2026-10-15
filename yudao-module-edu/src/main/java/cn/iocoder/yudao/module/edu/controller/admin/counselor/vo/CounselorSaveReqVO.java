package cn.iocoder.yudao.module.edu.controller.admin.counselor.vo;

import cn.iocoder.yudao.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Schema(description = "管理后台 - 导员创建/修改 Request VO")
@Data
public class CounselorSaveReqVO {

    @Schema(description = "导员编号（修改时必填）", example = "1024")
    private Long id;

    @Schema(description = "工号", requiredMode = Schema.RequiredMode.REQUIRED, example = "CD2026001")
    @NotBlank(message = "工号不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{4,20}$", message = "工号由 4-20 位数字、字母组成")
    private String code;

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "王老师")
    @NotBlank(message = "姓名不能为空")
    @Size(max = 30, message = "姓名长度不能超过 30 个字符")
    private String name;

    @Schema(description = "联系方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotBlank(message = "联系方式不能为空")
    @Mobile(message = "联系方式格式不正确")
    private String mobile;

    @Schema(description = "职称", example = "讲师")
    @Size(max = 30, message = "职称长度不能超过 30 个字符")
    private String title;

    @Schema(description = "所属院系编号（关联 system_dept.id）", example = "100")
    private Long departmentId;

    @Schema(description = "密码（创建时可选；留空则由系统生成初始密码；修改时为空表示不修改）", example = "qyjyxy+13800138000")
    @Size(max = 16, message = "密码长度不能超过 16 个字符")
    private String password;

    @Schema(description = "备注", example = "负责2023级学生日常管理")
    @Size(max = 255, message = "备注长度不能超过 255 个字符")
    private String remark;

}