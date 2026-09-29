// TChannelReconFileMapper.java
package org.example.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.repository.po.ReconSummaryVO;
import org.example.repository.po.TChannelReconFile;

import java.util.List;

public interface TChannelReconFileMapper extends BaseMapper<TChannelReconFile> {
    //渠道汇总统计
    @Select("SELECT channel_code,trans_date,COUNT(1) cnt,SUM(trans_amt) total_amt,SUM(fee_amt) total_fee " +
            "FROM t_channel_recon_file WHERE trans_date=#{transDate} GROUP BY channel_code,trans_date")
    List<ReconSummaryVO> summaryChannel(@Param("transDate") String transDate);
}
