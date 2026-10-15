package cn.iocoder.yudao.module.edu.service.clazz;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.edu.dal.dataobject.clazz.EduClassDO;
import cn.iocoder.yudao.module.edu.dal.mysql.clazz.EduClassMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ClassApi 默认实现
 *
 * 基于共享参考表 {@link EduClassDO} 校验班级存在与启用状态。
 * 年级管理模块落地后，应由其公开 Service 替换本实现（仅需新增一个更优的实现 Bean 并显式注入）。
 * 当前实现不抛出“班级不存在”异常，而是返回“有效”集合，由调用方比对差集决策，便于复用与扩展。
 *
 * @author edu-manage
 */
@Slf4j
@Service
public class ClassApiImpl implements ClassApi {

    @Resource
    private EduClassMapper eduClassMapper;

    /**
     * 去重并校验班级存在且启用，返回有效班级编号集合
     *
     * @param classIds 原始班级编号集合（允许含重复）
     * @return 去重后的有效班级编号集合
     */
    @Override
    public List<Long> validateClassesExist(List<Long> classIds) {
        Set<Long> distinctIds = new HashSet<>(classIds);
        if (distinctIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<EduClassDO> classes = eduClassMapper.selectListEnableByIds(distinctIds);
        List<Long> existIds = CollectionUtils.convertList(classes, EduClassDO::getId);
        // 记录未通过校验的班级，便于排查
        Set<Long> invalid = new HashSet<>(distinctIds);
        invalid.removeAll(existIds);
        if (!invalid.isEmpty()) {
            log.warn("[validateClassesExist] 以下班级不存在或未启用，已忽略：{}", invalid);
        }
        return existIds;
    }

}