package com.qcj.myzone.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.qcj.myzone.model.FilesInfo;
import com.qcj.myzone.repository.FileInfoRepository;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.BucketManager.FileListIterator;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;

@Service
public class FileService {
	
	@Resource
	private FileInfoRepository fileInfoRepository;
	

	/**
	 * 获得BucketManager
	 * @return
	 */
	public BucketManager getBucketManager(){
		//构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone2());
		String accessKey = "khCLjbIJ-htjneC2BUtX8zOBSk71wpm1TZnU9s5u";
		String secretKey = "PG0aUetavLZQvD6pp4hvgqDV_5P9_2X5xG5kfGKk";
		return new BucketManager(Auth.create(accessKey, secretKey), cfg);
	}
	/**
	 * 上传文件
	 * @param file
	 * @param filesInfo
	 * @return 
	 * @return
	 */
	public String doUpload(MultipartFile file,FilesInfo filesInfo){
		
		String msg = "上传成功";
		
		if(file.isEmpty()){
			msg = "请选择上传文件";
		}else if(filesInfo.getFileName() == null || filesInfo.getFileName().equals("")){
			msg = "请输入文件名";
		}else{
			
			 //获得文件后缀
	        String suffixName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
	        //文件名和路径
	        String fileName = filesInfo.getFileName();
	        String filePath = "";
			if(filesInfo.getFilePath() != null && !filesInfo.getFilePath().equals("")){
		       filePath = filesInfo.getFilePath()+"/";
			}
			
			byte[] uploadBytes = null;
			try {
				uploadBytes = file.getBytes();
			} catch (IOException e) {
				msg = "文件转字节流出错";
				e.printStackTrace();
			}
	        ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);
	        
	       
	        
			//构造一个带指定Zone对象的配置类
			Configuration cfg = new Configuration(Zone.zone2());
			//...其他参数参考类注释
			UploadManager uploadManager = new UploadManager(cfg);
			//...生成上传凭证，然后准备上传
			String accessKey = "khCLjbIJ-htjneC2BUtX8zOBSk71wpm1TZnU9s5u";
			String secretKey = "PG0aUetavLZQvD6pp4hvgqDV_5P9_2X5xG5kfGKk";
			String bucket = "test";
			//默认不指定key的情况下，以文件内容的hash值作为文件名
			Auth auth = Auth.create(accessKey, secretKey);
			String upToken = auth.uploadToken(bucket);
			try {
			    Response response = uploadManager.put(byteInputStream,filePath+fileName+suffixName, upToken,null,null);
			    //解析上传成功的结果
			    DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
			    System.out.println(putRet.key);
			    System.out.println(putRet.hash);
			} catch (QiniuException ex) {
			    Response r = ex.response;
			    System.err.println(r.toString());
			    msg = "上传出错:"+r.toString();
			    try {
			        System.err.println(r.bodyString());
			    } catch (QiniuException ex2) {
			        //ignore
			    }
			}
			
			if(msg.equals("上传成功")){
				filesInfo.setUrl("http://optpqehds.bkt.clouddn.com/"+filePath+fileName+suffixName);
				fileInfoRepository.save(filesInfo);
				msg += ",URL为:"+filesInfo.getUrl();
			}
		}
		return msg;
	}
	/**
	 * 查询目录
	 * @param prefix 文件名前缀
	 * @param delimiter 指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
	 * @return
	 */
	public Map<String, Object> getFileList(String prefix){
		
		if(prefix == null || prefix == "")
			prefix = "";
		else
			prefix += "/";
		
		String delimiter = "";
		
		//储存空间
		String bucket = "test";
		//每次迭代的长度限制，最大1000，推荐值 1000
		int limit = 1000;
		//列举空间文件列表
		BucketManager.FileListIterator fileListIterator = getBucketManager().createFileListIterator(bucket, prefix, limit, delimiter);
		FileInfo[] items = null;
		//文件夹和文件
		ArrayList<String> folderList = new ArrayList<>();
		ArrayList<Map> fileList = new ArrayList<>();
		
		while(fileListIterator.hasNext()){
			items = fileListIterator.next();
			 for (FileInfo item : items) {
				Map<String,Object> map = new HashMap<String, Object>();
				String fileName = item.key;
				String lastStr = fileName.substring(prefix.length(),fileName.length());
				//如果没有'/'则为文件
				if(lastStr.indexOf("/") == -1){
			    	map.put("key", lastStr);
			    	map.put("hash", item.hash);
			    	map.put("fsize", item.fsize);
			    	map.put("mimeType", item.mimeType);
			    	map.put("putTime", item.putTime);
			    	map.put("endUser", item.endUser);
			        
			    	//时间处理
			    	String date = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(item.putTime / 10000));
			        map.put("putTime", date);
			        fileList.add(map);
				}
				//否则为文件夹
				else{
					String folderName = lastStr.substring(0,lastStr.indexOf("/"));
					//合并重复文件夹
					if(!folderList.contains(folderName))
						folderList.add(folderName);
				}
			 }
		}
		
		Map<String,Object> fileMap = new HashMap<>();
		fileMap.put("folderList",folderList);
		fileMap.put("fileList", fileList);
		return fileMap;
	}
	/**
	 * 查询文件详细信息
	 * @param prefix
	 * @param fileName
	 * @return
	 */
	public Map<String, String> getFileInfo(String key){
		
		//储存空间
		String bucket = "test";
		System.out.println(key);
		BucketManager.FileListIterator fileListIterator = getBucketManager().createFileListIterator(bucket, key, 1, "");
		FileInfo[] items = fileListIterator.next();
		FileInfo item = items[0];
		
		Map<String,String> map = new HashMap<String, String>();
    	map.put("key", key);
    	map.put("hash", item.hash);
    	//保留两位小数
    	map.put("fsize", new BigDecimal(Double.valueOf(item.fsize)/1024).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()+"KB");
    	map.put("mimeType", item.mimeType);
    	map.put("endUser", item.endUser);
        
    	//时间处理
    	String date = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(item.putTime / 10000));
        map.put("putTime", date);
        
		return map;
	}
	/**
	 * 获取文件下载地址
	 * @param prefix
	 * @param fileName
	 * @return
	 */
	public String download(String prefix,String fileName){

		
		if(prefix != "")
			prefix += "/";
		
		String domainOfBucket = "http://optpqehds.bkt.clouddn.com";
		String encodedFileName= "";
		try {
			encodedFileName = URLEncoder.encode(prefix+fileName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String finalUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
		System.out.println(finalUrl);
		System.out.println(prefix+fileName);
		return finalUrl+"?attname=";
	}
	public String delete(String key){
		String msg = "删除成功";
		String bucket = "test";
		
		try {
			getBucketManager().delete(bucket, key);
		} catch (QiniuException ex) {
		    //如果遇到异常，说明删除失败
			msg = "删除失败:"+ex.code()+","+ex.response.toString();
		}
		
		return msg;
	}
	
}
