package org.example.service.epcc.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.po.SeqGeneratorPO;
import org.example.repository.service.ISeqGeneratorServiceExt;
import org.example.service.epcc.po.SeqSegment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class SeqGeneratorService {
    @Resource
    private ISeqGeneratorServiceExt seqGeneratorServiceExt;

    @Transactional(rollbackFor = Exception.class)
    public SeqSegment applySegment(String today, String instCode) {

        SeqGeneratorPO seqGeneratorPO = seqGeneratorServiceExt.getSegmentLock(today, instCode);

        if (seqGeneratorPO == null) {
            seqGeneratorPO = getTodayFirst(today, instCode);
        }

        if (seqGeneratorPO == null) {
            throw new RuntimeException("Failed to get seq generator po");
        }

        updateCurrentStep(seqGeneratorPO);

        // 获取号段起止
        return new SeqSegment(seqGeneratorPO.getCurrentSeq(), seqGeneratorPO.getCurrentSeq() + seqGeneratorPO.getStep());
    }

    private void updateCurrentStep(SeqGeneratorPO seqGeneratorPO) {
        SeqGeneratorPO seqGeneratorUpdate = new SeqGeneratorPO();
        seqGeneratorUpdate.setId(seqGeneratorPO.getId());
        seqGeneratorUpdate.setCurrentSeq(seqGeneratorPO.getCurrentSeq() + seqGeneratorPO.getStep());
        seqGeneratorServiceExt.updateById(seqGeneratorUpdate);
    }

    private SeqGeneratorPO getTodayFirst(String today, String instCode) {
        SeqGeneratorPO seqGeneratorTodayFirst = createTodayFirst(today, instCode);
        try {
            seqGeneratorServiceExt.save(seqGeneratorTodayFirst);
        } catch (DuplicateKeyException e) {
            try {
                Thread.sleep(500);
                //暂停一下，一会再去获取
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            log.warn("Duplicate key exception occurred");
        }
        return seqGeneratorServiceExt.getSegmentLock(today, instCode);
    }

    private SeqGeneratorPO createTodayFirst(String today, String instCode) {
        SeqGeneratorPO seqGeneratorPO = new SeqGeneratorPO();
        seqGeneratorPO.setBizDate(today);
        seqGeneratorPO.setInstCode(instCode);
        seqGeneratorPO.setCurrentSeq(0L);
        seqGeneratorPO.setStep(10000);
        return seqGeneratorPO;
    }
}