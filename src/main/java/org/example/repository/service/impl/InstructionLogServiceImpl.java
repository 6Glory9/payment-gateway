package org.example.repository.service.impl;

import org.example.repository.po.InstructionLogPO;
import org.example.repository.dao.InstructionLogMapper;
import org.example.repository.service.IInstructionLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 渠道请求记录表 服务实现类
 * </p>
 *
 * @author final
 * @since 2026-04-29 14:36:51
 */
@Service
public class InstructionLogServiceImpl extends ServiceImpl<InstructionLogMapper, InstructionLogPO> implements IInstructionLogService {

}
