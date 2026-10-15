package cn.iocoder.yudao.module.edu.service.counselor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.edu.controller.admin.counselor.vo.CounselorAssignClassReqVO;
import cn.iocoder.yudao.module.edu.controller.admin.counselor.vo.CounselorPageReqVO;
import cn.iocoder.yudao.module.edu.controller.admin.counselor.vo.CounselorSaveReqVO;
import cn.iocoder.yudao.module.edu.dal.dataobject.counselor.CounselorClassDO;
import cn.iocoder.yudao.module.edu.dal.dataobject.counselor.CounselorDO;
import cn.iocoder.yudao.module.edu.dal.mysql.counselor.CounselorClassMapper;
import cn.iocoder.yudao.module.edu.dal.mysql.counselor.CounselorMapper;
import cn.iocoder.yudao.module.edu.service.clazz.ClassApi;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.user.UserSaveReqVO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.edu.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.edu.enums.LogRecordConstants.*;

/**
 * 导员 Service 实现类
 *
 * @author edu-manage
 */
@Slf4j
@Service
public class CounselorServiceImpl implements CounselorService {

    /**
     * 系统默认部门编号，用于同步创建系统登录账号
     *
     * 可通过 yudao.edu.default-dept-id 配置，默认使用 system_dept 初始化数据中的 100（芋道源码）
     */
    @Value("${yudao.edu.default-dept-id:100}")
    private Long defaultDeptId;

    /**
     * 单个导员所带班级数量上限
     */
    private static final int MAX_CLASS_NUM = 50;

