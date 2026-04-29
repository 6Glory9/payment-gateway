package org.example.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.repository.dao.SeqGeneratorMapper;
import org.example.repository.dao.SeqGeneratorMapperExt;
import org.example.repository.po.SeqGeneratorPO;
import org.example.repository.service.ISeqGeneratorService;
import org.example.repository.service.ISeqGeneratorServiceExt;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author final
 * @since 2026-04-29 14:36:51
 */
@Service
public class SeqGeneratorServiceExtImpl extends ServiceImpl<SeqGeneratorMapperExt, SeqGeneratorPO> implements ISeqGeneratorServiceExt {

    public SeqGeneratorPO getSegmentLock(String date, String instCode) {
        return baseMapper.getSegmentLock(date, instCode);
    }
}
