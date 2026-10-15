package cn.iocoder.yudao.module.edu.dal.dataobject.clazz;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 班级 DO（共享参考数据）
 *
 * 注意：依据《模块协作规范》，班级/年级数据的正式管理归属「年级管理」模块。
 * 导员模块仅为满足“分配所带班级”对 ClassApi 的存在性校验，才在共享库中引用该表，
 * 不承担班级增删改的维护职责；年级管理模块落地后，应由其公开接口替换本表校验。
 *
 * @author edu-manage
 */
@TableName("edu_class")
@KeySequence("edu_class_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class EduClassDO extends BaseDO {

    /**
     * 班级编号
     */
    private Long id;
    /**
     * 班级名称
     */
    private String name;
    /**
     * 所属年级编号（关联 edu_grade.id）
     */
    private Long gradeId;
    /**
     * 所属专业编号
     */
    private Long majorId;
    /**
     * 所属院系编号（关联 system_dept.id）
     */
    private Long departmentId;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

}