    @Resource
    private CounselorMapper counselorMapper;
    @Resource
    private CounselorClassMapper counselorClassMapper;
    @Resource
    private ClassApi classApi;
    @Resource
    private AdminUserService adminUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = EDU_COUNSELOR_TYPE, subType = EDU_COUNSELOR_CREATE_SUB_TYPE, bizNo = "{{#counselor.id}}",
            success = EDU_COUNSELOR_CREATE_SUCCESS)
    public Long createCounselor(@Valid CounselorSaveReqVO createReqVO) {
        // 1. 校验正确性
        validateCounselorUnique(null, createReqVO.getCode(), createReqVO.getMobile());
        // 2. 依据 PRD 账号规则：导员用户名=姓名，密码默认 qyjyxy+手机号
        String password = StrUtil.blankToDefault(createReqVO.getPassword(), "qyjyxy" + createReqVO.getMobile());
        Long userId = createSystemUser(createReqVO.getDepartmentId(), createReqVO.getName(), password);
        // 3. 保存导员信息
        CounselorDO counselor = BeanUtils.toBean(createReqVO, CounselorDO.class);
        counselor.setUserId(userId);
        counselor.setStatus(CommonStatusEnum.ENABLE.getStatus());
        counselorMapper.insert(counselor);
        // 4. 记录操作日志上下文
        LogRecordContext.putVariable("counselor", counselor);
        return counselor.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = EDU_COUNSELOR_TYPE, subType = EDU_COUNSELOR_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = EDU_COUNSELOR_UPDATE_SUCCESS)
    public void updateCounselor(@Valid CounselorSaveReqVO updateReqVO) {
        // 1. 校验正确性
        CounselorDO counselor = validateCounselorExists(updateReqVO.getId());
        validateCounselorUnique(updateReqVO.getId(), updateReqVO.getCode(), updateReqVO.getMobile());
        // 2. 同步更新系统登录账号昵称（如有）
        if (counselor.getUserId() != null) {
            UserSaveReqVO userSaveReqVO = new UserSaveReqVO();
            userSaveReqVO.setId(counselor.getUserId());
            userSaveReqVO.setUsername(updateReqVO.getName());
            userSaveReqVO.setNickname(updateReqVO.getName());
            adminUserService.updateUser(userSaveReqVO);
        }
        // 3. 更新导员信息
        CounselorDO updateObj = BeanUtils.toBean(updateReqVO, CounselorDO.class);
        counselorMapper.updateById(updateObj);
        // 4. 记录操作日志上下文
        LogRecordContext.putVariable("counselor", counselor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = EDU_COUNSELOR_TYPE, subType = EDU_COUNSELOR_ASSIGN_CLASSES_SUB_TYPE,
            bizNo = "{{#assignReqVO.counselorId}}", success = EDU_COUNSELOR_ASSIGN_CLASSES_SUCCESS)
    public void assignClasses(@Valid CounselorAssignClassReqVO assignReqVO) {
        Long counselorId = assignReqVO.getCounselorId();
        // 1. 校验导员存在
        CounselorDO counselor = validateCounselorExists(counselorId);
        List<Long> rawClassIds = assignReqVO.getClassIds();
        // 2. 去重 + 数量校验
        Set<Long> distinctIds = new LinkedHashSet<>(rawClassIds);
        if (distinctIds.size() > MAX_CLASS_NUM) {
            throw exception(COUNSELOR_CLASS_NUM_LIMIT, MAX_CLASS_NUM);
        }
        // 3. 校验班级存在且启用，过滤出有效集合
        List<Long> validIds = classApi.validateClassesExist(new ArrayList<>(distinctIds));
        Set<Long> invalidIds = new LinkedHashSet<>(distinctIds);
        invalidIds.removeAll(validIds);
        for (Long invalidId : invalidIds) {
            throw exception(CLASS_NOT_EXISTS, invalidId);
        }
        // 4. 校验“一个班级一个主辅导员”：目标班级不得属于其他导员
        List<CounselorClassDO> existed = counselorClassMapper.selectListByClassIds(validIds);
        Map<Long, Long> ownerMap = CollectionUtils.convertMap(existed, CounselorClassDO::getClassId,
                CounselorClassDO::getCounselorId);
        for (Long classId : validIds) {
            Long ownerId = ownerMap.get(classId);
            if (ownerId != null && !ownerId.equals(counselorId)) {
                throw exception(CLASS_ALREADY_ASSIGNED, classId);
            }
        }
        // 5. 全量覆盖：先删该导员旧关系，再批量插入新关系（同一事务内原子完成）
        counselorClassMapper.physicalDeleteByCounselorId(counselorId);
        List<CounselorClassDO> relations = new ArrayList<>(validIds.size());
        for (int i = 0; i < validIds.size(); i++) {
            CounselorClassDO relation = new CounselorClassDO();
            relation.setCounselorId(counselorId);
            relation.setClassId(validIds.get(i));
            relation.setSort(i);
            relations.add(relation);
        }
        if (CollUtil.isNotEmpty(relations)) {
            relations.forEach(counselorClassMapper::insert);
        }
        // 6. 记录操作日志上下文
        LogRecordContext.putVariable("counselor", counselor);
        LogRecordContext.putVariable("classIds", validIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = EDU_COUNSELOR_TYPE, subType = EDU_COUNSELOR_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = EDU_COUNSELOR_DELETE_SUCCESS)
    public void deleteCounselor(Long id) {
        // 1. 校验导员存在
        CounselorDO counselor = validateCounselorExists(id);
        // 2. 删除前校验：存在未移交班级则禁止删除
        List<CounselorClassDO> classes = counselorClassMapper.selectListByCounselorId(id);
        if (!classes.isEmpty()) {
            throw exception(COUNSELOR_HAS_NOT_TRANSFERRED, classes.size());
        }
        // 3. 清理该导员系统登录账号（如有）（冗余保障，正常已无班级应可安全删除）
        if (counselor.getUserId() != null) {
            adminUserService.deleteUser(counselor.getUserId());
        }
        // 4. 删除导员
        counselorMapper.deleteById(id);
        // 5. 记录操作日志上下文
        LogRecordContext.putVariable("counselor", counselor);
    }

    @Override
    public CounselorDO getCounselor(Long id) {
        return counselorMapper.selectById(id);
    }

    @Override
    public PageResult<CounselorDO> getCounselorPage(CounselorPageReqVO pageReqVO) {
        return counselorMapper.selectPage(pageReqVO);
    }

    @Override
    public List<CounselorDO> getCounselorList() {
        return counselorMapper.selectList();
    }

    @Override
    public List<CounselorClassDO> getCounselorClassList(Long counselorId) {
        return counselorClassMapper.selectListByCounselorId(counselorId);
    }

    @Override
    public CounselorDO getPrimaryCounselor(Long classId) {
        CounselorClassDO relation = counselorClassMapper.selectByClassId(classId);
        return relation == null ? null : counselorMapper.selectById(relation.getCounselorId());
    }

    @Override
    public Map<Long, CounselorDO> getPrimaryCounselorMap(java.util.Collection<Long> classIds) {
        Map<Long, CounselorDO> result = new HashMap<>();
        if (CollUtil.isEmpty(classIds)) {
            return result;
        }
        // 按 class_id 查关系，再聚合成 classId -> counselor 映射
        counselorClassMapper.selectList(CounselorClassDO::getClassId, classIds)
                .forEach(relation -> {
                    CounselorDO counselor = counselorMapper.selectById(relation.getCounselorId());
                    if (counselor != null) {
                        result.put(relation.getClassId(), counselor);
                    }
                });
        return result;
    }

    // ==================== 私有方法 ====================

    /**
     * 创建导员对应的系统登录账号
     *
     * @param departmentId 所属院系（为空则用默认部门）
     * @param name         姓名（PRD：用户名=姓名）
     * @param password     密码
     * @return 系统用户编号
     */
    private Long createSystemUser(Long departmentId, String name, String password) {
        UserSaveReqVO userSaveReqVO = new UserSaveReqVO();
        userSaveReqVO.setUsername(name);
        userSaveReqVO.setPassword(password);
        userSaveReqVO.setNickname(name);
        userSaveReqVO.setDeptId(departmentId != null ? departmentId : defaultDeptId);
        return adminUserService.createUser(userSaveReqVO);
    }

    /**
     * 校验工号、手机号唯一
     */
    private void validateCounselorUnique(Long id, String code, String mobile) {
        CounselorDO byCode = counselorMapper.selectByCode(code);
        if (byCode != null && !byCode.getId().equals(id)) {
            throw exception(COUNSELOR_CODE_EXISTS, code);
        }
        CounselorDO byMobile = counselorMapper.selectByMobile(mobile);
        if (byMobile != null && !byMobile.getId().equals(id)) {
            throw exception(COUNSELOR_MOBILE_EXISTS, mobile);
        }
    }

    /**
     * 校验导员存在
     */
    private CounselorDO validateCounselorExists(Long id) {
        CounselorDO counselor = counselorMapper.selectById(id);
        if (counselor == null) {
            throw exception(COUNSELOR_NOT_EXISTS);
        }
        return counselor;
    }

}