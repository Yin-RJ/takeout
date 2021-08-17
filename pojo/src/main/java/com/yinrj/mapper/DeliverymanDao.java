package com.yinrj.mapper;

import com.yinrj.entity.Deliveryman;
import com.yinrj.entity.DeliverymanExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeliverymanDao {
    long countByExample(DeliverymanExample example);

    int deleteByExample(DeliverymanExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Deliveryman record);

    int insertSelective(Deliveryman record);

    List<Deliveryman> selectByExample(DeliverymanExample example);

    Deliveryman selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Deliveryman record, @Param("example") DeliverymanExample example);

    int updateByExample(@Param("record") Deliveryman record, @Param("example") DeliverymanExample example);

    int updateByPrimaryKeySelective(Deliveryman record);

    int updateByPrimaryKey(Deliveryman record);
}