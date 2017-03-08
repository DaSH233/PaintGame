package com.server;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.imageio.spi.IIOServiceProvider;

public class RoomServer {
	 //�����Ϸ�Ƿ�ʼ
	 private boolean start = false;
	 //�����Ϸ�Ƿ����
	 private boolean exit = false;
	 //���ڵ���ʱ
	 private int time = 60;
	 //���浱ǰ�滭�߱��
	 private int painter = 0;
	 //���浱ǰ�غ�����һ���������غ�
	 private int turn = 0;
	 //����ش���ȷ����Ի�õķ���
	 private int addscore = 3;
	 //������ɫ
	 private StringBuffer color = new StringBuffer(String.valueOf(Color.BLACK.getRGB()));
	 //�����ϸ
	 private StringBuffer stroke = new StringBuffer("3.0");
	 //����ǰ������
	 private int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
	 //����б�
	 private List<String> playerID = new ArrayList<>();
	 //�÷��б�
	 private List<Integer> score = new ArrayList<>();
	 //�����¼
	 private List<String> answer = new ArrayList<>();
	 //ϵͳ��Ϣ
	 private List<String> systemInfo = new ArrayList<>();
	 //�����б�
	 private List<Quetion> questions = new ArrayList<>();
	 //������
	 class Quetion {
		 //����
		 public String Name;
		 //��ʾ
		 public String tip;
		 
		 public Quetion (String Name, String tip){
			 this.Name = Name;
			 this.tip = tip;
		 }
	 }
	 
