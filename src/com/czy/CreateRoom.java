package com.czy;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

//输入创建房间信息界面，在此输入房间名，或者放弃创建
public class CreateRoom extends JFrame{
	
	//确定按钮
	private JButton btnensure = new JButton("确定");
	//取消按钮
	private JButton btncancel = new JButton("取消");
	//按钮集合
	private JPanel btnArea = new JPanel();
	//输入框，输入房间名
	private JTextField inputName = new JTextField(25);
	private JPanel inputArea = new JPanel();
	private LoginSocketIO loginSocketIO;
	
	public CreateRoom(LoginSocketIO loginSocketIO) {
		this.loginSocketIO = loginSocketIO;
		btnensure.addActionListener(ensurelistener);
		btncancel.addActionListener(cancellistener);
		btnArea.add(btnensure);
		btnArea.add(btncancel);
		inputArea.add(inputName);
		this.setLayout(new GridLayout(2 ,1)); 
		this.add(inputArea);
		this.add(btnArea);
        this.setTitle("输入房间名");  
        this.setSize(400,150);  
        this.setLocationRelativeTo(null);  
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);   
        this.setVisible(true);
	}
	
	//确定按钮监听事件
	ActionListener ensurelistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //获取事件源，发生事件的对象  
            Object obj = e.getSource();  
            if(obj instanceof JButton){
            	if(!inputName.getText().isEmpty()) {
            		//获取输入的字符串
            		String roomId = loginSocketIO.createRoom(inputName.getText());
            		//若服务器还有空余（为了减小负担，服务器设置房间数最多为10个）
            		if(!roomId.equals("full")) {
		            	new Thread(new Runnable() {
		    				@Override
		    				public void run() {
		    					//新建窗口，进入游戏
		    					Canvas canvas = new Canvas(true, Integer.valueOf(roomId).intValue(), inputName.getText(), null);
		    				}
		    			}).start();
            		}
            		//清空输入框
            		inputName.setText("");
            		//退出该窗口
            		dispose();
            	}
            }
        }  
    };
    
    //取消按钮监听事件
    ActionListener cancellistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //获取事件源，发生事件的对象  
            Object obj = e.getSource();  
            if(obj instanceof JButton){
            	//什么都不做，退出该窗口
            	dispose();
            }
        }  
    };
}
