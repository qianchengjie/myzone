package com.qcj.myzone.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.qcj.myzone.model.blackboard.Floor;
import com.qcj.myzone.model.blackboard.Reply;
import com.qcj.myzone.model.user.User;
import com.qcj.myzone.repository.FloorRepository;
import com.qcj.myzone.repository.ReplyRepository;
import com.qcj.myzone.repository.user.UserRepository;
import com.qiniu.common.Zone;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

@Service
public class BlackboardService {
	
	@Resource
	private FloorRepository floorRepository;
	
	@Resource
	private ReplyRepository replyRepository;
	
	@Resource
	private UserRepository userRepository;
	
	/**
	 * 返回页数
	 * @return
	 */
	public long getPageSum(){
		return (floorRepository.count()-1)/6;
	}
	/**
	 * 找出该pageNum页所有楼层
	 * @param pageNum
	 * @return
	 */
	public Page<Floor> findAll(int pageNum){
		Sort sort = new Sort(Sort.Direction.DESC, "id"); 
		Pageable pageable = new PageRequest(pageNum, 6, sort);
		return floorRepository.findAll(pageable);
	}
	/**
	 * 留言
	 * @param floor
	 * @return
	 */
	public Map<String, Object> leaveMessage(Floor floor){
		String msg = "留言成功";
		floor.setImgSrc(userRepository.getImgSrc(floor.getUsername()));
		floor.setTime(new Date().toLocaleString());
		floor.setFlNum((int) floorRepository.count()+1);
		floor = floorRepository.save(floor);
		Map<String,Object>map = new HashMap<>();
		map.put("floor",floor);
		map.put("msg",msg);
		return map;
	}
	/**
	 * 删除留言
	 * @param floorId
	 * @return
	 */
	public Map<String, Object> deleteMessage(int floorId,int currentPage){
		String msg = "删除成功";
		long count = floorRepository.count();
		long pageSum = (count-1)/6+1;
		Map<String, Object> map = new HashMap<>();
		if(count > 6){
			if(currentPage <  pageSum){
				Sort sort = new Sort(Sort.Direction.DESC, "id");
				Pageable pageable = new PageRequest(currentPage, 6, sort);
				Page<Floor> page = floorRepository.findAll(pageable);
				List<Floor> list = page.getContent();
				Floor floor = list.get(0);
				map.put("floor", floor);
			}
		}else{
			msg = "删除成功，仅剩一页";
		}
		floorRepository.delete(floorId);
		map.put("msg", msg);
		return map;
	}
	/**
	 * 找出该id楼层的所有评论
	 * @param id
	 * @return
	 */
	public Iterable<Reply> findAllReply(int id){
		return replyRepository.findAllReplyById(id);
	}
	/**
	 * 新增回复
	 * @param reply
	 * @return
	 */
	public String reply(Reply reply){
		reply.setImgSrc(userRepository.getImgSrc(reply.getUsername()));
		reply.setTime(new Date().toLocaleString());
		replyRepository.save(reply);
		Floor floor = floorRepository.findOne(reply.getFloorId());
		floor.setRpCount(floor.getRpCount()+1);
		floorRepository.save(floor);
		return "回复成功";
	}
	/**
	 * 新增赞
	 * @param floorId
	 * @return
	 */
	public int zan(int floorId){
		Floor floor = floorRepository.findOne(floorId);
		floor.setZanCount(floor.getZanCount()+1);
		floorRepository.save(floor);
		return floorRepository.getZanCount(floorId);
	}
	/**
	 * 获得upToken
	 * @return
	 */
	public Map<String, String> getUpToken(){
		//构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone2());
		//...其他参数参考类注释
		UploadManager uploadManager = new UploadManager(cfg);
		//...生成上传凭证，然后准备上传
		String accessKey = "khCLjbIJ-htjneC2BUtX8zOBSk71wpm1TZnU9s5u";
		String secretKey = "PG0aUetavLZQvD6pp4hvgqDV_5P9_2X5xG5kfGKk";
		String bucket = "imgs";
		//默认不指定key的情况下，以文件内容的hash值作为文件名
		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(bucket);
		Map<String,String> map = new HashMap<>();
		map.put("uptoken", upToken);
		return map;
	}

}
