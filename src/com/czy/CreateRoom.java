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

//���봴��������Ϣ���棬�ڴ����뷿���������߷�������
public class CreateRoom extends JFrame{
	
	//ȷ����ť
	private JButton btnensure = new JButton("ȷ��");
	//ȡ����ť
	private JButton btncancel = new JButton("ȡ��");
	//��ť����
	private JPanel btnArea = new JPanel();
	//��������뷿����
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
        this.setTitle("���뷿����");  
        this.setSize(400,150);  
        this.setLocationRelativeTo(null);  
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);   
        this.setVisible(true);
	}
	
	//ȷ����ť�����¼�
	ActionListener ensurelistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //��ȡ�¼�Դ�������¼��Ķ���  
            Object obj = e.getSource();  
            if(obj instanceof JButton){
            	if(!inputName.getText().isEmpty()) {
            		//��ȡ������ַ���
            		String roomId = loginSocketIO.createRoom(inputName.getText());
            		//�����������п��ࣨΪ�˼�С���������������÷��������Ϊ10����
            		if(!roomId.equals("full")) {
		            	new Thread(new Runnable() {
		    				@Override
		    				public void run() {
		    					//�½����ڣ�������Ϸ
		    					Canvas canvas = new Canvas(true, Integer.valueOf(roomId).intValue(), inputName.getText(), null);
		    				}
		    			}).start();
            		}
            		//��������
            		inputName.setText("");
            		//�˳��ô���
            		dispose();
            	}
            }
        }  
    };
    
    //ȡ����ť�����¼�
    ActionListener cancellistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //��ȡ�¼�Դ�������¼��Ķ���  
            Object obj = e.getSource();  
            if(obj instanceof JButton){
            	//ʲô���������˳��ô���
            	dispose();
            }
        }  
    };
}
