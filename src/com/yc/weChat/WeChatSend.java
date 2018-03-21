package com.yc.weChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yc.WeChartGlobal;
import com.yc.WeChatPanel;
import com.yc.email.MailsSend;
import com.yc.utils.esbUtils.FileUtil;
import com.yc.utils.esbUtils.JsonTools;
import com.yc.utils.esbUtils.StringUtils;
import com.yc.utils.files.PropertiesUtil;

public class WeChatSend {
	public static void createHandlerThread(Socket socket){
		WeChatSend mas = WeChatSend.getInstance();
		Handler handler = mas.new Handler(socket);
		Thread t = new Thread(handler);
		t.start();
	}
	private static WeChatSend single=null;
	public static WeChatSend getInstance(){
		if(single==null){
			single = new WeChatSend();
		}
		return single;
	}
	public class Handler implements Runnable{
		private Socket socket = null;
		public Handler(Socket socket){
			this.socket = socket;
		}
		public void run() {
			BufferedReader in = null;
			PrintWriter out = null;
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream(), WeChartGlobal.charsetName));
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), WeChartGlobal.charsetName));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String getLine = null;
			try {
				getLine = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(getLine!=null){
//				System.out.println(getLine);
				
//				FileUtil.mkdir(WeChartGlobal.BASE_DIR);
				FileUtil.createTxt(WeChartGlobal.ledname, getLine, WeChartGlobal.charsetName);//WeChartGlobal.BASE_DIR+
				
				String createDate = new SimpleDateFormat("yyMMddHHmmssSSS").format(new Date());
				FileUtil.createTxt(WeChartGlobal.mailPath+"/led-wechat"+MailsSend.spilt_+createDate+".txt", getLine, WeChartGlobal.charsetName);
				
				getTemplateMsg(getLine);
				
				fieldSet(Boolean.TRUE);
				
			}
			out.write("ok\n");
			out.flush();
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e) {  
				e.printStackTrace();
			}
		}
		
	}
	public static void fieldSet(Boolean isSend){
		try {
			Object o=WeChatPanel.class.newInstance();//获取对象
			Field f=WeChatPanel.class.getField("send");//根据key获取参数
			f.set(o, isSend);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	public static void getTemplateMsg(String mesg) {
		String[] ss = mesg.split(StringUtils.spilt1);
		if(ss.length>0){
			if(ss.length==2){
				sendTemplateMsg(ss[0], ss[1],"");
			}else if(ss.length==3){
				sendTemplateMsg(ss[0], ss[1], ss[2]);
			}
		}
	}
	//url:报表...
	private static void sendTemplateMsg(String vin,String status,String detailUrl) {
		// TODO Auto-generated method stub
//		String type = "";
		//业务参数
		String remark = PropertiesUtil.getPropertiesKey(WeChartGlobal.PORTMESG, WeChartGlobal.REMARK);
		String nums = PropertiesUtil.getPropertiesKey(WeChartGlobal.PORTMESG, WeChartGlobal.OPENIDS);
		int size = 0;
		try {
			size = Integer.valueOf(nums);
		} catch (Exception e) {
			size = 0;
		}
		if(size>0){
			String token = WeixinAccessTokenMsg.getWeCharAccessToken();
			if(token!=null){
				String url = PropertiesUtil.getPropertiesKey(WeChartGlobal.PORTMESG, WeChartGlobal.URL);
				String requestUrl =url.replace(WeChartGlobal.ACCESS_TOKEN,token);
				for(int i = 1 ; i<=size; i++){
					String openid = PropertiesUtil.getPropertiesKey(WeChartGlobal.PORTMESG, WeChartGlobal.OPENID+i);
					if(openid!=null){
						String result = TemplateMsg.sendTemplateMsg(requestUrl, token, vin, status, remark, openid,detailUrl);
						//{"":40001,"errmsg":"invalid credential, access_token is invalid or not latest hint: [CYHbfa0259vr44!]"}
						String errcode = JsonTools.getJsonKey(result, "errcode");
						if(errcode!=null && "40001".equals(errcode)){//要重新获取 access_token
							token = WeixinAccessTokenMsg.weCharAccessToken();
							requestUrl =url.replace(WeChartGlobal.ACCESS_TOKEN,token);
							result = TemplateMsg.sendTemplateMsg(requestUrl, token, vin, status, remark, openid,detailUrl);
							errcode = JsonTools.getJsonKey(result, "errcode");
						}
						if(errcode!=null && "0".equals(errcode)){
//							type = "NOTES";
						}else{
//							type = "ERROR";
						}
					}
				}
			}
		}
	}

}
