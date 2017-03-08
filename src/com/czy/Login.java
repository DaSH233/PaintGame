package com.czy;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

//登录界面，可以在该界面创建、加入房间
public class Login extends JFrame{
	
	//创建房间按钮
	private JButton createRoom = new JButton("创建房间");
	//加入房间按钮
	private JButton goRoom = new JButton("加入房间");
	//按钮集合
	private JPanel btnArea = new JPanel();
	//房间列表
	private JList roomList = new JList();
	//自定义socket操作类
	private LoginSocketIO loginSocketIO = new LoginSocketIO();
	//房间名列表
	private List<String> roomName = new ArrayList<>();
	
	public Login() {
		//设置按钮监听事件
		createRoom.addActionListener(createlistener);
		goRoom.addActionListener(golistener);
		btnArea.add(createRoom);
		btnArea.add(goRoom);
		roomList.setPreferredSize(new Dimension(400,600));
	    roomList.setBackground(Color.WHITE);
	    roomList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setLayout(new BorderLayout());  
        this.add(btnArea, BorderLayout.NORTH);
        this.add(roomList, BorderLayout.CENTER);
        this.setTitle("你画我猜v0.233");  
        this.setSize(400,600);  
        this.setLocationRelativeTo(null);  
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);  
        this.setVisible(true);
        //设置退出时执行的操作
        Runtime.getRuntime().addShutdownHook(new Thread(){
        	public void run(){
        		//关闭socket连接
        		loginSocketIO.close();
                System.out.println("end");
                }
        	});
        //实时接收房间信息
        while(true) {
        	//由于实时刷新，所以我们需要手动将选中的房间设为选中，这样屏幕上才有效果
        	int selected = roomList.getSelectedIndex();
        	roomName = loginSocketIO.getRoomName();
        	//设置JList的内容为接收到的列表
        	roomList.setListData(roomName.toArray());
        	roomList.setSelectedIndex(selected);
        	try {
        		//进程睡眠，减少接收频率
        		Thread.currentThread().sleep(100);
        	}catch (Exception e) {}
        }
	}
	
	//创建房间按钮的监听事件
	ActionListener createlistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //获取事件源，发生事件的对象  
            Object obj = e.getSource();
            //若事件源为按钮则执行以下操作
            if(obj instanceof JButton){
            	new Thread(new Runnable() {
    				@Override
    				public void run() {
    					//创建新窗口，提示输入房间信息
    					CreateRoom createRoom = new CreateRoom(loginSocketIO);
    				}
    			}).start();
            }
        }  
    };
    
    //加入房间按钮监听事件
    ActionListener golistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //获取事件源，发生事件的对象  
            Object obj = e.getSource();  
            if(obj instanceof JButton){ 
            	if(roomList.getSelectedValue() != null) {
	            	new Thread(new Runnable() {
	    				@Override
	    				public void run() {
	    					//创建新创建口，进入游戏画面，并连入服务器，这里我服务的地址设置为本地，需根据不同服务器地址修改
	    					Canvas canvas = new Canvas(false, Integer.valueOf(loginSocketIO.getRoomId().get(roomList.getSelectedIndex())).intValue(), String.valueOf(roomList.getSelectedValue()), null);
	    				}
	    			}).start();
            	}
            }
        }  
    };
}