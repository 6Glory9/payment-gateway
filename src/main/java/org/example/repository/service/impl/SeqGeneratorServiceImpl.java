package org.example.repository.service.impl;

import org.example.repository.po.SeqGeneratorPO;
import org.example.repository.dao.SeqGeneratorMapper;
import org.example.repository.service.ISeqGeneratorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author final
 * @since 2026-04-29 14:36:51
 */
@Service
public class SeqGeneratorServiceImpl extends ServiceImpl<SeqGeneratorMapper, SeqGeneratorPO> implements ISeqGeneratorService {

}
