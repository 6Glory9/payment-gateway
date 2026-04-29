package org.example.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.repository.po.SeqGeneratorPO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author final
 * @since 2026-04-29 14:36:51
 */
public interface ISeqGeneratorServiceExt extends IService<SeqGeneratorPO> {
    SeqGeneratorPO getSegmentLock(String date, String instCode);
}
