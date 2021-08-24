package com.yinrj.mapper;

import com.yinrj.entity.Settlement;
import com.yinrj.entity.SettlementExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SettlementDao {
    long countByExample(SettlementExample example);

    int deleteByExample(SettlementExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Settlement record);

    int insertSelective(Settlement record);

    List<Settlement> selectByExample(SettlementExample example);

    Settlement selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Settlement record, @Param("example") SettlementExample example);

    int updateByExample(@Param("record") Settlement record, @Param("example") SettlementExample example);

    int updateByPrimaryKeySelective(Settlement record);

    int updateByPrimaryKey(Settlement record);
}