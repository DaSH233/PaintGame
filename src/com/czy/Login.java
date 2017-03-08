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

//��¼���棬�����ڸý��洴�������뷿��
public class Login extends JFrame{
	
	//�������䰴ť
	private JButton createRoom = new JButton("��������");
	//���뷿�䰴ť
	private JButton goRoom = new JButton("���뷿��");
	//��ť����
	private JPanel btnArea = new JPanel();
	//�����б�
	private JList roomList = new JList();
	//�Զ���socket������
	private LoginSocketIO loginSocketIO = new LoginSocketIO();
	//�������б�
	private List<String> roomName = new ArrayList<>();
	
	public Login() {
		//���ð�ť�����¼�
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
        this.setTitle("�㻭�Ҳ�v0.233");  
        this.setSize(400,600);  
        this.setLocationRelativeTo(null);  
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);  
        this.setVisible(true);
        //�����˳�ʱִ�еĲ���
        Runtime.getRuntime().addShutdownHook(new Thread(){
        	public void run(){
        		//�ر�socket����
        		loginSocketIO.close();
                System.out.println("end");
                }
        	});
        //ʵʱ���շ�����Ϣ
        while(true) {
        	//����ʵʱˢ�£�����������Ҫ�ֶ���ѡ�еķ�����Ϊѡ�У�������Ļ�ϲ���Ч��
        	int selected = roomList.getSelectedIndex();
        	roomName = loginSocketIO.getRoomName();
        	//����JList������Ϊ���յ����б�
        	roomList.setListData(roomName.toArray());
        	roomList.setSelectedIndex(selected);
        	try {
        		//����˯�ߣ����ٽ���Ƶ��
        		Thread.currentThread().sleep(100);
        	}catch (Exception e) {}
        }
	}
	
	//�������䰴ť�ļ����¼�
	ActionListener createlistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //��ȡ�¼�Դ�������¼��Ķ���  
            Object obj = e.getSource();
            //���¼�ԴΪ��ť��ִ�����²���
            if(obj instanceof JButton){
            	new Thread(new Runnable() {
    				@Override
    				public void run() {
    					//�����´��ڣ���ʾ���뷿����Ϣ
    					CreateRoom createRoom = new CreateRoom(loginSocketIO);
    				}
    			}).start();
            }
        }  
    };
    
    //���뷿�䰴ť�����¼�
    ActionListener golistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //��ȡ�¼�Դ�������¼��Ķ���  
            Object obj = e.getSource();  
            if(obj instanceof JButton){ 
            	if(roomList.getSelectedValue() != null) {
	            	new Thread(new Runnable() {
	    				@Override
	    				public void run() {
	    					//�����´����ڣ�������Ϸ���棬������������������ҷ���ĵ�ַ����Ϊ���أ�����ݲ�ͬ��������ַ�޸�
	    					Canvas canvas = new Canvas(false, Integer.valueOf(loginSocketIO.getRoomId().get(roomList.getSelectedIndex())).intValue(), String.valueOf(roomList.getSelectedValue()), null);
	    				}
	    			}).start();
            	}
            }
        }  
    };
}