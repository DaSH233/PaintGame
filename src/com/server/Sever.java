package com.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.spi.IIOServiceProvider;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

//�ܷ�����������ÿ������
public class Sever extends JFrame{
	 //�������б�
	 private List<String> roomName = new ArrayList<>();
	 //����id�б�
	 private List<Integer> roomId = new ArrayList<>();
	 //��ʾ�����Ƿ��ѱ�ռ��
	 private boolean[] roomAvailable = new boolean[]{true, true, true, true, true, true, true, true, true, true};
	 private JList roomList = new JList();
	 private JLabel ip = new JLabel();
	
	 public static void main(String[] args) throws IOException {
		 Sever sever = new Sever();
		 sever.init();
	 }
	 
	 public Sever(){
		 ip.setFont(new Font("΢���ź�", Font.BOLD, 20));
		 try {
			 InetAddress address = InetAddress.getLocalHost();
			 ip.setText("������ip��" + address.getHostAddress());
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 roomList.setPreferredSize(new Dimension(400,600));
		 roomList.setBackground(Color.WHITE);
		 roomList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		 this.setLayout(new BorderLayout());
		 this.add(ip, BorderLayout.NORTH);
		 this.add(roomList, BorderLayout.CENTER);
		 this.setTitle("�㻭�Ҳ·�����v0.233");  
	     this.setSize(400,600);  
	     this.setLocationRelativeTo(null);  
	     this.setDefaultCloseOperation(EXIT_ON_CLOSE);  
	     this.setVisible(true);
	 }
	 
	 public void init() {
		 //���������˿ڵķ���һ�����ڽ��գ�һ�����ڷ���
		 ServerSocket serverSocket1 = null;
		 ServerSocket serverSocket2 = null;
		 try {  
			 serverSocket1 = new ServerSocket(10000);
			 serverSocket2 = new ServerSocket(11000);
			 //ѭ���ȴ�socket����
			 while(true) {
				 Socket socket1 = serverSocket1.accept();
				 Socket socket2 = serverSocket2.accept();
				 //�½����պͷ��������߳�
				 new Thread(new get(socket1)).start(); 
				 new Thread(new send(socket2)).start(); 
	         }
	     } catch (Exception e) {  
	         e.printStackTrace();  
	     } finally{  
	         try {  
	             if(serverSocket1 != null)
	            	 serverSocket1.close();   
	             if(serverSocket2 != null)
	            	 serverSocket2.close();   
	         } catch (IOException e) {  
	             e.printStackTrace();  
	         }  
	     }  
	 }
	 
	 //��ȡ�߳�
	 class get implements Runnable{
		 private Socket socket;  
		  
		 public get(Socket socket) {  
		     this.socket = socket;  
		 }  
			
		 @Override
		 public void run() {
			System.out.println("sever get start");
			BufferedReader br = null;
			PrintStream ps = null;
			try{
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				ps = new PrintStream(socket.getOutputStream());
				//ѭ�����ղ�������
			    while (true) {
			    	String tag = br.readLine();
			        if(tag != null) {
			        	//������Ϊ��������
			        	if(tag.equals("createRoom")) {
			        		String name = br.readLine();
					        int i = 0;
					        //�������з��䣬���пգ���ʹ�ã�������������������û�У��򷵻���Ϣ
					        for(i = 0; i < 10; i++)
					        	if(roomAvailable[i]) {
					        		roomAvailable[i] = false;
					        		roomName.add(name);
					        		roomId.add(20000 + i);
					        		ps.println(20000 + i);
					        		final int fi = i + 20000;
					        		new Thread(new Runnable() {
					        			@Override
					        			public void run() {
					        					try {
					        		                RoomServer roomServer = new RoomServer(fi);
					        		            } catch (Exception e) {
					        		                e.printStackTrace();
					        				}
					        			}
					        		}).start();
					        		roomList.setListData(roomName.toArray());
					        		break;
					        	}
					        if(i == 10)
					        	ps.println("full");
			        	}
			        	//������Ϊɾ�����䣬��ɾ���б��еķ��䣬�����ø÷���Ϊ����
			        	else if(tag.equals("removeRoom")) {
			        		int removeId = Integer.valueOf(br.readLine()).intValue();
			        		roomAvailable[removeId - 20000] = true;
			        		for(int i = 0; i < roomId.size(); i++) {
			        			if(roomId.get(i).intValue() == removeId) {
			        				roomName.remove(i);
			        				roomId.remove(i);
			        				roomList.setListData(roomName.toArray());
			        				break;
			        			}
			        		}
			        	}
			        	//������Ϊ�˳������˳�ѭ��
			        	else if(tag.equals("exit"))
			        		break;
			        }
			    }
			}catch (IOException e) {
				e.printStackTrace();
			}finally{  
	            try{
	                if(br != null)
	                    br.close();
	                if(ps != null)
		                ps.close();
	                if(socket != null)
	                    socket = null;
	                System.out.println("sever get end");
	            }catch (IOException e) {
					e.printStackTrace();
				}
	        }  
		}
	}
	 
	 //�����߳�
	 class send implements Runnable{
		 private Socket socket;  
		  
		 public send(Socket socket) {  
		     this.socket = socket;  
		 }  
			
		 @Override
		 public void run() {
			System.out.println("sever send start");
			BufferedReader br = null;
			PrintStream ps = null;
			try{
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				ps = new PrintStream(socket.getOutputStream());
				//ѭ�����ղ�������
			    while (true) {
			        String tag = br.readLine();
			        if(tag != null) {
			        	//������Ϊ��ȡ�������б�����
			        	if(tag.equals("rroomName")) {
			        		for(int i = 0; i < roomName.size(); i++)
			        			ps.println(roomName.get(i));
			        		ps.println("end");
			        	}
			        	//������Ϊ��ȡ����id�б�����
			        	else if(tag.equals("rroomId")) {
			        		for(int i = 0; i < roomId.size(); i++)
			        			ps.println(roomId.get(i));
			        		ps.println("end");
			        	}
			        	//������Ϊ�˳��������ѭ��
			        	else if(tag.equals("exit"))
			        		break;
			        }
			    }
			}catch (IOException e) {
				e.printStackTrace();
			}finally{  
	            try{
	                if(br != null)
	                    br.close();
	                if(ps != null)
		                ps.close();  
	                if(socket != null)
	                    socket = null;  
	                System.out.println("sever send end");
	            }catch (IOException e) {
					e.printStackTrace();
				}
	        }  
		}
	}
}
