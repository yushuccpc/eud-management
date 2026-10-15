package cn.iocoder.yudao.module.edu.service.counselor;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.module.edu.controller.admin.counselor.vo.CounselorAssignClassReqVO;
import cn.iocoder.yudao.module.edu.controller.admin.counselor.vo.CounselorSaveReqVO;
import cn.iocoder.yudao.module.edu.dal.dataobject.counselor.CounselorClassDO;
import cn.iocoder.yudao.module.edu.dal.dataobject.counselor.CounselorDO;
import cn.iocoder.yudao.module.edu.dal.mysql.counselor.CounselorClassMapper;
import cn.iocoder.yudao.module.edu.dal.mysql.counselor.CounselorMapper;
import cn.iocoder.yudao.module.edu.service.clazz.ClassApi;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.user.UserSaveReqVO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.module.edu.enums.ErrorCodeConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 导员 Service 单元测试
 *
 * 覆盖：创建/更新/删除/分配班级的正常流程、边界场景与异常分支。
 *
 * @author edu-manage
 */
@ExtendWith(MockitoExtension.class)
class CounselorServiceImplTest {

    @InjectMocks
    private CounselorServiceImpl counselorService;

    @Mock
    private CounselorMapper counselorMapper;
    @Mock
    private CounselorClassMapper counselorClassMapper;
    @Mock
    private ClassApi classApi;
    @Mock
    private AdminUserService adminUserService;
    @Mock
    private PasswordEncoder passwordEncoder;

    private CounselorDO buildCounselor(Long id, String code, String mobile) {
        CounselorDO doo = new CounselorDO();
        doo.setId(id);
        doo.setCode(code);
        doo.setName("王老师");
        doo.setMobile(mobile);
        doo.setTitle("讲师");
        doo.setDepartmentId(100L);
        doo.setUserId(200L);
        doo.setStatus(CommonStatusEnum.ENABLE.getStatus());
        return doo;
    }

    @BeforeEach
    void setUp() {
        // @Value 字段在单测中不会自动注入，手动赋值默认部门
        ReflectionTestUtils.setField(counselorService, "defaultDeptId", 100L);
    }

    // ==================== create ====================

    @Test
    void createCounselor_success_syncCreateUserAndSave() {
        CounselorSaveReqVO reqVO = new CounselorSaveReqVO();
        reqVO.setName("王老师");
        reqVO.setCode("CD2026001");
        reqVO.setMobile("13800138000");
        reqVO.setPassword(null); // 默认密码 qyjyxy+手机号

        when(counselorMapper.selectByCode("CD2026001")).thenReturn(null);
        when(counselorMapper.selectByMobile("13800138000")).thenReturn(null);
        when(adminUserService.createUser(any(UserSaveReqVO.class))).thenReturn(300L);
        when(counselorMapper.insert(any(CounselorDO.class))).thenAnswer(inv -> {
            CounselorDO arg = inv.getArgument(0);
            arg.setId(1L);
            return 1;
        });

        Long id = counselorService.createCounselor(reqVO);

        assertThat(id).isEqualTo(1L);
        // 校验系统账号创建：用户名=姓名
        ArgumentCaptor<UserSaveReqVO> userCaptor = ArgumentCaptor.forClass(UserSaveReqVO.class);
        verify(adminUserService).createUser(userCaptor.capture());
        assertThat(userCaptor.getValue().getUsername()).isEqualTo("王老师");
        // 校验导员保存时写入 userId 与开启状态
        ArgumentCaptor<CounselorDO> counselorCaptor = ArgumentCaptor.forClass(CounselorDO.class);
        verify(counselorMapper).insert(counselorCaptor.capture());
        assertThat(counselorCaptor.getValue().getUserId()).isEqualTo(300L);
        assertThat(counselorCaptor.getValue().getStatus()).isEqualTo(CommonStatusEnum.ENABLE.getStatus());
    }

    @Test
    void createCounselor_codeExists_throws() {
        CounselorSaveReqVO reqVO = new CounselorSaveReqVO();
        reqVO.setCode("CD2026001");
        reqVO.setMobile("13800138000");
        when(counselorMapper.selectByCode("CD2026001")).thenReturn(buildCounselor(9L, "CD2026001", "13900000000"));

        assertThatThrownBy(() -> counselorService.createCounselor(reqVO))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(COUNSELOR_CODE_EXISTS.getMsg().replace("{}", "CD2026001"));
        verify(counselorMapper, never()).insert(any(CounselorDO.class));
    }

