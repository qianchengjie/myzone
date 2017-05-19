package com.qcj.myzone.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.qcj.myzone.model.user.User;
import com.qcj.myzone.model.user.UserRoleRelation;
import com.qcj.myzone.repository.user.RRRRepository;
import com.qcj.myzone.repository.user.URRRepository;
import com.qcj.myzone.repository.user.URightRepository;
import com.qcj.myzone.repository.user.URoleRepository;
import com.qcj.myzone.repository.user.UserRepository;

@Service
public class UserService {
	
	@Resource
	private UserRepository userRepository;
	
	@Resource
	private URRRepository uRRRepository;
	
	@Resource
	private RRRRepository rRRRepository;
	
	@Resource
	private URoleRepository uRoleRepository;
	
	@Resource
	private URightRepository uRightRepository;

	public boolean register1(Map<String,Object> map){
		String username = (String) map.get("username");
		String pwd1 = (String) map.get("pwd1");
		String pwd2 = (String) map.get("pwd2");
		String email = (String) map.get("email");
		MultipartFile file = (MultipartFile)map.get("file");
		if(username == null || "".equals(username)
				|| pwd1 == null || "".equals(pwd1)
				|| pwd2 == null ||  "".equals(pwd2)
				|| !pwd1.equals(pwd2)
				|| email == null || "".equals(email)
				|| file.isEmpty()){
			return false;
		}
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        fileName = username + "." + suffixName;
        // 文件上传后的路径
        String filePath = "e:/imgs";
        // 解决中文问题，liunx下中文路径，图片显示问题
        // fileName = UUID.randomUUID() + suffixName;
        File dest = new File(filePath + fileName);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
			file.transferTo(dest);
			} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
			} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		
		return true;
	}
	/**
	 * 用户注册
	 * @param user
	 * @return
	 */
	public String register(User user){
		String msg = "注册成功";
		if("".equals( user.getUsername() ) || user.getUsername() == null)
			msg = "用户名不能为空";
		else if("".equals( user.getPassword() ) || user.getPassword() == null)
			msg = "密码不能为空";
		else if("".equals( user.getEmail() ) || user.getEmail() == null)
			msg = "邮箱不能为空";
		else if(userRepository.getUsername( user.getUsername() ) != null)
			msg = "用户名已存在";
		else if(userRepository.getEmail( user.getEmail() ) != null)
			msg = "邮箱已被注册";
		else{
			//用户信息默认设置
			user.setImgSrc("img/person.png");
			user.setRegDate(new Date().toLocaleString());
			user = userRepository.save(user);
			UserRoleRelation urr = new UserRoleRelation();
			//用户权限默认设置
			urr.setuId(user.getId());
			urr.setrId(3);
			uRRRepository.save(urr);
		}
		return msg;
	}
	/**
	 * 用户登录
	 * @param user
	 * @return
	 */
	public String login(User user){
		String msg = "登录成功";
		if(userRepository.getUsername( user.getUsername() ) == null)
			msg = "用户名不存在";
		else if(!userRepository.getPassword( user.getUsername() ).equals( user.getPassword() ))
			msg = "密码错误";
		return msg;
	}
	/**
	 * 获得用户ID
	 * @param username
	 * @return
	 */
	public int getUserId(String username){
		return userRepository.getUserId(username);
	}
	/**
	 * 用户名验证
	 * @param username
	 * @return
	 */
	public String checkUsername(String username){
		String msg = "用户名可用";
		if(userRepository.getUsername(username) != null)
			msg = "用户名已存在";
		return msg;
	}
	/**
	 * 邮箱验证
	 * @param email
	 * @return
	 */
	public String checkEmail(String email){
		String msg = "邮箱可用";
		if(userRepository.getEmail(email) != null)
			msg = "邮箱已被注册";
		return msg;
		
	}
	/**
	 * 获得用户对象
	 * @param username
	 * @return
	 */
	public User getUser(String username){
		return userRepository.getUser(username);
	}
	/**
	 * 获得头像
	 * @param usernmae
	 * @return
	 */
	public String getImgSrc(String usernmae){
		return userRepository.getImgSrc(usernmae);
	}
	/**
	 * 获得角色ID
	 * @param uId
	 * @return
	 */
	public int getRoleId(int uId){
		return uRRRepository.getrId(uId);
	}
	/**
	 * 获得角色名
	 * @param rId
	 * @return
	 */
	public String getRoleName(int roleId){
		return uRoleRepository.getRoleName(roleId);
	}
	/**
	 * 获得权限ID组
	 * @param roleId
	 * @return
	 */
	public List<Integer> getRightIdListId(int roleId){
		return rRRRepository.getRightId(roleId);
	}
	/**
	 * 获得拥有权限ID组
	 * @param roleId
	 * @return
	 */
	public List<Integer> getHaveRightIdListId(int roleId){
		return rRRRepository.getHaveRightId(roleId);
	}
	/**
	 * 获得权限名
	 * @param rightId
	 * @return
	 */
	public ArrayList<String> getRightNameList(List<Integer> roleIds){
		ArrayList<String> rightNameList = new ArrayList<>();
		for(int roleId:roleIds){
			rightNameList.add(uRightRepository.getRightName(roleId));
		}
		return rightNameList;
	}
	/**
	 * 判断是有拥有此权限
	 * @param roleId
	 * @param rightId
	 * @return
	 */
	public boolean isHavaThisRight(String username, int rightId){
		for(int right : getHaveRightIdListId(getRoleId(userRepository.getUserId(username)))){
			if( right == rightId){
				return true;
			}
		}
		return false;
	}
	/**
	 * 返回全部用户信息
	 * @return
	 */
	public ArrayList<Map> getAllURR(){
		ArrayList<Map> uRRList = new ArrayList<>();
		
		Iterable<User> users = userRepository.findAll();
		for(User user:users){
			Map<String, Object> map = new HashMap<>();
			map.put("username", user.getUsername());
			map.put("rolename", getRoleName(getRoleId(user.getId())));
			map.put("rightnamelist", getRightNameList(getHaveRightIdListId(getRoleId(user.getId()))));
			uRRList.add(map);
		}
		return uRRList;
	}
	/**
	 * 检查用户是否未更改，是否已存在
	 * @param newUsername
	 * @param oldUsername
	 * @return
	 */
	public String checkUsername(String newUsername,String oldUsername){
		String msg = "用户名可以更改";
		
		if(newUsername.equals(oldUsername))
			msg = "用户名未改变";
		else if(userRepository.getUsername(newUsername) != null)
			msg = "该用户名已存在";
		
		return msg;
	}
	/**
	 * 更改用户名
	 * @param newUsername
	 * @param user
	 * @return
	 */
	public String modifyUsername(String newUsername,User user){
		String msg = "更名成功";
		if(newUsername.equals(user.getUsername())){
			msg = "用户名未更改";
		}else{
			user.setUsername(newUsername);
			userRepository.save(user);
		}
		return msg;
	}
	/**
	 * 检测密码是否未变
	 * @param username
	 * @param newPassword
	 * @return
	 */
	public String checkPassword(String username,String newPassword){
		String msg = "密码可以更改";
		if(newPassword.equals(userRepository.getPassword(username)))
			msg = "密码未改变";
		return msg;
	}
	public String modifyPassword(User user,String newPassword){
		String msg = "改密成功";
		if(newPassword.equals(userRepository.getPassword(user.getUsername())))
			msg = "密码未改变";
		else{
			user.setPassword(newPassword);
			userRepository.save(user);
		}
		return msg;
	}
}
