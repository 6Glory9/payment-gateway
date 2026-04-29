package org.example.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.repository.po.SeqGeneratorPO;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author final
 * @since 2026-04-29 14:36:51
 */
public interface SeqGeneratorMapperExt extends BaseMapper<SeqGeneratorPO> {

    @Select("SELECT * FROM seq_generator WHERE date = #{date} AND inst_code = #{instCode} FOR UPDATE")
    SeqGeneratorPO getSegmentLock(@Param("date") String date, @Param("instCode") String instCode);
}