    @Test
    void createCounselor_mobileExists_throws() {
        CounselorSaveReqVO reqVO = new CounselorSaveReqVO();
        reqVO.setCode("CD2026002");
        reqVO.setMobile("13800138000");
        when(counselorMapper.selectByCode("CD2026002")).thenReturn(null);
        when(counselorMapper.selectByMobile("13800138000")).thenReturn(buildCounselor(9L, "X", "13800138000"));

        assertThatThrownBy(() -> counselorService.createCounselor(reqVO))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(COUNSELOR_MOBILE_EXISTS.getMsg().replace("{}", "13800138000"));
    }

    // ==================== update ====================

    @Test
    void updateCounselor_success() {
        CounselorSaveReqVO reqVO = new CounselorSaveReqVO();
        reqVO.setId(1L);
        reqVO.setCode("CD2026001");
        reqVO.setMobile("13800138000");
        reqVO.setName("李老师");
        CounselorDO existed = buildCounselor(1L, "CD2026001", "13800138000");
        existed.setUserId(200L);

        when(counselorMapper.selectById(1L)).thenReturn(existed);
        when(counselorMapper.selectByCode("CD2026001")).thenReturn(existed); // 同一条，算不冲突
        when(counselorMapper.selectByMobile("13800138000")).thenReturn(existed);

        counselorService.updateCounselor(reqVO);

        // 同步更新系统账号昵称
        ArgumentCaptor<UserSaveReqVO> userCaptor = ArgumentCaptor.forClass(UserSaveReqVO.class);
        verify(adminUserService).updateUser(userCaptor.capture());
        assertThat(userCaptor.getValue().getNickname()).isEqualTo("李老师");
        verify(counselorMapper).updateById(any(CounselorDO.class));
    }

    @Test
    void updateCounselor_notExists_throws() {
        CounselorSaveReqVO reqVO = new CounselorSaveReqVO();
        reqVO.setId(999L);
        reqVO.setCode("CD2026001");
        reqVO.setMobile("13800138000");
        when(counselorMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> counselorService.updateCounselor(reqVO))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(COUNSELOR_NOT_EXISTS.getMsg());
    }

    // ==================== assignClasses ====================

    @Test
    void assignClasses_success_overwriteRelations() {
        CounselorAssignClassReqVO reqVO = new CounselorAssignClassReqVO();
        reqVO.setCounselorId(1L);
        reqVO.setClassIds(Arrays.asList(10L, 11L, 11L)); // 含重复，应去重为 10,11

        when(counselorMapper.selectById(1L)).thenReturn(buildCounselor(1L, "CD2026001", "13800138000"));
        // 两个班级均有效
        when(classApi.validateClassesExist(Arrays.asList(10L, 11L))).thenReturn(Arrays.asList(10L, 11L));
        when(counselorClassMapper.selectListByClassIds(Arrays.asList(10L, 11L))).thenReturn(Collections.emptyList());

        counselorService.assignClasses(reqVO);

        verify(counselorClassMapper).physicalDeleteByCounselorId(1L);
        // 插入 2 条关系
        ArgumentCaptor<CounselorClassDO> captor = ArgumentCaptor.forClass(CounselorClassDO.class);
        verify(counselorClassMapper, times(2)).insert(captor.capture());
        assertThat(captor.getAllValues()).extracting(CounselorClassDO::getClassId)
                .containsExactlyInAnyOrder(10L, 11L);
        assertThat(captor.getAllValues()).extracting(CounselorClassDO::getCounselorId)
                .allMatch(cid -> cid.equals(1L));
    }

    @Test
    void assignClasses_classInvalid_throws() {
        CounselorAssignClassReqVO reqVO = new CounselorAssignClassReqVO();
        reqVO.setCounselorId(1L);
        reqVO.setClassIds(Arrays.asList(10L, 99L)); // 99 不存在
        when(counselorMapper.selectById(1L)).thenReturn(buildCounselor(1L, "CD2026001", "13800138000"));
        // 仅 10 有效，99 被过滤 => 应抛 CLASS_NOT_EXISTS
        when(classApi.validateClassesExist(Arrays.asList(10L, 99L))).thenReturn(Arrays.asList(10L));

        assertThatThrownBy(() -> counselorService.assignClasses(reqVO))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(CLASS_NOT_EXISTS.getMsg().replace("{}", "99"));
        verify(counselorClassMapper, never()).physicalDeleteByCounselorId(anyLong());
    }

