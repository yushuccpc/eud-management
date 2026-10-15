package cn.iocoder.yudao.module.edu.service.counselor;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.edu.controller.admin.counselor.vo.CounselorAssignClassReqVO;
import cn.iocoder.yudao.module.edu.controller.admin.counselor.vo.CounselorPageReqVO;
import cn.iocoder.yudao.module.edu.controller.admin.counselor.vo.CounselorSaveReqVO;
import cn.iocoder.yudao.module.edu.dal.dataobject.counselor.CounselorClassDO;
import cn.iocoder.yudao.module.edu.dal.dataobject.counselor.CounselorDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 导员 Service 接口
 *
 * @author edu-manage
 */
public interface CounselorService {

    /**
     * 创建导员
     *
     * 依据 PRD 账号规则，导员登录账号默认使用“姓名”，初始密码默认 qyjyxy+手机号；
     * 支持同步创建系统登录账号。
     *
     * @param createReqVO 创建信息
     * @return 导员编号
     */
    Long createCounselor(@Valid CounselorSaveReqVO createReqVO);

    /**
     * 更新导员基础信息
     *
     * @param updateReqVO 更新信息
     */
    void updateCounselor(@Valid CounselorSaveReqVO updateReqVO);

    /**
     * 分配所带班级（全量覆盖）
     *
     * 校验导员存在、班级存在且启用、每个班级仅一个主辅导员（class_id 唯一）；
     * 更新过程在单事务内“先删后插”，保证原子性。
     *
     * @param assignReqVO 分配信息
     */
    void assignClasses(@Valid CounselorAssignClassReqVO assignReqVO);

    /**
     * 删除导员
     *
     * 删除前校验是否存在未移交班级；存在则抛 {@code COUNSELOR_HAS_NOT_TRANSFERRED} 禁止删除。
     *
     * @param id 导员编号
     */
    void deleteCounselor(Long id);

    /**
     * 获得导员
     *
     * @param id 导员编号
     * @return 导员信息
     */
    CounselorDO getCounselor(Long id);

    /**
     * 获得导员分页
     *
     * @param pageReqVO 分页查询
     * @return 导员分页
     */
    PageResult<CounselorDO> getCounselorPage(CounselorPageReqVO pageReqVO);

    /**
     * 获得导员列表（全部，用于下拉选择）
     *
     * @return 导员列表
     */
    List<CounselorDO> getCounselorList();

    /**
     * 获得导员所带班级关系列表（协作规范契约支撑）
     *
     * @param counselorId 导员编号
     * @return 班级关系列表
     */
    List<CounselorClassDO> getCounselorClassList(Long counselorId);

    /**
     * 按班级编号获得主辅导员（协作规范 {@code CounselorApi.getPrimaryCounselor} 契约）
     *
     * @param classId 班级编号
     * @return 主辅导员导员信息；无则返回 null
     */
    CounselorDO getPrimaryCounselor(Long classId);

    /**
     * 按班级编号集合批量获得主辅导员（协作规范契约，供课表/考勤等模块引用）
     *
     * @param classIds 班级编号集合
     * @return classId -> 主辅导员 的映射；无则缺失
     */
    java.util.Map<Long, CounselorDO> getPrimaryCounselorMap(java.util.Collection<Long> classIds);

}