	 public RoomServer(int roomId){
		 //����ʱ
		 new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					try {
		                Thread.sleep(1000);
		                if(turn == 2)
		                	break;
		                if(start)
		                	time--;
		                if(time == 0) {
		                	time = 60;
		                	addscore = 3;
		                	if(painter < playerID.size() - 1)
		                		painter++;
		                	else {
		                		painter = 0;
		                		turn++;
		                	}
		                }
		            } catch (InterruptedException e) {
		                e.printStackTrace();
		            }
				}
			}
		}).start();
		 //��ȡ��Ŀ
		 readQuetion("./q1.txt");
		 //������Ŀ˳��
		 Collections.shuffle(questions);
		 init(roomId);
	 }
	 
	 //��ȡָ��Ŀ¼�µ���Ŀ
	 public void readQuetion(String filePath){
	        try {
	                File file=new File(filePath);
	                //�ж��ļ��Ƿ����
	                if(file.isFile() && file.exists()){ 
	                	//���ǵ������ʽ
	                    InputStreamReader read = new InputStreamReader(new FileInputStream(file),"GBK");
	                    BufferedReader bufferedReader = new BufferedReader(read);
	                    String lineName = null;
	                    String lineTip = null;
	                    while((lineName = bufferedReader.readLine()) != null && (lineTip = bufferedReader.readLine()) != null){
	                        questions.add(new Quetion(lineName, lineTip));
	                    }
	                    read.close();
	        }else{
	            System.out.println("�Ҳ���ָ�����ļ�");
	        }
	        } catch (Exception e) {
	            System.out.println("��ȡ�ļ����ݳ���");
	            e.printStackTrace();
	        }
	 }
	 
	 public void init(int roomId) {
		 ServerSocket serverSocket1 = null;
		 ServerSocket serverSocket2 = null;
		 try {  
			 serverSocket1 = new ServerSocket(roomId);
			 serverSocket2 = new ServerSocket(10000 + roomId);
			 while(true) {
				 //����Ϸ�������˳�ѭ��
				 if(turn == 2 || exit){
					 System.out.println("exit");
					 break;
				 }
				 Socket socket1 = serverSocket1.accept();
				 Socket socket2 = serverSocket2.accept();
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
	             System.out.println("room closed");
	         } catch (IOException e) {  
	             e.printStackTrace();  
	         }  
	     }  
	 }
	 
	 class get implements Runnable{
		 private Socket socket;  
		  
		 public get(Socket socket) {  
		     this.socket = socket;  
		 }  
			
		 @Override
		 public void run() {
			System.out.println("room get start");
			BufferedReader br = null;
			PrintStream ps = null;
			//������̹߳�����û���id
			StringBuffer id = new StringBuffer();
			//��Ǹ��û��Ƿ�ش���ȷ����ֹ�ظ����ϵͳ��Ϣ
			boolean correct = false;
			try{
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				ps = new PrintStream(socket.getOutputStream());
			    while (true) {
			    	//����ûغϽ�����������
			    	if(time == 0)
			    		correct = false;
			        String tag = br.readLine();
			        if(tag != null) {
			        	    //����Ϊ������ɫ
				        	if(tag.equals("wcolor"))
				        		color = new StringBuffer(br.readLine());
				        	//����Ϊ�����ϸ
				        	else if(tag.equals("wstroke"))
				        		stroke = new StringBuffer(br.readLine());
				        	//����Ϊ����ǰ������
				        	else if(tag.equals("wxy")) {
				        		x1 = Integer.valueOf(br.readLine()).intValue();
				        		y1 = Integer.valueOf(br.readLine()).intValue();
				        		x2 = Integer.valueOf(br.readLine()).intValue();
				        		y2 = Integer.valueOf(br.readLine()).intValue();
				        	}
				        	//����Ϊ����id
				        	else if(tag.equals("wid")) {
				        		id = new StringBuffer(br.readLine());
				        		playerID.add(id.toString());
				        		score.add(new Integer(0));
				        	}
				        	//����Ϊ����������Ϣ�����жϻش��Ƿ���ȷ������ȷ�����������û���Ļ����ʾ������ʾ��ʾϵͳ��Ϣ���û��ش���ȷ
				        	else if(tag.equals("wanswer")) {
				        		String str = br.readLine();
				        		if(str.equals(questions.get(painter + turn * playerID.size()).Name)) {
				        			if(!playerID.get(painter).equals(id.toString()) && !correct) {
				        				systemInfo.add("��" + id + "��" + "�ش���ȷ +" + addscore);
					        			for(int i = 0; i < playerID.size(); i++)
						            		if(playerID.get(i).equals(id.toString())){
						            			score.set(i, score.get(i) + addscore);
						            			score.set(painter, score.get(painter) + 1);
						            			if(addscore > 1)
						            				addscore--;
						            			break;
						            		}
					        			correct = true;
				        			}
				        		}
				        		else
				        			answer.add(id + " : " + str);
				        	}
				        	//����Ϊ��ʼ��Ϸ
				        	else if(tag.equals("wstart")) {
				        		start = true;
				        		time = 60;
				        	}
				        	//����Ϊ������Ϸ
				        	else if(tag.equals("wexit"))
				        		exit = true;
				        	//����Ϊ�û��˳�����������
				        	else if(tag.equals("exit"))
				        		break;
			        }
			    }
			}catch (IOException e) {
				e.printStackTrace();
			}finally{  
	            try{
	            	for(int i = 0; i < playerID.size(); i++)
	            		if(playerID.get(i).equals(id.toString())){
	            			playerID.remove(i);
	            			score.remove(i);
	            			break;
	            		}
	                if(br != null)
	                    br.close();
	                if(ps != null)
		                ps.close();
	                if(socket != null)
	                    socket = null;
	                System.out.println("room get end");
	            }catch (IOException e) {
					e.printStackTrace();
				}
	        }  
		}
	}
	 
	 class send implements Runnable{
		 private Socket socket;  
		  
		 public send(Socket socket) {  
		     this.socket = socket;  
		 }  
			
		 @Override
		 public void run() {
			System.out.println("room send start");
			BufferedReader br = null;
			PrintStream ps = null;
			try{
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				ps = new PrintStream(socket.getOutputStream());
			    while (true) {
			        String tag = br.readLine();
			        if(tag != null) {
			        	    //����Ϊ��ȡ��ɫ
				        	if(tag.equals("rcolor"))
				        		ps.println(color);
				        	//����Ϊ��ȡ��ϸ
				        	else if(tag.equals("rstroke"))
				        		ps.println(stroke);
				        	//����Ϊ��ȡǰ������
				        	else if(tag.equals("rxy")) {
				        		ps.println(x1);
				        		ps.println(y1);
				        		ps.println(x2);
				        		ps.println(y2);
				        	}
				        	//����Ϊ��ȡid
				        	else if(tag.equals("rid")) {
				        		for(int i = 0; i < playerID.size(); i++)
				        			ps.println(playerID.get(i));
				        		ps.println("end");
				        	}
				        	//����Ϊ��ȡ�÷�
				        	else if(tag.equals("rscore")) {
				        		for(int i = 0; i < score.size(); i++)
				        			ps.println(score.get(i).intValue());
				        		ps.println("end");
				        	}
				        	//����Ϊ��ȡ�����¼
				        	else if(tag.equals("ranswer")) {
				        		for(int i = 0; i < answer.size(); i++)
				        			ps.println(answer.get(i));
				        		ps.println("end");
				        	}
				        	//����Ϊ��ȡϵͳ��Ϣ
				        	else if(tag.equals("rsysInfo")) {
				        		for(int i = 0; i < systemInfo.size(); i++)
				        			ps.println(systemInfo.get(i));
				        		ps.println("end");
				        	}
				        	//����Ϊ��ȡ����ʱ
				        	else if(tag.equals("rtime"))
				        		ps.println(time);
				        	//����Ϊ��ȡ�滭��id
				        	else if(tag.equals("rpainter")){
				        		if(playerID.isEmpty())
				        			ps.println("???");
				        		else
				        			ps.println(playerID.get(painter));
				        	}
				        	//����Ϊ��ȡ��ʾ
				        	else if(tag.equals("rtips")) {
				        		if(start)
				        			ps.println(questions.get(painter + turn * playerID.size()).tip);
				        		else
				        			ps.println(new String("null"));
				        	}
				        	//����Ϊ��ȡ����
				        	else if(tag.equals("rquestion")) {
				        		if(start)
				        			ps.println(questions.get(painter + turn * playerID.size()).Name);
				        		else
				        			ps.println(new String("null"));
				        	}
				        	//����Ϊ��ȡ��Ϸ�Ƿ����
				        	else if(tag.equals("rend")) {
				        		if(turn == 2)
				        			ps.println("end");
				        		else
				        			ps.println("continue");
				        	}
				        	//����Ϊ�û��˳�����������
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
	                System.out.println("room send end");
	            }catch (IOException e) {
					e.printStackTrace();
				}
	        }  
		}
	}
}
