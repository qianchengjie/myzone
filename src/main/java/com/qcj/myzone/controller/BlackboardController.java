package com.qcj.myzone.controller;


import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.qcj.myzone.model.blackboard.Floor;
import com.qcj.myzone.model.blackboard.Reply;
import com.qcj.myzone.services.BlackboardService;
import com.qcj.myzone.services.UserService;

@Controller
@RequestMapping("blackboard")
public class BlackboardController {

	@Resource
	private BlackboardService blackboardService;
	
	@Resource
	private UserService userService;
	
	/**
	 * 获取页数
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getpagesum", method = RequestMethod.GET)
	public long pageSum(){
		return blackboardService.getPageSum();
	}
	/**
	 * 查询当前选择页,获取页数并返回数据
	 * @param map
	 * @param pageNum
	 * @param pageSum
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView blackboard(
			@RequestParam(value = "pageNum")int pageNum,
			HttpSession session){
		ModelAndView mav = new ModelAndView();
		Iterable<Floor> floors = blackboardService.findAll(pageNum-1);
		long pageSum = blackboardService.getPageSum();
		mav.addObject("floors",floors);
		mav.addObject("pageSum", pageSum+1);
		mav.addObject("currentPage", pageNum);
		if(session.getAttribute("username") == null){
			//游客
			session.setAttribute("right", 0);
		}else{
			String username = (String) session.getAttribute("username");
			//普通权限
			if(userService.isHavaThisRight(username, 3))
				mav.addObject("right", 3);
			//管理权限
			if(userService.isHavaThisRight(username, 2))
				mav.addObject("right", 2);
		}
		mav.setViewName("blackboard");
		return mav; 
	}
	/**
	 * 留言
	 * @param floor
	 * @return
	 */
	@RequestMapping(value = "leaveMessage", method = RequestMethod.POST)
	public @ResponseBody String leaveMessage(
				@ModelAttribute(value = "floor")Floor floor
			){
		return JSON.toJSONString(blackboardService.leaveMessage(floor));
	}
	/**
	 * 删除留言
	 * @param floorId
	 * @param currentPage
	 * @return
	 */
	@RequestMapping(value = "deleteMessage", method = RequestMethod.POST)
	public @ResponseBody String deleteMessage(int floorId,int currentPage){
		return JSON.toJSONString(blackboardService.deleteMessage(floorId,currentPage));
	}
	/**
	 * 查选当前楼层回复的内容
	 * @param floorId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "viewReply", method = RequestMethod.GET)
	public Iterable viewReply(int floorId){
		return blackboardService.findAllReply(floorId);
	}
	/**
	 * 用户回复
	 * @param reply
	 * @return
	 */
	@RequestMapping(value = "reply", method = RequestMethod.POST)
	@ResponseBody
	public String reply(
			@ModelAttribute(value = "reply") Reply reply
			){
		return blackboardService.reply(reply);
	}
	/**
	 * 获得赞的数量
	 * @param floorId
	 * @return
	 */
	@RequestMapping(value = "zan", method = RequestMethod.POST)
	@ResponseBody
	public int zan(int floorId){
		return blackboardService.zan(floorId);
	}
	/**
	 * 获得上传口令
	 * @return
	 */
	@RequestMapping(value = "getUpToken", method = RequestMethod.GET)
	public @ResponseBody String getUpToken(){
		return JSON.toJSONString(blackboardService.getUpToken());
	}
	
}
