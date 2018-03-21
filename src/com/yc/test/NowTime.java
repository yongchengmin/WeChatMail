package com.yc.test;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

public class NowTime extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//添加显示时间的JLabel
	public NowTime(){ 
		JLabel time = new JLabel();
		add(time);
		this.setTimer(time);
	}
	
	//设置Timer 1000ms实现一次动作 实际是一个线程 
	private void setTimer(JLabel time){
		final JLabel varTime = time;
		Timer timeAction = new Timer(1000, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				long timemillis = System.currentTimeMillis();
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				varTime.setText(df.format(new Date(timemillis)));
			}
		});
		timeAction.start();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NowTime timeFrame = new NowTime();
		timeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		timeFrame.setSize(160, 80);
		//居中显示窗体
		timeFrame.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - timeFrame.getWidth()) / 2,
				(int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - timeFrame.getHeight()) / 2);
		timeFrame.setVisible(true);
	}

}
