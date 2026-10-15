package cn.iocoder.yudao.module.edu.controller.admin.counselor.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 导员班级关系分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CounselorClassPageReqVO extends PageParam {

    @Schema(description = "导员编号", example = "1024")
    private Long counselorId;

}