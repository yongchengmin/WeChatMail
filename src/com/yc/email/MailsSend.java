package com.yc.email;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import com.yc.WeChartGlobal;
import com.yc.utils.IpUtils;
import com.yc.utils.esbUtils.DateUtil;
import com.yc.utils.esbUtils.FileUtil;
import com.yc.utils.files.PropertiesUtil;

public class MailsSend {
	public static String comma = ",";
	public static String enter = "\r\n";
	
	private static String subject = "";
	private static String encodeing = "UTF-8";
	public static String spilt_ = "_";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*String[] cloumns = new String[]{
				"时间","VIN","节点"
		};
		String createDate = null,vin = null,notes = null,url = null;
		String d = null;
		String[] ss = null;
		List<String[]> values = new ArrayList<String[]>();
		File file =new File(WeChartGlobal.mailPath);
		File[] emailFiles = file.listFiles();
		if (emailFiles != null) {
			for (File sourceFile : emailFiles) {
				if(sourceFile.isFile()){
					d = FileUtil.readStrTxt(sourceFile,encodeing);
					System.out.println(d);
					ss = d.split(com.yc.utils.esbUtils.StringUtils.spilt1);
					if(ss.length>0){
						vin = ss[0];
						if(ss.length>=2){
							notes = ss[1];
							if(ss.length>=3){
								url = ss[2];
							}
						}
						values.add(new String[]{
								createDate,vin,notes	
						});
					}
//					createDate = StringUtils.substringAfter(sourceFile.getName(), com.yc.utils.esbUtils.StringUtils.spilt1);//第一个#开始截取(从左起)
					createDate = StringUtils.substringBetween(sourceFile.getName(), spilt_, ".");//截取第一个_与第一个.之间的(从左起)
					System.out.println(createDate+","+vin+","+notes+","+url);
					//....
					sourceFile.delete();
				}
				
			}
		}*/
		File file =new File(WeChartGlobal.mailPath);
		File[] emailFiles = file.listFiles();
		sendMails(emailFiles);
	}
	
	public static void sendMails(File[] emailFiles){
		String[] cloumns = new String[]{
				"编号","VIN","节点","详情"
		};
		String createDate = null,vin = null,notes = null,url = null;
		String d = null;
		String[] ss = null;
		List<String[]> values = new ArrayList<String[]>();
		if (emailFiles != null) {
			for (File sourceFile : emailFiles) {
				if(sourceFile.isFile()){
					vin = "";
					notes = "";
					url = "";
					d = FileUtil.readStrTxt(sourceFile,encodeing);
					ss = d.split(com.yc.utils.esbUtils.StringUtils.spilt1);
					if(ss.length>0){
						vin = ss[0];
						if(ss.length>=2){
							notes = ss[1];
							if(ss.length>=3){
								url = ss[2];
							}
						}
					}
					createDate = StringUtils.substringBetween(sourceFile.getName(), spilt_, ".");//截取第一个_与第一个.之间的(从左起)
					values.add(new String[]{
							createDate,vin,notes,url	
					});
					sourceFile.delete();
				}
			}
			//test
//			String htmlmail = getHtmlNotesMsg(createDate+"-"+IpUtils.localIp(),cloumns,values);
//			System.out.println(htmlmail);
			try {
				sendMails(DateUtil.format(new Date(), WeChartGlobal.ymdHms), cloumns, values);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (EmailException e) {
				e.printStackTrace();
			}
		}

	}

	private static void sendMails(String createDate,String[] cloumns,List<String[]> values) 
			throws EmailException, MalformedURLException{
		String[] emails = loadMailNames();
//		String address = "yongcheng_min@163.com,309831731@qq.com";//sunxiaojing0628@126.com
//		String emails[] = address.split(",");
		String host = PropertiesUtil.getPropertiesKey(WeChartGlobal.PORTMESG, WeChartGlobal.HOST);
		String userName = PropertiesUtil.getPropertiesKey(WeChartGlobal.PORTMESG, WeChartGlobal.USER_NAME);
		String password = PropertiesUtil.getPropertiesKey(WeChartGlobal.PORTMESG, WeChartGlobal.PASS_WORD);
		String from = PropertiesUtil.getPropertiesKey(WeChartGlobal.PORTMESG, WeChartGlobal.FROM);
		subject = PropertiesUtil.getPropertiesKey(WeChartGlobal.PORTMESG, WeChartGlobal.SUBJECT)+"("+createDate+")";
		String head = PropertiesUtil.getPropertiesKey(WeChartGlobal.PORTMESG, WeChartGlobal.HEAD);
		for(int i=0;i<emails.length;i++){
			if(emails[i].contains("@")){
				HtmlEmail email = new HtmlEmail();
				email.setHostName(host);
				email.addTo(emails[i]);
				email.setFrom(from);
				email.setCharset(encodeing);
				email.setAuthentication(userName, password);
				email.setSubject(subject);
				email.setHtmlMsg(getHtmlNotesMsg(head,cloumns,values)+enter+"------------"+enter
						+IpUtils.localIp());
				email.setTextMsg("Your email client does not support HTML messages");
				email.send();
			}
		}
	}
	
	private static String[] loadMailNames(){
		List<String> userNames = new ArrayList<String>();
		String noteMails = PropertiesUtil.getPropertiesKey(WeChartGlobal.PORTMESG, WeChartGlobal.NOTE_MAILS);
		String[] mailstrs = noteMails.split(comma);
		for(String mail : mailstrs) {
			userNames.add(mail);
		}
		
		String[] emails = new String[userNames.size()];
		int k = 0;
		for(String o : userNames){
			emails[k] =o;
			k++;
		}
		return emails;
	}
	
	//头,明细(3列)
	private static String getHtmlNotesMsg(String head,String[] cloumns,List<String[]> values){
		String bgcolor = "bgcolor='#00FF00'";
		StringBuffer str = new StringBuffer();
		str.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
		str.append("<TABLE border=1 cellspacing=0 style='border:1px solid blue'>");
		
		str.append("<TR>");
		str.append("<TD colspan="+cloumns.length+" style='text-align:center'>"+head+"");
		str.append("</TD>");
		str.append("</TR>");
		
		String style = "style='word-break:break-all'";
		str.append("<TR>");
//			str.append("<TD style='border:1px solid black'>"+cloumns[0]);
		str.append("<TD "+style+" "+bgcolor+">"+cloumns[0]);
		str.append("</TD>");
		str.append("<TD "+style+" "+bgcolor+">"+cloumns[1]);
		str.append("</TD>");
		str.append("<TD "+style+" "+bgcolor+">"+cloumns[2]);
		str.append("</TD>");
		str.append("<TD "+style+" "+bgcolor+">"+cloumns[3]);
		str.append("</TD>");
		str.append("</TR>");
		for(int i = 0 ; i<values.size() ; i++){
			String[] v = values.get(i);
			str.append("<TR>");
			str.append("<TD "+style+">"+v[0]+"");
			str.append("</TD>");
			str.append("<TD "+style+">"+v[1]+"");
			str.append("</TD>");
			str.append("<TD "+style+">"+v[2]+"");
			str.append("</TD>");
			str.append("<TD "+style+"><a href='"+v[3]+"'>详情</a>");
			str.append("</TD>");
			str.append("</TR>");
		}
		str.append("</TABLE>");
		return str.toString();
	}
}
