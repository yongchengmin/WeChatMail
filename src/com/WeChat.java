package com;

import java.io.IOException;

import com.yc.WeChartGlobal;
import com.yc.WeChatPanel;
import com.yc.utils.esbUtils.FileUtil;

public class WeChat {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		init();
		WeChatPanel.sendChat();
	}

	private static void init(){
		FileUtil.mkdir(WeChartGlobal.ledname);
		FileUtil.mkdir(WeChartGlobal.mailPath);
	}
}
