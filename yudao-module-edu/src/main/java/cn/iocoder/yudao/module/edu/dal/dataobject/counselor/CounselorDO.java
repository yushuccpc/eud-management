package cn.iocoder.yudao.module.edu.dal.dataobject.counselor;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导员 DO
 *
 * 导员（辅导员）个人档案。user_id 为同步创建的系统登录用户编号（system_users.id），
 * 业务档案 ID（id）与登录用户 ID（user_id）严格区分，二者不可互换。
 *
 * @author edu-manage
 */
@TableName("edu_counselor")
@KeySequence("edu_counselor_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class CounselorDO extends BaseDO {

    /**
     * 导员档案编号
     */
    private Long id;
    /**
     * 工号
     */
    private String code;
    /**
     * 姓名
     */
    private String name;
    /**
     * 联系方式
     */
    private String mobile;
    /**
     * 职称
     */
    private String title;
    /**
     * 所属院系编号（关联 system_dept.id）
     */
    private Long departmentId;
    /**
     * 同步创建的系统登录用户编号（system_users.id）
     */
    private Long userId;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}