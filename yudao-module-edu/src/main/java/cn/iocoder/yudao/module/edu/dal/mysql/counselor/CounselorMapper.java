package cn.iocoder.yudao.module.edu.dal.mysql.counselor;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.edu.controller.admin.counselor.vo.CounselorPageReqVO;
import cn.iocoder.yudao.module.edu.dal.dataobject.counselor.CounselorDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 导员 Mapper
 *
 * @author edu-manage
 */
@Mapper
public interface CounselorMapper extends BaseMapperX<CounselorDO> {

    /**
     * 按工号查询导员
     *
     * @param code 工号
     * @return 导员信息
     */
    default CounselorDO selectByCode(String code) {
        return selectOne(CounselorDO::getCode, code);
    }

    /**
     * 按手机号查询导员
     *
     * @param mobile 手机号
     * @return 导员信息
     */
    default CounselorDO selectByMobile(String mobile) {
        return selectOne(CounselorDO::getMobile, mobile);
    }

    /**
     * 分页查询导员列表
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<CounselorDO> selectPage(CounselorPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CounselorDO>()
                .likeIfPresent(CounselorDO::getName, reqVO.getName())
                .likeIfPresent(CounselorDO::getCode, reqVO.getCode())
                .likeIfPresent(CounselorDO::getMobile, reqVO.getMobile())
                .eqIfPresent(CounselorDO::getDepartmentId, reqVO.getDepartmentId())
                .eqIfPresent(CounselorDO::getStatus, reqVO.getStatus())
                .orderByDesc(CounselorDO::getId));
    }

}