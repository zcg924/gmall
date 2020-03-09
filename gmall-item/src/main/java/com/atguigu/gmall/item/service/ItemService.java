package com.atguigu.gmall.item.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.config.ThreadPoolConfig;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.item.feign.GmallWmsClient;
import com.atguigu.gmall.item.vo.ItemVO;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVO;
import com.atguigu.sms.vo.ItemSaleVO;
import com.atguigu.wms.Api.entity.WareSkuEntity;
import com.baomidou.mybatisplus.extension.api.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author zcgstart
 * @create 2020-03-05 21:49
 */
@Service
public class ItemService {

    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private GmallSmsClient smsClient;
    @Autowired
    private GmallWmsClient wmsClient;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    public ItemVO queryItemVO(Long skuId) {

        ItemVO itemVO = new ItemVO();

        itemVO.setSkuId(skuId);
        //根据skuId查询sku
        CompletableFuture<SkuInfoEntity> skuCompletableFuture = CompletableFuture.supplyAsync(() -> {

            Resp<SkuInfoEntity> skuInfoEntityResp = this.pmsClient.querySkuById(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            if (skuInfoEntity == null) {
                return null;
            }
            itemVO.setWeight(skuInfoEntity.getWeight());
            itemVO.setSkuTitle(skuInfoEntity.getSkuTitle());
            itemVO.setSkuSubTitle(skuInfoEntity.getSkuSubtitle());
            itemVO.setPrice(skuInfoEntity.getPrice());
            return skuInfoEntity;
        }, threadPoolExecutor);

        CompletableFuture<Void> categoryCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {

            //根据sku中的categoryId查询分类
            Resp<CategoryEntity> categoryEntityResp = this.pmsClient.queryCategoryById(skuInfoEntity.getCatalogId());
            CategoryEntity categoryEntity = categoryEntityResp.getData();
            if (categoryEntity != null) {
                itemVO.setCategoryId(categoryEntity.getCatId());
                itemVO.setCategoryName(categoryEntity.getName());
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> brandCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {

            //根据sku中的brandId查询品牌
            Resp<BrandEntity> brandEntityResp = this.pmsClient.queryBrandById(skuInfoEntity.getBrandId());
            BrandEntity brandEntity = brandEntityResp.getData();
            if (brandEntity != null) {
                itemVO.setBrandId(brandEntity.getBrandId());
                itemVO.setBrandName(brandEntity.getName());
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> spuCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //根据sku中的spuId查询spu
            Resp<SpuInfoEntity> spuInfoEntityResp = this.pmsClient.querySpuById(skuInfoEntity.getSpuId());
            SpuInfoEntity spuInfoEntity = spuInfoEntityResp.getData();
            if (spuInfoEntity != null) {
                itemVO.setSpuId(spuInfoEntity.getId());
                itemVO.setSpuName(spuInfoEntity.getSpuName());
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> imgCompletableFuture = CompletableFuture.runAsync(() -> {
            //根据skuId查询图片
            Resp<List<SkuImagesEntity>> listResp = this.pmsClient.queryImagesBySkuId(skuId);
            List<SkuImagesEntity> skuImagesEntities = listResp.getData();
            if (!CollectionUtils.isEmpty(skuImagesEntities)) {
                List<String> imageUrls = skuImagesEntities.stream().map(skuImagesEntity -> skuImagesEntity.getImgUrl()).collect(Collectors.toList());
                itemVO.setImages(imageUrls);
            }
        }, threadPoolExecutor);


        //根据skuId查询库存信息
        CompletableFuture<Void> storeCompletableFuture = CompletableFuture.runAsync(() -> {
            Resp<List<WareSkuEntity>> wareSkuBySkuId = this.wmsClient.queryWareSkuBySkuId(skuId);
            List<WareSkuEntity> wareSkuEntities = wareSkuBySkuId.getData();
            if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                itemVO.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0));
            }
        }, threadPoolExecutor);


        CompletableFuture<Void> boundCompletableFuture = CompletableFuture.runAsync(() -> {

            //根据skuId查询营销信息： 积分 打折 满减
            Resp<List<ItemSaleVO>> itemSaleVOBySkuId = this.smsClient.queryItemSaleVOBySkuId(skuId);
            List<ItemSaleVO> itemSaleVOS = itemSaleVOBySkuId.getData();
            itemVO.setSales(itemSaleVOS);
        }, threadPoolExecutor);

        CompletableFuture<Void> descCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //根据sku中的spuId查询描述信息
            Resp<SpuInfoDescEntity> spuInfoDescEntityResp = this.pmsClient.queryDescBySpuId(skuInfoEntity.getSpuId());
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescEntityResp.getData();
            if (spuInfoDescEntity != null && StringUtils.isNotBlank(spuInfoDescEntity.getDecript())) {

                List<String> desc = Arrays.asList(StringUtils.split(spuInfoDescEntity.getDecript(), ","));
                itemVO.setDesc(desc);
            }
        }, threadPoolExecutor);


        //根据sku中的categoryId查询分组
        //遍历组到中间表查询每个组的规格参数Id
        //根据spuId和attrId查询规格参数名及值
        CompletableFuture<Void> groupCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            Resp<List<ItemGroupVO>> itemGroupVOsByCidAndSpuId = this.pmsClient.queryItemGroupVOsByCidAndSpuId(skuInfoEntity.getCatalogId(), skuInfoEntity.getSpuId());
            List<ItemGroupVO> itemGroupVOS = itemGroupVOsByCidAndSpuId.getData();
            if (!CollectionUtils.isEmpty(itemGroupVOS)) {
                itemVO.setGroupVOS(itemGroupVOS);
            }
        }, threadPoolExecutor);


