package cn.iocoder.yudao.module.edu.enums;

/**
 * Edu（学院管理）操作日志枚举
 * 目的：统一管理，也减少 Service 里各种“复杂”字符串
 *
 * @author edu-manage
 */
public interface LogRecordConstants {

    // ======================= EDU_COUNSELOR 导员 =======================

    String EDU_COUNSELOR_TYPE = "EDU 导员";
    String EDU_COUNSELOR_CREATE_SUB_TYPE = "添加导员";
    String EDU_COUNSELOR_CREATE_SUCCESS = "添加了导员【{{#counselor.name}}】，并同步创建登录账号";
    String EDU_COUNSELOR_UPDATE_SUB_TYPE = "更新导员";
    String EDU_COUNSELOR_UPDATE_SUCCESS = "更新了导员【{{#counselor.name}}】";
    String EDU_COUNSELOR_DELETE_SUB_TYPE = "删除导员";
    String EDU_COUNSELOR_DELETE_SUCCESS = "删除了导员【{{#counselor.name}}】";
    String EDU_COUNSELOR_ASSIGN_CLASSES_SUB_TYPE = "分配所带班级";
    String EDU_COUNSELOR_ASSIGN_CLASSES_SUCCESS = "为导员【{{#counselor.name}}】分配了所带班级，共【{{#classIds?.size()}}】个";

}