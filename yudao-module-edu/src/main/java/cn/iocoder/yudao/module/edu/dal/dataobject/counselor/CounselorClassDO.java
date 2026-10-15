package cn.iocoder.yudao.module.edu.dal.dataobject.counselor;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导员-班级关系 DO
 *
 * class_id 建立唯一约束，保证“一个班级只有一个主辅导员”。
 * 导员按 counselor_id 聚合其所有关系记录，即为其“所带班级列表”。
 * 该关系是「主辅导员」分配的唯一定义（协作规范第二节：关系只存一份）。
 *
 * @author edu-manage
 */
@TableName("edu_counselor_class")
@KeySequence("edu_counselor_class_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class CounselorClassDO extends BaseDO {

    /**
     * 关系编号
     */
    private Long id;
    /**
     * 导员档案编号（关联 edu_counselor.id）
     */
    private Long counselorId;
    /**
     * 班级编号（关联 edu_class.id），唯一 => 主辅导员
     */
    private Long classId;
    /**
     * 排序
     */
    private Integer sort;

}