    @Test
    void assignClasses_classOwnedByOther_throws() {
        CounselorAssignClassReqVO reqVO = new CounselorAssignClassReqVO();
        reqVO.setCounselorId(1L);
        reqVO.setClassIds(Arrays.asList(10L));
        when(counselorMapper.selectById(1L)).thenReturn(buildCounselor(1L, "CD2026001", "13800138000"));
        when(classApi.validateClassesExist(Arrays.asList(10L))).thenReturn(Arrays.asList(10L));
        // 班级 10 已由导员 2 持有
        CounselorClassDO rel = new CounselorClassDO();
        rel.setCounselorId(2L);
        rel.setClassId(10L);
        when(counselorClassMapper.selectListByClassIds(Arrays.asList(10L))).thenReturn(Arrays.asList(rel));

        assertThatThrownBy(() -> counselorService.assignClasses(reqVO))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(CLASS_ALREADY_ASSIGNED.getMsg().replace("{}", "10"));
    }

    // ==================== delete ====================

    @Test
    void deleteCounselor_hasClass_throws() {
        when(counselorMapper.selectById(1L)).thenReturn(buildCounselor(1L, "CD2026001", "13800138000"));
        CounselorClassDO rel = new CounselorClassDO();
        rel.setCounselorId(1L);
        rel.setClassId(10L);
        when(counselorClassMapper.selectListByCounselorId(1L)).thenReturn(Arrays.asList(rel));

        assertThatThrownBy(() -> counselorService.deleteCounselor(1L))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(COUNSELOR_HAS_NOT_TRANSFERRED.getMsg().replace("{}", "1"));
        verify(counselorMapper, never()).deleteById(any());
        verify(adminUserService, never()).deleteUser(anyLong());
    }

    @Test
    void deleteCounselor_noClass_success() {
        when(counselorMapper.selectById(1L)).thenReturn(buildCounselor(1L, "CD2026001", "13800138000"));
        when(counselorClassMapper.selectListByCounselorId(1L)).thenReturn(Collections.emptyList());

        counselorService.deleteCounselor(1L);

        verify(adminUserService).deleteUser(200L);
        verify(counselorMapper).deleteById(1L);
    }

    // ==================== 主辅导员契约 ====================

    @Test
    void getPrimaryCounselor_notAssigned_returnsNull() {
        when(counselorClassMapper.selectByClassId(10L)).thenReturn(null);
        assertThat(counselorService.getPrimaryCounselor(10L)).isNull();
    }

    @Test
    void getPrimaryCounselor_assigned_returnsCounselor() {
        CounselorClassDO rel = new CounselorClassDO();
        rel.setCounselorId(1L);
        rel.setClassId(10L);
        when(counselorClassMapper.selectByClassId(10L)).thenReturn(rel);
        when(counselorMapper.selectById(1L)).thenReturn(buildCounselor(1L, "CD2026001", "13800138000"));

        CounselorDO result = counselorService.getPrimaryCounselor(10L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getPrimaryCounselorMap_emptyInput_returnsEmpty() {
        assertThat(counselorService.getPrimaryCounselorMap(Collections.emptyList())).isEmpty();
    }

    @Test
    void getPrimaryCounselorMap_batch_returnsMapping() {
        when(counselorClassMapper.selectList(any(com.baomidou.mybatisplus.core.toolkit.support.SFunction.class), anyCollection())).thenReturn(new ArrayList<>(Arrays.asList(
                buildRelation(1L, 10L), buildRelation(1L, 11L), buildRelation(2L, 12L))));
        when(counselorMapper.selectById(1L)).thenReturn(buildCounselor(1L, "CD1", "13800000001"));
        when(counselorMapper.selectById(2L)).thenReturn(buildCounselor(2L, "CD2", "13800000002"));

        Map<Long, CounselorDO> map = counselorService.getPrimaryCounselorMap(Arrays.asList(10L, 11L, 12L, 99L));

        assertThat(map).containsKeys(10L, 11L, 12L);
        assertThat(map.get(12L).getId()).isEqualTo(2L);
    }

    private CounselorClassDO buildRelation(Long counselorId, Long classId) {
        CounselorClassDO rel = new CounselorClassDO();
        rel.setCounselorId(counselorId);
        rel.setClassId(classId);
        return rel;
    }

    // 占位，避免未使用告警；可补充 Controller 层（@WebMvcTest）集成测试
    @Test
    void page_success() {
        // 仅验证分页透传，Mapper 为 default 方法，此处验证 Service 不抛异常
        cn.iocoder.yudao.module.edu.controller.admin.counselor.vo.CounselorPageReqVO pageReqVO =
                new cn.iocoder.yudao.module.edu.controller.admin.counselor.vo.CounselorPageReqVO();
        when(counselorMapper.selectPage(pageReqVO)).thenReturn(PageResult.empty());
        assertThat(counselorService.getCounselorPage(pageReqVO)).isNotNull();
    }
}