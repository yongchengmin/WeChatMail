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

import com.yc.WeChartGlobal;
import com.yc.utils.esbUtils.DateUtil;
import com.yc.utils.esbUtils.FileUtil;
import com.yc.utils.files.PropertiesUtil;
import com.yc.utils.url.UrlReqUtil;

public class WeixinAccessTokenMsg{
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
//		FileUtil.mkdir(WeChartGlobal.BASE_DIR);
    	File f= new File(PropertiesUtil.PROPERTIES_ACCESS_TOKEN);//WeChartGlobal.BASE_DIR+
    	if(!f.exists()){
    		List<String> list = new ArrayList<String>();
    		list.add(PropertiesUtil.ACCESS_TOKEN+"=init");
    		list.add(PropertiesUtil.EXPIRATION_TIME+"=7200");
    		try {
				FileUtil.listToFile(list, PropertiesUtil.PROPERTIES_ACCESS_TOKEN);//WeChartGlobal.BASE_DIR+
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
		String app_secret = PropertiesUtil.getPropertiesKey(WeChartGlobal.PORTMESG, WeChartGlobal.APP_SECRET);
		//测试IP是否可以获取token
		//https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx6b8ffba0cb003c8e&secret=00138927f11e3bdd5bb5953ece2e5014
		//如果失败提示报文:{"errcode":40164,"errmsg":"invalid ip 117.71.48.31, not in whitelist hint: [jq01132976]"}
		returnData=UrlReqUtil.post(WeChartGlobal.URL_TOKEN, "grant_type=client_credential&appid="+WeChartGlobal.APP_ID+"&secret="+app_secret);
		if(returnData==null){
			return null;
		}
		JSONObject json=JSONObject.fromObject(returnData);
		if(json.containsKey(WeChartGlobal.ERRCODE)){
			String errcode = json.getString(WeChartGlobal.ERRCODE);
			if(errcode!=null){
				accessToken = json.getString(WeChartGlobal.ERRMSG);
				FileUtil.createTxt(WeChartGlobal.ledname, accessToken, WeChartGlobal.charsetName);//WeChartGlobal.BASE_DIR+
				accessToken = null;
			}
		}
		else{
			if(json.containsKey(PropertiesUtil.ACCESS_TOKEN)){
				if(json.get(PropertiesUtil.ACCESS_TOKEN)!=null&&!json.get(PropertiesUtil.ACCESS_TOKEN).equals("")){
					accessToken = json.get(PropertiesUtil.ACCESS_TOKEN).toString();
					//同时更新文件
					URL fileUrl = null;//WeixinAccessTokenMsg.class.getResource(PropertiesUtil.PROPERTIES_ACCESS_TOKEN);//得到文件路径
					try {//file:/d:/wechar-openid/accessToken.properties
						fileUrl = new URL("file:/"+PropertiesUtil.PROPERTIES_ACCESS_TOKEN);//WeChartGlobal.BASE_DIR+
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
		}
		return accessToken;
	}

}