        //根据sku中的spuId查询skus
        //根据skus获取skuIds
        //根据skuIds查询销售属性
        CompletableFuture<Void> saleCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            Resp<List<SkuSaleAttrValueEntity>> skuSaleAttrValueEntities = this.pmsClient.querySaleAttrValueBySpuId(skuInfoEntity.getSpuId());
            List<SkuSaleAttrValueEntity> saleAttrValueEntities = skuSaleAttrValueEntities.getData();
            if (!CollectionUtils.isEmpty(saleAttrValueEntities)) {
                itemVO.setSaleAttrValues(saleAttrValueEntities);
            }
        }, threadPoolExecutor);

        CompletableFuture.allOf(categoryCompletableFuture, brandCompletableFuture, spuCompletableFuture, imgCompletableFuture, storeCompletableFuture, boundCompletableFuture, descCompletableFuture, groupCompletableFuture, saleCompletableFuture).join();
        return itemVO;
    }

    public static void main(String[] args) {
//        MyTread myTread = new MyTread();
//        myTread.start();
//        MyRunnable myRunnable = new MyRunnable();
//        new Thread(myRunnable).start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("这也是一个子线程方法！Runnable接口匿名内部类");
//            }
//        }).start();

//        new Thread(() -> {
//            System.out.println("这是个子线程方法！lambda表达式");
//        }).start();

//        FutureTask futureTask = new FutureTask<>(new MyCallable());
//        new Thread(futureTask).start();
//        try {
//            System.out.println(futureTask.get());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//
//        ExecutorService executorService = Executors.newFixedThreadPool(5);
//        for (int i = 0; i < 10; i++) {
//            executorService.execute(() -> {
//                System.out.println("线程池开启一个子任务" + Thread.currentThread().getName());
//            });
//        }
//        ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(2);
//        threadPool.schedule(() -> {
//            System.out.println("线程池开启一个子任务" + Thread.currentThread().getName());
//        }, 20, TimeUnit.SECONDS);
//        threadPool.scheduleAtFixedRate(() -> {
//            System.out.println("线程池的定时任务：" + Thread.currentThread().getName());
//        }, 2, 5, TimeUnit.SECONDS);
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 5, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
//
//        for (int i = 0; i <300 ; i++) {
//            threadPoolExecutor.execute(() -> {
//                System.out.println("自定义线程执行任务" + Thread.currentThread().getName());
//            });
//        }
//        System.out.println("这是主线程" );
//        CompletableFuture.runAsync(() -> {
//            System.out.println("开启一个不带返回值的子任务");
//        });

        CompletableFuture.supplyAsync(() -> {
            System.out.println("开启一个带返回值的子任务");
            //int i = 1 / 0;
            return "hello";
        }).whenCompleteAsync((t, u) -> {
            System.out.println("t:" + t);
            System.out.println("u:" + u);
        }).handleAsync((t, u) -> {
            System.out.println("hand t:" + t);
            System.out.println("hand u:" + u);
            return "handle";
        });

    }
}
class MyRunnable implements Runnable{

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("这是个子线程,Runnable");
    }
}


class MyTread extends Thread{
    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("这是个子线程");
    }
}

class MyCallable implements Callable<String>{

    @Override
    public String call() throws Exception {
        System.out.println("这是个实现Callable的子线程方法");
        return "hello callable";
    }
}
