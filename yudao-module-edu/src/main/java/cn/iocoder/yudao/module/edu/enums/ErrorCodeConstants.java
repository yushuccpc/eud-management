package cn.iocoder.yudao.module.edu.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Edu（学院管理）错误码枚举类
 *
 * edu 学院管理业务模块，使用 1-060-000-000 段
 *
 * @author edu-manage
 */
public interface ErrorCodeConstants {

    // ========== 导员模块 1-060-000-000 ==========
    ErrorCode COUNSELOR_NOT_EXISTS = new ErrorCode(1_060_000_000, "导员不存在");
    ErrorCode COUNSELOR_CODE_EXISTS = new ErrorCode(1_060_000_001, "导员工号【{}】已存在");
    ErrorCode COUNSELOR_MOBILE_EXISTS = new ErrorCode(1_060_000_002, "导员手机号【{}】已存在");
    ErrorCode COUNSELOR_HAS_NOT_TRANSFERRED = new ErrorCode(1_060_000_003, "该导员仍有【{}】个未移交班级，无法删除");
    ErrorCode COUNSELOR_CLASS_ID_BLANK = new ErrorCode(1_060_000_004, "班级分配列表不能为空");
    ErrorCode COUNSELOR_CLASS_NUM_LIMIT = new ErrorCode(1_060_000_005, "所带班级数量不能超过【{}】个");

    // ========== 班级共享参考数据 1-060-001-000 ==========
    ErrorCode CLASS_NOT_EXISTS = new ErrorCode(1_060_001_000, "班级【{}】不存在");
    ErrorCode CLASS_ALREADY_ASSIGNED = new ErrorCode(1_060_001_001, "班级【{}】已有主辅导员，无法重复分配");

}