package org.example.service.epcc.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeqSegment implements Serializable {

    private Long startSeq;

    private Long endSeq;

    public SeqSegment(Long startSeq, Long endSeq) {
        this.startSeq = startSeq;
        this.endSeq = endSeq;
    }
}
