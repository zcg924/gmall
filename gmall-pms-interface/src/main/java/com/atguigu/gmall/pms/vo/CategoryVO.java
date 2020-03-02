package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

/**
 * @author zcgstart
 * @create 2020-02-11 11:26
 */
@Data
public class CategoryVO extends CategoryEntity {
    private List<CategoryEntity> subs;
}
