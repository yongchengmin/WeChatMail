package com.yc.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author :      yc.min              
 */
public class IpUtils {
	/**127.0.0.1*/
	public static String LOCAL_IP = "127.0.0.1";
	
	public static String localIp(){
		//服务的地址
		InetAddress address;
		String localIp = "";
		try {
			address = InetAddress.getLocalHost();
			localIp = address.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return localIp;
	}
	public static Boolean iPTest(String ip) throws UnknownHostException{
		boolean state = true;
		try {  
           InetAddress ad = InetAddress.getByName(ip);  
           state = ad.isReachable(60000);//测试是否可以达到该地址  
           if(!state) {
        	   System.err.println("连接失败:"+ ad.getHostAddress());  
           }
        }catch (IOException e) {
			e.printStackTrace();
		}
		return state;
		/*while(true){
			try {  
	           InetAddress ad = InetAddress.getByName("192.168.10.218");  
	           boolean state = ad.isReachable(60000);//测试是否可以达到该地址  
	           if(state) {
	        	   System.out.println("连接成功" + ad.getHostAddress());  
	           } else{
	        	   System.err.println("连接失败");  
	           }  
	        }catch (IOException e) {
				e.printStackTrace();
			} 
			try {
				Thread.sleep(60000);//9min=540000,1min=60000
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}*/
	}  
	public static void main(String[] args) {
		try {
			IpUtils.iPTest("192.168.10.92");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}

