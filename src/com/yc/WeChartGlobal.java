package com.yc;

public interface WeChartGlobal {
	static final String PORT = "port";
	
	static final String URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
	static final String templateId = "09-mNpH07Up_D4Ycrms8uHI98TEoASuxh76pPW6vhRM";
	static final String topColor = "#00DD00";
	static final String ledname = "led-wechat.txt";
	static final String PORTMESG = "portMesg.properties";//配置信息
	
	static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	static final String dmy_hms = "yyyy-MM-dd HH:mm:ss";
	static final String charsetName = "UTF-8";
	
	static final String TOKENMAP = "tokenMap";
	static final String URL_TOKEN = "https://api.weixin.qq.com/cgi-bin/token";
	static final String APP_ID = "wx6b8ffba0cb003c8e";
	static final String APP_SECRET = "00138927f11e3bdd5bb5953ece2e5014";
	
	static final String OPENIDS = "openids";//存放总个数
	static final String OPENID = "openid";//存放对应的ID
	
	static final String ERRCODE = "errcode";//错误编码
	static final String ERRMSG = "errmsg";//错误描述
}
