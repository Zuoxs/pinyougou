package cn.itcast.core.controller;

import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模板管理
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {


    @Reference
    private TypeTemplateService typeTemplateService;
    //查询分页对象 有条件
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody TypeTemplate tt){

        return typeTemplateService.search(page,rows,tt);

    }
    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody TypeTemplate tt){
        try {
            typeTemplateService.add(tt);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    //添加
    @RequestMapping("/update")
    public Result update(@RequestBody TypeTemplate tt){
        try {
            typeTemplateService.update(tt);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    //查询一个模板
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }

    ///开始审核
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status){
        try {
            typeTemplateService.updateStatus(ids,status);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }

}
