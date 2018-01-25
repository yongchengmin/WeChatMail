package com.yc.weChat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import com.yc.utils.esbUtils.DateUtil;
import com.yc.utils.esbUtils.FileUtil;
import com.yc.utils.files.PropertiesUtil;
import com.yc.utils.url.UrlReqUtil;

public class WeixinAccessTokenMsg{
	public static String TOKENMAP = "tokenMap";
	public static String URL = "https://api.weixin.qq.com/cgi-bin/token";
	public static String APP_ID = "wx6b8ffba0cb003c8e";
	public static String APP_SECRET = "00138927f11e3bdd5bb5953ece2e5014";
	/**
	 *验证是否过期,获取 ACCESS_TOKEN
	 */
	public static String getWeCharAccessToken(){
		InputStream ips = weCharInputStream();
//		InputStream ips = WeixinAccessTokenMsg.class.getResourceAsStream(PropertiesUtil.PROPERTIES_ACCESS_TOKEN);
		String accessToken = "";
		Long expirationTime = PropertiesUtil.getLongAccessTokenKey(ips);
		if(System.currentTimeMillis()<expirationTime){
			accessToken = PropertiesUtil.getStringAccessTokenKey(ips);
		}else{
			accessToken = getToken(ips);
		}
		return accessToken;
	}
	/**
	 * 获取文件的InputStream
	 */
	private static InputStream weCharInputStream(){
		FileUtil.mkdir(TemplateMsg.BASE_DIR);
    	File f= new File(TemplateMsg.BASE_DIR+PropertiesUtil.PROPERTIES_ACCESS_TOKEN);
    	if(!f.exists()){
    		List<String> list = new ArrayList<String>();
    		list.add(PropertiesUtil.ACCESS_TOKEN+"=init");
    		list.add(PropertiesUtil.EXPIRATION_TIME+"=7200");
    		try {
				FileUtil.listToFile(list, TemplateMsg.BASE_DIR+PropertiesUtil.PROPERTIES_ACCESS_TOKEN);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
    	}
    	InputStream ips = null;
    	try {
			ips = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ips;
	}
	/**
	 *不验证是否过期,直接获取 ACCESS_TOKEN
	 */
	public static String weCharAccessToken(){
		InputStream ips = weCharInputStream();
		String accessToken = getToken(ips);
		return accessToken;
	}
	/**
	 * 获取 ACCESS_TOKEN 并重写文件
	 */
	private static String getToken(InputStream ips){
		String accessToken = null;
		String returnData = null;
		try {
			returnData=UrlReqUtil.post(URL, "grant_type=client_credential&appid="+APP_ID+"&secret="+APP_SECRET);
		} catch (Exception e) {
			returnData = null;
		}
		if(returnData==null){
			return null;
		}
		JSONObject json=JSONObject.fromObject(returnData);
		if(json.containsKey(PropertiesUtil.ACCESS_TOKEN)){
			if(json.get(PropertiesUtil.ACCESS_TOKEN)!=null&&!json.get(PropertiesUtil.ACCESS_TOKEN).equals("")){
				accessToken = json.get(PropertiesUtil.ACCESS_TOKEN).toString();
				//同时更新文件
				URL fileUrl = null;//WeixinAccessTokenMsg.class.getResource(PropertiesUtil.PROPERTIES_ACCESS_TOKEN);//得到文件路径
				try {//file:/d:/wechar-openid/accessToken.properties
					fileUrl = new URL("file:/"+TemplateMsg.BASE_DIR+PropertiesUtil.PROPERTIES_ACCESS_TOKEN);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				Long expires_in = DateUtil.addSecond(Integer.parseInt(json.get(PropertiesUtil.EXPIRATION_TIME).toString()));
				PropertiesUtil.saveKey(PropertiesUtil.PROPERTIES_ACCESS_TOKEN, PropertiesUtil.ACCESS_TOKEN,
						accessToken,fileUrl,ips);
				PropertiesUtil.saveKey(PropertiesUtil.PROPERTIES_ACCESS_TOKEN, PropertiesUtil.EXPIRATION_TIME, 
						expires_in+"",fileUrl,ips);
			}
		}
		return accessToken;
	}

}
