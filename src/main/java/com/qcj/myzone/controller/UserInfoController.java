package com.qcj.myzone.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qcj.myzone.services.UserService;

@Controller
@RequestMapping("userinfo")
public class UserInfoController {
	
	@Resource
	public UserService userService;
	
	@RequestMapping("")
	public ModelAndView userinfo(HttpSession session){
		ModelAndView mav = new ModelAndView();
		if(session.getAttribute("username") == null){
			mav.setViewName("/");
			return mav;
		}
		mav.addObject(userService.getUser((String)session.getAttribute("username")));
		mav.setViewName("userinfo");
		return mav;
	}
	/**
	 * 取得用户名
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "getUsername", method = RequestMethod.POST)
	public @ResponseBody String getUsername(HttpSession session){
		return (String)session.getAttribute("username");
	}
	/**
	 * 检查用户名是否可以更改
	 * @param session
	 * @param newUsername
	 * @return
	 */
	@RequestMapping(value = "checkUsername", method = RequestMethod.POST)
	public @ResponseBody String checkUsername(HttpSession session,String newUsername){
		return userService.checkUsername(newUsername, (String)session.getAttribute("username"));
	}
	/**
	 * 修改用户名
	 * @param session
	 * @param newUsername
	 * @return
	 */
	@RequestMapping(value = "modifyUsername", method = RequestMethod.POST)
	public @ResponseBody String modifyUsername(HttpSession session,String newUsername){
		String msg = userService.modifyUsername(newUsername, userService.getUser((String)session.getAttribute("username")));
		if(msg.equals("更名成功")){
			session.setAttribute("username", newUsername);
		}
		return msg;
	}
	/**
	 * 检查密码是否可以更改
	 * @param session
	 * @param newPassword
	 * @return
	 */
	@RequestMapping(value = "checkPassword", method = RequestMethod.POST)
	public @ResponseBody String checkPassword(HttpSession session,String newPassword){
		return userService.checkUsername((String)session.getAttribute("username"),newPassword);
	}
	/**
	 * 更改密码
	 * @param session
	 * @param newPassword
	 * @return
	 */
	@RequestMapping(value = "modifyPassword", method = RequestMethod.POST)
	public @ResponseBody String modifyPassword(HttpSession session,String newPassword){
		String msg = userService.modifyPassword(userService.getUser((String)session.getAttribute("username")),newPassword);
		return msg;
	}
	
}
