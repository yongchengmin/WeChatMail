package com.yc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import com.yc.email.MailsSend;
import com.yc.utils.esbUtils.DateUtil;
import com.yc.utils.esbUtils.FileUtil;
import com.yc.utils.esbUtils.StringUtils;
import com.yc.utils.files.PropertiesUtil;
import com.yc.weChat.WeChatSend;

public class WeChatPanel {
	public static String dmy_hms = "yy-MM-dd HH:mm:ss";
	static JScrollPane jsp;
	static JTextArea jta;
	static JPanel panelVinNorth;
	static JPanel panelVin;
	
	static JScrollPane jspMail;
	static JTextArea jtaMail;
	static JPanel panelMailNotrh;
	static JPanel panelMail;
	
	static int i = 1,delay = 500,countMails = 0,j = 1;
	static String temp1;
	static String temp2;
	protected final static String file_no = "File not generated";
	protected final static String CHARACTER = "utf-8";
	public static boolean send = false;

	final static int width = 1100;
	final static int height = 620;
	final static JFrame frame = new JFrame();
	
	static String textMail = "<html> Note Mail Message<br/></html>";
    static JLabel lb1 = new JLabel(textMail,JLabel.CENTER);
    static JLabel timeMail = new JLabel("刷新",JLabel.RIGHT);
    
    static String showLine = "";
    static int lines = 0;
	static{
		showLine = PropertiesUtil.getPropertiesKey(WeChartGlobal.PORTMESG, WeChartGlobal.SHOW_LINE);
		lines = org.apache.commons.lang.StringUtils.isEmpty(showLine)?8:Integer.valueOf(showLine);
	}
	public static void sendChat() throws IOException{
		final String port = PropertiesUtil.getPropertiesKey(WeChartGlobal.PORTMESG, WeChartGlobal.PORT);
		ServerSocket server = new ServerSocket(Integer.valueOf(port));
		Socket socket = null;
		boolean flag = true;
		
		String delaySend = PropertiesUtil.getPropertiesKey(WeChartGlobal.PORTMESG, WeChartGlobal.DELAY_SEND);
		final int delayMail = delaySend==null?300000:Integer.valueOf(delaySend);//默认5分钟
		
        EventQueue.invokeLater(new Runnable(){
            @Override 
            public void run(){
              //add 20180316
            	frame.setTitle("WeChat SEND "+port+" "+lines+"行/条");
                frame.setLayout(new GridLayout(0,2));
                jtaMail = new JTextArea();
                jtaMail.setCaretPosition(jtaMail.getDocument().getLength());
                jtaMail.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,24));
                jtaMail.setEditable(false);
                jspMail = new JScrollPane(jtaMail);
                
                lb1.setForeground(Color.red);
                panelMail = new JPanel();
                panelMail.setLayout(new BorderLayout());
                

                panelMailNotrh = new JPanel();
                panelMailNotrh.add(lb1);
                panelMailNotrh.add(timeMail);
                panelMail.add(panelMailNotrh,BorderLayout.NORTH);
                panelMail.add(jspMail);
                
                frame.add(panelMail);
                //^^^^^^^^^^^^^^^^^
                jta = new JTextArea();
                jta.setCaretPosition(jta.getDocument().getLength());
                jta.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,24));
                jta.setEditable(false);
                jsp = new JScrollPane(jta);
                
                String textVin = "<html> VIN Send Message<br/></html>";
                final JLabel lb2 = new JLabel(textVin,JLabel.CENTER);
                lb2.setForeground(Color.red);
                panelVin = new JPanel();
                panelVin.setLayout(new BorderLayout());
                panelVinNorth = new JPanel();
                panelVinNorth.add(lb2);
                panelVin.add(panelVinNorth,BorderLayout.NORTH);
                
                panelVin.add(jsp);
                frame.add(panelVin);
                Timer timer = new Timer(delay,new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
//                    	System.out.println(DateUtil.format(new Date(), "yyMMddHHmmssSSS"));
                    	if(send){
                        	if(i>lines){
                        		frame.remove(panelVin);
                        		
                        		jta = null;jsp = null;panelVin = null;
                        		jta = new JTextArea();
                                jta.setCaretPosition(jta.getDocument().getLength());
                                jta.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,24));
                                jta.setEditable(false);
                                jsp = new JScrollPane(jta);
                        		
                                panelVin = new JPanel();
                                panelVin.setLayout(new BorderLayout());
                                panelVin.add(panelVinNorth,BorderLayout.NORTH);
                                
                                panelVin.add(jsp);
                                frame.add(panelVin);
                        		frame.validate();
                        		frame.repaint();
                        		i = 1;
                        	}
                    		temp1 =  readUsers();
                        	temp2 = "("+DateUtil.format(new Date(), dmy_hms)+")";
                            jta.append(i+"."+temp1.trim()+temp2+"\n");
                            jta.append(StringUtils.getStr(width, "-")+"\n");
                        	i++;
                        	send = false;
                        	sendMails(lines);
                    	}
                    	//间隔5分钟自动发一次邮件...
                    	countMails += delay;
//                    	long timemillis = System.currentTimeMillis();
//        				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    	timeMail.setText("邮件倒计时:"+(delayMail-countMails));//df.format(new Date(timemillis))
                    	if(countMails >= delayMail){
                    		sendMails(1);
                    		countMails = 0;
//                        	System.out.println("countMails == "+countMails+"...."+DateUtil.format(new Date(), "HH:mm:ss"));
                    	}
                    }
                });
                timer.start();
                
                URL url = WeChatPanel.class.getClassLoader().getResource("timg.jpg");
//                System.out.println(url.getPath());///D:/jac_gitee_v001/WeChatMail/bin/timg.jpg
                ImageIcon icon=new ImageIcon(url);
                frame.setIconImage(icon.getImage());  
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setPreferredSize(new Dimension(width,height));
                frame.pack();
                frame.setVisible(true);
            }
        });
        try {
			while(flag){
				socket = server.accept();
				WeChatSend.createHandlerThread(socket);
			}
		} finally{
			server.close();
		}
    }
	private static String readUsers(){
		File file = new  File(WeChartGlobal.ledname);
        if(!file.exists()){
        	return file_no;
        }
        String d = FileUtil.readStrTxt(file,CHARACTER);
		return d;
	}
	private static void sendMails(int line){
		File file =new File(WeChartGlobal.mailPath);
		File[] emailFiles = file.listFiles();
		if (emailFiles != null && emailFiles.length >= line) {
			if(j>lines){
				frame.remove(panelMail);
        		
        		jtaMail = null;jspMail = null;panelMail = null;
        		jtaMail = new JTextArea();
                jtaMail.setCaretPosition(jtaMail.getDocument().getLength());
                jtaMail.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,24));
                jtaMail.setEditable(false);
                jspMail = new JScrollPane(jtaMail);
        		
                panelMail = new JPanel();
                panelMail.setLayout(new BorderLayout());
                panelMail.add(panelMailNotrh,BorderLayout.NORTH);
                panelMail.add(jspMail);
                frame.add(panelMail);
        		frame.validate();
        		frame.repaint();
        		j = 1;
            }
			
			MailsSend.sendMails(emailFiles);
			temp1 = "sendMails "+emailFiles.length;
    		temp2 = "("+DateUtil.format(new Date(), dmy_hms)+")";
            jtaMail.append(j+"."+temp1.trim()+temp2+"\n");
            jtaMail.append(StringUtils.getStr(width, "-")+"\n");
            j++;
		}
	}
}
