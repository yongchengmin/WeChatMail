package com.yc.weChat;

import java.util.ArrayList;
import java.util.List;

import com.yc.WeChartGlobal;
import com.yc.utils.url.UrlReqUtil;
import com.yc.utils.wechar.Template;
import com.yc.utils.wechar.TemplateParam;
public class TemplateMsg {
	public static String sendTemplateMsg(String requestUrl,String token,String vin,String status,String remark
			,String openid,String url){
		//用户在该公众号下的openid
		//3 调用模板消息的发送方法
		Template tem=new Template();
		tem.setTemplateId(WeChartGlobal.templateId);//为模板id 
		tem.setTopColor(WeChartGlobal.topColor);
		tem.setToUser(openid);
		tem.setUrl(url);//根据不用的底盘号,传递不同的URL,前端人员完成
		//"https://mp.weixin.qq.com/"
				
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
