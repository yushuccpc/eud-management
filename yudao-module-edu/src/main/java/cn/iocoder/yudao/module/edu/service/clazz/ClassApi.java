package cn.iocoder.yudao.module.edu.service.clazz;

import java.util.List;

/**
 * 班级访问契约（ClassApi）
 *
 * 依据《模块协作规范》第二节：班级/年级数据归属于「年级管理」模块，其他模块通过公开
 * Service/API 查询或请求变更，不直接操作对方 Mapper。
 *
 * 本接口即导员模块面向“年级管理”公开能力的适配层。当前阶段年级管理模块尚未落地，
 * 由 {@link ClassApiImpl} 基于共享参考表 edu_class 提供存在性校验；年级模块就绪后，
 * 仅需替换该实现即可，而无需改动导员 Service。
 *
 * @author edu-manage
 */
public interface ClassApi {

    /**
     * 校验班级存在且有效（启用），并返回其中“有效”的班级编号集合。
     *
     * 若传入的班级编号存在无效项（不存在或未启用），应按实现约定抛出业务异常；若不抛出，
     * 调用方需自行过滤返回值与入参的差集进行后续处理。
     *
     * @param classIds 班级编号集合（已去重）
     * @return 有效（存在且启用）的班级编号集合
     */
    List<Long> validateClassesExist(List<Long> classIds);

}