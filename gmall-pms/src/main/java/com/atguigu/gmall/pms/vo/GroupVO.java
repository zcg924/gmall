package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.api.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.api.entity.AttrEntity;
import com.atguigu.gmall.pms.api.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;
@Data
public class GroupVO extends AttrGroupEntity {

    private List<AttrEntity> attrEntities;

    private List<AttrAttrgroupRelationEntity> relations;

}
