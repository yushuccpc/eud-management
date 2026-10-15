package cn.iocoder.yudao.module.edu.dal.mysql.clazz;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.edu.dal.dataobject.clazz.EduClassDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 班级 Mapper（共享参考数据）
 *
 * 仅用于导员模块对“班级存在且生效”的校验，班级增删改不在此维护。
 *
 * @author edu-manage
 */
@Mapper
public interface EduClassMapper extends BaseMapperX<EduClassDO> {

    /**
     * 查询启用状态的班级
     *
     * @param classIds 班级编号集合
     * @return 启用的班级列表
     */
    default List<EduClassDO> selectListEnableByIds(Collection<Long> classIds) {
        return selectList(new LambdaQueryWrapperX<EduClassDO>()
                .in(EduClassDO::getId, classIds)
                .eq(EduClassDO::getStatus, CommonStatusEnum.ENABLE.getStatus()));
    }

}