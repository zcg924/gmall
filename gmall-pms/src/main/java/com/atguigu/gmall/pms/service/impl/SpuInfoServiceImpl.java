package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.dao.*;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.service.ProductAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;
import com.atguigu.gmall.pms.service.SpuInfoService;
import com.atguigu.gmall.pms.vo.BaseAttrValueVO;
import com.atguigu.gmall.pms.vo.SkuInfoVO;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.atguigu.sms.vo.SaleVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescDao spuInfoDescDao;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoDao skuInfoDao;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private AttrDao attrDao;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private GmallSmsClient smsClient;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuByCidOrKey(QueryCondition condition, Long catId) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        //判断是否为全站，查本类
        if(catId != 0l){
            wrapper.eq("catalog_id",catId);
        }
        //关键字查询
        String key = condition.getKey();
        if(StringUtils.isNotBlank(key)){
            wrapper.and(t -> t.eq("id",key).or().like("spu_name",key));
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(condition),
                wrapper

        );
        return new PageVo(page);
    }

    @Override
    public void saveSpuInfoVO(SpuInfoVO spuInfoVO) {
        //保存spu基本信息
        spuInfoVO.setPublishStatus(1);
        spuInfoVO.setCreateTime(new Date());
        spuInfoVO.setUodateTime(spuInfoVO.getCreateTime());
        this.save(spuInfoVO);
        Long spuId = spuInfoVO.getId();//获取新增时的id
        // 保存spu的描述信息 spu_info_desc
        List<String> spuImages = spuInfoVO.getSpuImages();
        if(!CollectionUtils.isEmpty(spuImages)) {
            SpuInfoDescEntity infoDescEntity = new SpuInfoDescEntity();
            infoDescEntity.setSpuId(spuId);
            infoDescEntity.setDecript(StringUtils.join(spuImages,","));
            this.spuInfoDescDao.insert(infoDescEntity);
        }

        // 1.3. 保存spu的规格参数信息
        List<BaseAttrValueVO> baseAttrs = spuInfoVO.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)) {
            List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(productAttrValueVO -> {
                productAttrValueVO.setSpuId(spuId);
                productAttrValueVO.setAttrSort(0);
                productAttrValueVO.setQuickShow(0);
                return productAttrValueVO;
            }).collect(Collectors.toList());
            this.productAttrValueService.saveBatch(productAttrValueEntities);
        }
        /// 2. 保存sku相关信息
        List<SkuInfoVO> skuInfoVOS = spuInfoVO.getSkus();
        if (CollectionUtils.isEmpty(skuInfoVOS)){
            return;
        }
        skuInfoVOS.forEach(skuInfoVO -> {
            // 2.1. 保存sku基本信息
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(skuInfoVO, skuInfoEntity);
            // 品牌和分类的id需要从spuInfo中获取
            skuInfoEntity.setBrandId(spuInfoVO.getBrandId());
            skuInfoEntity.setCatalogId(spuInfoVO.getCatalogId());
            // 获取随机的uuid作为sku的编码
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString().substring(0,10).toUpperCase());
            // 获取图片列表
            List<String> images = skuInfoVO.getImages();
            // 如果图片列表不为null，则设置默认图片
            if(!CollectionUtils.isEmpty(images)){
                skuInfoEntity.setSkuDefaultImg(skuInfoEntity.getSkuDefaultImg()==null?images.get(0):skuInfoEntity.getSkuDefaultImg());
            }
            skuInfoEntity.setSpuId(spuId);
            this.skuInfoDao.insert(skuInfoEntity);
            // 2.2. 保存sku图片信息
            Long skuId = skuInfoEntity.getSkuId();
            if(!CollectionUtils.isEmpty(images)){
                String defaultImage =images.get(0);
                List<SkuImagesEntity> skuImages= images.stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setDefaultImg(StringUtils.equals(defaultImage, image) ? 1 : 0);
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(image);
                    skuImagesEntity.setImgSort(0);
                    return skuImagesEntity;

                }).collect(Collectors.toList());
                this.skuImagesService.saveBatch(skuImages);
            }

            // 2.3. 保存sku的规格参数（销售属性）
            List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVO.getSaleAttrs();
            if(!CollectionUtils.isEmpty(saleAttrs)){
                saleAttrs.forEach(saleAttr -> {
                    saleAttr.setAttrName(this.attrDao.selectById(saleAttr.getAttrId()).getAttrName());
                    saleAttr.setAttrSort(0);
                    saleAttr.setSkuId(skuId);
                });
                this.skuSaleAttrValueService.saveBatch(saleAttrs);
            }

            // 3. 保存营销相关信息，需要远程调用gmall-sms
            SaleVO saleVO = new SaleVO();
            BeanUtils.copyProperties(skuInfoVO,saleVO);
            saleVO.setSkuId(skuId);
            this.smsClient.saveSales(saleVO);
        });



    }

}