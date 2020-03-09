package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import lombok.Data;

import java.util.List;

/**
 * @author zcgstart
 * @create 2020-03-05 21:35
 */
@Data
public class ItemGroupVO {

    private Long id;
    private String name;//分组的名字
    private List<ProductAttrValueEntity> baseAttrValues;
}
