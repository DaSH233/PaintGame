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
	 //标记游戏是否开始
	 private boolean start = false;
	 //标记游戏是否结束
	 private boolean exit = false;
	 //用于倒计时
	 private int time = 60;
	 //保存当前绘画者编号
	 private int painter = 0;
	 //保存当前回合数，一共进行两回合
	 private int turn = 0;
	 //保存回答正确后可以获得的分数
	 private int addscore = 3;
	 //保存颜色
	 private StringBuffer color = new StringBuffer(String.valueOf(Color.BLACK.getRGB()));
	 //保存粗细
	 private StringBuffer stroke = new StringBuffer("3.0");
	 //保存前后坐标
	 private int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
	 //玩家列表
	 private List<String> playerID = new ArrayList<>();
	 //得分列表
	 private List<Integer> score = new ArrayList<>();
	 //聊天记录
	 private List<String> answer = new ArrayList<>();
	 //系统消息
	 private List<String> systemInfo = new ArrayList<>();
	 //问题列表
	 private List<Quetion> questions = new ArrayList<>();
	 //问题类
	 class Quetion {
		 //问题
		 public String Name;
		 //提示
		 public String tip;
		 
		 public Quetion (String Name, String tip){
			 this.Name = Name;
			 this.tip = tip;
		 }
	 }
	 
	 public RoomServer(int roomId){
		 //倒计时
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
		 //读取题目
		 readQuetion("./q1.txt");
		 //打乱题目顺序
		 Collections.shuffle(questions);
		 init(roomId);
	 }
	 
	 //读取指定目录下的题目
	 public void readQuetion(String filePath){
	        try {
	                File file=new File(filePath);
	                //判断文件是否存在
	                if(file.isFile() && file.exists()){ 
	                	//考虑到编码格式
	                    InputStreamReader read = new InputStreamReader(new FileInputStream(file),"GBK");
	                    BufferedReader bufferedReader = new BufferedReader(read);
	                    String lineName = null;
	                    String lineTip = null;
	                    while((lineName = bufferedReader.readLine()) != null && (lineTip = bufferedReader.readLine()) != null){
	                        questions.add(new Quetion(lineName, lineTip));
	                    }
	                    read.close();
	        }else{
	            System.out.println("找不到指定的文件");
	        }
	        } catch (Exception e) {
	            System.out.println("读取文件内容出错");
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
				 //若游戏结束则退出循环
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
			//保存该线程管理的用户的id
			StringBuffer id = new StringBuffer();
			//标记该用户是否回答正确，防止重复输出系统信息
			boolean correct = false;
			try{
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				ps = new PrintStream(socket.getOutputStream());
			    while (true) {
			    	//如果该回合结束，则重置
			    	if(time == 0)
			    		correct = false;
			        String tag = br.readLine();
			        if(tag != null) {
			        	    //操作为保存颜色
				        	if(tag.equals("wcolor"))
				        		color = new StringBuffer(br.readLine());
				        	//操作为保存粗细
				        	else if(tag.equals("wstroke"))
				        		stroke = new StringBuffer(br.readLine());
				        	//操作为保存前后坐标
				        	else if(tag.equals("wxy")) {
				        		x1 = Integer.valueOf(br.readLine()).intValue();
				        		y1 = Integer.valueOf(br.readLine()).intValue();
				        		x2 = Integer.valueOf(br.readLine()).intValue();
				        		y2 = Integer.valueOf(br.readLine()).intValue();
				        	}
				        	//操作为保存id
				        	else if(tag.equals("wid")) {
				        		id = new StringBuffer(br.readLine());
				        		playerID.add(id.toString());
				        		score.add(new Integer(0));
				        	}
				        	//操作为保存聊天信息，并判断回答是否正确，若正确，则不在其他用户屏幕上显示，并显示显示系统消息该用户回答正确
				        	else if(tag.equals("wanswer")) {
				        		String str = br.readLine();
				        		if(str.equals(questions.get(painter + turn * playerID.size()).Name)) {
				        			if(!playerID.get(painter).equals(id.toString()) && !correct) {
				        				systemInfo.add("【" + id + "】" + "回答正确 +" + addscore);
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
				        	//操作为开始游戏
				        	else if(tag.equals("wstart")) {
				        		start = true;
				        		time = 60;
				        	}
				        	//操作为结束游戏
				        	else if(tag.equals("wexit"))
				        		exit = true;
				        	//操作为用户退出，结束进程
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
			        	    //操作为读取颜色
				        	if(tag.equals("rcolor"))
				        		ps.println(color);
				        	//操作为读取粗细
				        	else if(tag.equals("rstroke"))
				        		ps.println(stroke);
				        	//操作为读取前后坐标
				        	else if(tag.equals("rxy")) {
				        		ps.println(x1);
				        		ps.println(y1);
				        		ps.println(x2);
				        		ps.println(y2);
				        	}
				        	//操作为读取id
				        	else if(tag.equals("rid")) {
				        		for(int i = 0; i < playerID.size(); i++)
				        			ps.println(playerID.get(i));
				        		ps.println("end");
				        	}
				        	//操作为读取得分
				        	else if(tag.equals("rscore")) {
				        		for(int i = 0; i < score.size(); i++)
				        			ps.println(score.get(i).intValue());
				        		ps.println("end");
				        	}
				        	//操作为读取聊天记录
				        	else if(tag.equals("ranswer")) {
				        		for(int i = 0; i < answer.size(); i++)
				        			ps.println(answer.get(i));
				        		ps.println("end");
				        	}
				        	//操作为读取系统消息
				        	else if(tag.equals("rsysInfo")) {
				        		for(int i = 0; i < systemInfo.size(); i++)
				        			ps.println(systemInfo.get(i));
				        		ps.println("end");
				        	}
				        	//操作为读取倒计时
				        	else if(tag.equals("rtime"))
				        		ps.println(time);
				        	//操作为读取绘画者id
				        	else if(tag.equals("rpainter")){
				        		if(playerID.isEmpty())
				        			ps.println("???");
				        		else
				        			ps.println(playerID.get(painter));
				        	}
				        	//操作为读取提示
				        	else if(tag.equals("rtips")) {
				        		if(start)
				        			ps.println(questions.get(painter + turn * playerID.size()).tip);
				        		else
				        			ps.println(new String("null"));
				        	}
				        	//操作为读取问题
				        	else if(tag.equals("rquestion")) {
				        		if(start)
				        			ps.println(questions.get(painter + turn * playerID.size()).Name);
				        		else
				        			ps.println(new String("null"));
				        	}
				        	//操作为读取游戏是否结束
				        	else if(tag.equals("rend")) {
				        		if(turn == 2)
				        			ps.println("end");
				        		else
				        			ps.println("continue");
				        	}
				        	//操作为用户退出，结束进程
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
