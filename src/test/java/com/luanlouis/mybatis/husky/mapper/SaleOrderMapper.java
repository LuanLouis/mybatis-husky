package com.luanlouis.mybatis.husky.mapper;

import com.luanlouis.mybatis.husky.beans.SaleOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能描述:
 * <p>
 * <p>
 * 相关设计：
 *
 * @author terrylouis1991@gmail.com
 * @date 2019/04/09
 */
@Mapper
public interface SaleOrderMapper {



    int insert(@Param("saleOrder") SaleOrder saleOrder,@Param("tenantId") String tenantId);

    List<SaleOrder> selectAll(@Param("tenantId") String tenantId);


}
