package com.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.annotation.IgnoreAuth;

import com.entity.LiuyanbeizhuEntity;
import com.entity.view.LiuyanbeizhuView;

import com.service.LiuyanbeizhuService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;
import java.io.IOException;

/**
 * 留言备注
 * 后端接口
 * @author 
 * @email 
 * @date 2022-03-25 14:13:13
 */
@RestController
@RequestMapping("/liuyanbeizhu")
public class LiuyanbeizhuController {
    @Autowired
    private LiuyanbeizhuService liuyanbeizhuService;


    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,LiuyanbeizhuEntity liuyanbeizhu,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("shangjia")) {
			liuyanbeizhu.setShangjiabianhao((String)request.getSession().getAttribute("username"));
		}
		if(tableName.equals("yonghu")) {
			liuyanbeizhu.setYonghuming((String)request.getSession().getAttribute("username"));
		}
        EntityWrapper<LiuyanbeizhuEntity> ew = new EntityWrapper<LiuyanbeizhuEntity>();
		PageUtils page = liuyanbeizhuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, liuyanbeizhu), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,LiuyanbeizhuEntity liuyanbeizhu, 
		HttpServletRequest request){
        EntityWrapper<LiuyanbeizhuEntity> ew = new EntityWrapper<LiuyanbeizhuEntity>();
		PageUtils page = liuyanbeizhuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, liuyanbeizhu), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( LiuyanbeizhuEntity liuyanbeizhu){
       	EntityWrapper<LiuyanbeizhuEntity> ew = new EntityWrapper<LiuyanbeizhuEntity>();
      	ew.allEq(MPUtil.allEQMapPre( liuyanbeizhu, "liuyanbeizhu")); 
        return R.ok().put("data", liuyanbeizhuService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(LiuyanbeizhuEntity liuyanbeizhu){
        EntityWrapper< LiuyanbeizhuEntity> ew = new EntityWrapper< LiuyanbeizhuEntity>();
 		ew.allEq(MPUtil.allEQMapPre( liuyanbeizhu, "liuyanbeizhu")); 
		LiuyanbeizhuView liuyanbeizhuView =  liuyanbeizhuService.selectView(ew);
		return R.ok("查询留言备注成功").put("data", liuyanbeizhuView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        LiuyanbeizhuEntity liuyanbeizhu = liuyanbeizhuService.selectById(id);
        return R.ok().put("data", liuyanbeizhu);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        LiuyanbeizhuEntity liuyanbeizhu = liuyanbeizhuService.selectById(id);
        return R.ok().put("data", liuyanbeizhu);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody LiuyanbeizhuEntity liuyanbeizhu, HttpServletRequest request){
    	liuyanbeizhu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(liuyanbeizhu);
        liuyanbeizhuService.insert(liuyanbeizhu);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody LiuyanbeizhuEntity liuyanbeizhu, HttpServletRequest request){
    	liuyanbeizhu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(liuyanbeizhu);
        liuyanbeizhuService.insert(liuyanbeizhu);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody LiuyanbeizhuEntity liuyanbeizhu, HttpServletRequest request){
        //ValidatorUtils.validateEntity(liuyanbeizhu);
        liuyanbeizhuService.updateById(liuyanbeizhu);//全部更新
        return R.ok();
    }
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        liuyanbeizhuService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 提醒接口
     */
	@RequestMapping("/remind/{columnName}/{type}")
	public R remindCount(@PathVariable("columnName") String columnName, HttpServletRequest request, 
						 @PathVariable("type") String type,@RequestParam Map<String, Object> map) {
		map.put("column", columnName);
		map.put("type", type);
		
		if(type.equals("2")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			Date remindStartDate = null;
			Date remindEndDate = null;
			if(map.get("remindstart")!=null) {
				Integer remindStart = Integer.parseInt(map.get("remindstart").toString());
				c.setTime(new Date()); 
				c.add(Calendar.DAY_OF_MONTH,remindStart);
				remindStartDate = c.getTime();
				map.put("remindstart", sdf.format(remindStartDate));
			}
			if(map.get("remindend")!=null) {
				Integer remindEnd = Integer.parseInt(map.get("remindend").toString());
				c.setTime(new Date());
				c.add(Calendar.DAY_OF_MONTH,remindEnd);
				remindEndDate = c.getTime();
				map.put("remindend", sdf.format(remindEndDate));
			}
		}
		
		Wrapper<LiuyanbeizhuEntity> wrapper = new EntityWrapper<LiuyanbeizhuEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}

		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("shangjia")) {
			wrapper.eq("shangjiabianhao", (String)request.getSession().getAttribute("username"));
		}
		if(tableName.equals("yonghu")) {
			wrapper.eq("yonghuming", (String)request.getSession().getAttribute("username"));
		}

		int count = liuyanbeizhuService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	







}
