package com.qcj.myzone.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qcj.myzone.model.FilesInfo;
import com.qcj.myzone.services.FileService;
import com.qiniu.storage.BucketManager.FileListIterator;

@Controller
@RequestMapping("file")
public class FileController {
	
	@Resource
	private FileService fileService;
	
	@RequestMapping("")
	public String upload(){
		return "upload";
	}
	//上传文件
	@RequestMapping(value = "doUpload", method = RequestMethod.POST)
	public @ResponseBody String doUpload(
			MultipartFile file,
			FilesInfo filesInfo){
		System.out.println(file);
		return fileService.doUpload(file,filesInfo);
	}
	
	@RequestMapping(value = "getFileList", method = RequestMethod.GET)
	public ModelAndView getFileListGet(
			String prefix
			){
		ModelAndView mav = new ModelAndView();
		Map<String, Object> fileMap = fileService.getFileList(prefix);
		//获得文件夹和文件名
		mav.addObject("folderList",fileMap.get("folderList"));
		mav.addObject("fileList",fileMap.get("fileList"));
		//当前文件夹
		mav.addObject("currentFolder", prefix);
		mav.setViewName("filelist");
		return mav;
	}
	
	@RequestMapping(value = "getFileList", method = RequestMethod.POST)
	public @ResponseBody String  getFileListPost(String prefix){
		Map<String, Object> fileMap = new HashMap<String,Object>();
		fileMap = fileService.getFileList(prefix);
		String json = JSON.toJSONString(fileMap);
		return json;
	}
	/**
	 * 获得文件信息
	 * @param prefix
	 * @param fileName
	 * @return
	 */
	@RequestMapping(value = "getFileInfo", method = RequestMethod.POST)
	public @ResponseBody String  getFileInfo(String key){
		String json = JSON.toJSONString(fileService.getFileInfo(key));
		return json;
	}
	/**
	 * 文件下载
	 * @param prefix
	 * @param fileName
	 * @return
	 */
	@RequestMapping(value = "download", method = RequestMethod.POST)
	public @ResponseBody String download(String prefix,String fileName){
		return fileService.download(prefix, fileName);
	}
	/**
	 * 文件删除
	 * @param prefix
	 * @param fileName
	 * @return
	 */
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public @ResponseBody String delete(String key){
		return fileService.delete(key);
	}
}
