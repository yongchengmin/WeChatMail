package com.yc.weChat;

import java.util.ArrayList;
import java.util.List;

import com.yc.utils.url.UrlReqUtil;
import com.yc.utils.wechar.Template;
import com.yc.utils.wechar.TemplateParam;
public class TemplateMsg {
	public static String BASE_DIR = "d:/wechat-openid/";//存放配置文件的根目录
	public static String URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
	public static String templateId = "09-mNpH07Up_D4Ycrms8uHI98TEoASuxh76pPW6vhRM";
	public static String topColor = "#00DD00";
	public static String pathname = "openid-used.txt";
	public static String ledname = "led-wechat.txt";
	
	public static String sendTemplateMsg(String requestUrl,String token,String vin,String status,String remark
			,List<String> list,String openid){
		//用户在该公众号下的openid
		//3 调用模板消息的发送方法
		Template tem=new Template();
		tem.setTemplateId(templateId);//为模板id 
		tem.setTopColor(topColor);
		tem.setToUser(openid);
		tem.setUrl("");
				
		List<TemplateParam> paras=new ArrayList<TemplateParam>();
		paras.add(new TemplateParam("orderNumber",":"+vin,"#0044BB"));
		paras.add(new TemplateParam("status",status,"#0044BB"));
		paras.add(new TemplateParam("remark",remark,"#AAAAAA"));
		tem.setTemplateParamList(paras);
		//{"errcode":0,"errmsg":"ok","msgid":108925056251363330}
		String result = UrlReqUtil.post(requestUrl, tem.toJSON());
		return result;
		//https://mp.weixin.qq.com/advanced/tmplmsg?action=faq&token=16798010&lang=zh_CN
	}
}
