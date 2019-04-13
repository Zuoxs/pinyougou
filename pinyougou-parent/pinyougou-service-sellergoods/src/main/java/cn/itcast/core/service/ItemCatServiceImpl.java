package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品分类管理
 */
@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<ItemCat> findByParentId(Long parentId) {

        //查询所有商品分类结果集 保存到缓存中
        List<ItemCat> itemCatList = itemCatDao.selectByExample(null);


        //遍历
        for (ItemCat itemCat : itemCatList) {

            //缓存 数据类型 Hash类型
            redisTemplate.boundHashOps("itemCatList").put(itemCat.getName(),itemCat.getTypeId());

        }
        //添加
        //修改
        //删除
        //redisTemplate.boundHashOps("itemCatList").delete(itemCat.getName())






        //从Mysql数据查询
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        itemCatQuery.createCriteria().andParentIdEqualTo(parentId);
        return itemCatDao.selectByExample(itemCatQuery);
    }

    //查询一个
    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }

    @Override
    public PageResult search(Integer page, Integer rows, ItemCat itemCat) {
        List<ItemCat> itemCats = itemCatDao.selectByExample(null);
        for (ItemCat itemCat1 : itemCats) {
            redisTemplate.boundHashOps("itemCatList").put(itemCat1.getName(),itemCat1.getTypeId());
        }

        PageHelper.startPage(page, rows);

        ItemCatQuery itemCatQuery = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = itemCatQuery.createCriteria();
        criteria.andParentIdEqualTo(itemCat.getParentId());

        if (null != itemCat.getStatus() && !"".equals(itemCat.getStatus())) {
            criteria.andStatusEqualTo(itemCat.getStatus());
        }
        if (null != itemCat.getName() && !"".equals(itemCat.getName().trim())) {
            criteria.andNameLike("%" + itemCat.getName() + "%");
        }
        if (null != itemCat.getTypeId() && !"".equals(itemCat.getTypeId())) {
            criteria.andTypeIdEqualTo(itemCat.getTypeId());
        }

        Page<ItemCat> itemCatPage= (Page<ItemCat>) itemCatDao.selectByExample(itemCatQuery);
        return new PageResult(itemCatPage.getTotal(),itemCatPage.getResult());
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        if (null != ids && ids.length > 0) {

            ItemCat itemCat = new ItemCat();
            itemCat.setStatus(status);

            for (Long id : ids) {

                itemCat.setId(id);

                itemCatDao.updateByPrimaryKeySelective(itemCat);


            }

        }
    }
}
