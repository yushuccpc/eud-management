package cn.iocoder.yudao.module.edu.dal.mysql.counselor;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.edu.controller.admin.counselor.vo.CounselorClassPageReqVO;
import cn.iocoder.yudao.module.edu.dal.dataobject.counselor.CounselorClassDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 导员-班级关系 Mapper
 *
 * @author edu-manage
 */
@Mapper
public interface CounselorClassMapper extends BaseMapperX<CounselorClassDO> {

    /**
     * 按导员编号查询其所有所带班级关系
     *
     * @param counselorId 导员编号
     * @return 关系列表
     */
    default List<CounselorClassDO> selectListByCounselorId(Long counselorId) {
        return selectList(CounselorClassDO::getCounselorId, counselorId);
    }

    /**
     * 按班级编号查询关系（校验“一个班级一个主辅导员”）
     *
     * @param classId 班级编号
     * @return 关系，无则返回 null
     */
    default CounselorClassDO selectByClassId(Long classId) {
        return selectOne(CounselorClassDO::getClassId, classId);
    }

    /**
     * 按导员编号删除所有关系（分配班级时全量覆盖）
     * <p>
     * 使用物理删除：关系表存在 uk_class_id 唯一键，若用逻辑删除（deleted 位），
     * 软删除行仍占用唯一键，导员二次分配同一班级时会触发 DuplicateKeyException。
     *
     * @param counselorId 导员编号
     */
    @Delete("DELETE FROM edu_counselor_class WHERE counselor_id = #{counselorId}")
    int physicalDeleteByCounselorId(@Param("counselorId") Long counselorId);

    /**
     * 按班级编号集合查询关系，用于删除导员前的“未移交班级”统计
     *
     * @param classIds 班级编号集合
     * @return 关系列表
     */
    default List<CounselorClassDO> selectListByClassIds(Collection<Long> classIds) {
        return selectList(CounselorClassDO::getClassId, classIds);
    }

    /**
     * 分页查询班级成员（主辅导员视角，可选）——预留扩展
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<CounselorClassDO> selectPage(CounselorClassPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CounselorClassDO>()
                .eqIfPresent(CounselorClassDO::getCounselorId, reqVO.getCounselorId())
                .orderByAsc(CounselorClassDO::getSort));
    }

}