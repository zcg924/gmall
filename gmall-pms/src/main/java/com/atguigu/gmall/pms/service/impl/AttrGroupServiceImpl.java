package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.dao.ProductAttrValueDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.pms.vo.ItemGroupVO;
import net.bytebuddy.matcher.CollectionErasureMatcher;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private AttrAttrgroupRelationDao relationDao;
    @Autowired
    private AttrDao attrDao;
    @Autowired
    private ProductAttrValueDao attrValueDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryGroupByCidPage(QueryCondition queryCondition, Long catId) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(queryCondition),
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id",catId)
        );

        return new PageVo(page);
    }

    @Override
    public GroupVO queryGroupVoByGid(Long gid) {
        GroupVO groupVO = new GroupVO();
        //根据gid查询组
        AttrGroupEntity groupEntity = this.getById(gid);
        BeanUtils.copyProperties(groupEntity,groupVO);

        //查询中间表
        List<AttrAttrgroupRelationEntity> relationEntities = this.relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", gid));
        groupVO.setRelations(relationEntities);

        //判断中间表是否为空
        if(CollectionUtils.isEmpty(relationEntities)){
            return groupVO;
        }

        //获取所有规格参数的Id
        List<Long> attrIds = relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        //查询规格参数
        List<AttrEntity> attrEntities = this.attrDao.selectBatchIds(attrIds);
        groupVO.setAttrEntities(attrEntities);

        return groupVO;
    }

    @Override
    public List<GroupVO> queryGroupVOsByCatId(Long catId) {
        //根据分类的id查询规格参数组
        List<AttrGroupEntity> groupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));
        //遍历规格参数组查询每个组下中间关系
        return groupEntities.stream().map(attrGroupEntity -> this.queryGroupVoByGid(attrGroupEntity.getAttrGroupId())).collect(Collectors.toList());
    }

    @Override
    public List<ItemGroupVO> queryItemGroupVOsByCidAndSpuId(Long cid, Long spuId) {

        //根据sku中的categoryId查询分组
        List<AttrGroupEntity> groupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cid));
        if(CollectionUtils.isEmpty(groupEntities)){
            return null;
        }
        List<ItemGroupVO> itemGroupVOList = groupEntities.stream().map(group -> {
            ItemGroupVO itemGroupVO = new ItemGroupVO();
            itemGroupVO.setId(group.getAttrGroupId());
            itemGroupVO.setName(group.getAttrGroupName());
            //遍历组到中间表查询每个组的规格参数Id
            List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = this.relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", group.getAttrGroupId()));
            if (!CollectionUtils.isEmpty(attrAttrgroupRelationEntities)) {

                List<Long> attrIds = attrAttrgroupRelationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
                //根据spuId和attrId查询规格参数名及值
                List<ProductAttrValueEntity> attrValueEntities = this.attrValueDao.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_Id", spuId).in("attr_Id", attrIds));

                itemGroupVO.setBaseAttrValues(attrValueEntities);
            }
            return itemGroupVO;
        }).collect(Collectors.toList());

        return itemGroupVOList;
    }


}