package cn.iocoder.yudao.module.edu.controller.admin.counselor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "管理后台 - 导员分配所带班级 Request VO")
@Data
public class CounselorAssignClassReqVO {

    @Schema(description = "导员编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "导员编号不能为空")
    private Long counselorId;

    @Schema(description = "所带班级编号集合（全量覆盖；每个班级仅能被一个导员作为主辅导员持有）",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    @NotEmpty(message = "所带班级不能为空")
    private List<Long> classIds;

}