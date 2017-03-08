package com.czy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//自定义socket通信类，用于canvas
public class SocketIO {
	
	private String serverAddress = null;
	private Socket socket1 = null;
	private Socket socket2 = null;
	private BufferedReader br1 = null;
	private PrintStream ps1 = null;
	private BufferedReader br2 = null;
	private PrintStream ps2 = null;
	
	//根据房间号计算出端口号，然后加入房间，该构造默认连接本地ip
	public SocketIO(int roomId) {
		InetAddress address = null;
		try {
			address = InetAddress.getLocalHost();
			this.serverAddress = address.getHostAddress();
			socket1 = new Socket(serverAddress, roomId);
			ps1 = new PrintStream(socket1.getOutputStream());
			br1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
			socket2 = new Socket(serverAddress, 10000 + roomId);
			ps2 = new PrintStream(socket2.getOutputStream());
			br2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
		}catch (Exception e) {  
	        e.printStackTrace();
		}
	}

	//同上，可以输入ip
	public SocketIO(int roomId, String serverAddress) {
		try {
			this.serverAddress = serverAddress;
			socket1 = new Socket(serverAddress, roomId);
			ps1 = new PrintStream(socket1.getOutputStream());
			br1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
			socket2 = new Socket(serverAddress, 10000 + roomId);
			ps2 = new PrintStream(socket2.getOutputStream());
			br2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
		}catch (Exception e) {  
	        e.printStackTrace();
		}
	}
	
	//发送颜色
	public void sendColor(String color) {
		ps1.println("wcolor");
		ps1.println(color);
	}
	
	//接收颜色
	public int getColor() {
		String color = null;
		try {
			ps2.println("rcolor");
			color = br2.readLine();
		}catch (IOException e) {  
	        e.printStackTrace();
		}
		return Integer.valueOf(color).intValue();
	}
	
	//发送粗细
	public void sendStroke(String stroke) {
		ps1.println("wstroke");
		ps1.println(stroke);
	}
	
	//接收粗细
	public int getStroke() {
		String stroke = null;
		try {
			ps2.println("rstroke");
			stroke = br2.readLine();
		}catch (IOException e) {  
	        e.printStackTrace();
		}
		return Float.valueOf(stroke).intValue();
	}
	
	//发送前后坐标
	public void sendXY(int x1, int y1, int x2, int y2) {
		ps1.println("wxy");
		ps1.println(String.valueOf(x1));
		ps1.println(String.valueOf(y1));
		ps1.println(String.valueOf(x2));
		ps1.println(String.valueOf(y2));
	}
	
	//接收前后坐标
	public int[] getXY() {
		int[] xy = new int[4];
		try {
			ps2.println("rxy");
			xy[0] = Integer.valueOf(br2.readLine()).intValue();
			xy[1] = Integer.valueOf(br2.readLine()).intValue();
			xy[2] = Integer.valueOf(br2.readLine()).intValue();
			xy[3] = Integer.valueOf(br2.readLine()).intValue();
		}catch (IOException e) {  
	        e.printStackTrace();
		}
		return xy;
	}
	
	//发送当前用户id
	public void sendID(String id) {
		ps1.println("wid");
		ps1.println(id);
	}
	
	//接收id列表
	public List<String> getID() {
		List<String> playerID = new ArrayList<>();
		String mes = null;
		try {
			ps2.println("rid");
			mes = br2.readLine();
			while(!mes.equals("end")) {
				playerID.add(mes);
				mes = br2.readLine();
			}
		}catch (IOException e) {  
	        e.printStackTrace();
		}
		return playerID;
	}
	
	//接收得分列表
	public List<String> getScore() {
		List<String> Score = new ArrayList<>();
		String mes = null;
		try {
			ps2.println("rscore");
			mes = br2.readLine();
			while(!mes.equals("end")) {
				Score.add(mes);
				mes = br2.readLine();
			}
		}catch (IOException e) {  
	        e.printStackTrace();
		}
		return Score;
	}
	
	//发送聊天消息（包括回答）
	public void sendAnswer(String answer) {
		ps1.println("wanswer");
		ps1.println(answer);
	}
	
	//接收玩家的消息列表
	public List<String> getAnsewer() {
		List<String> answer = new ArrayList<>();
		String mes = null;
		try {
			ps2.println("ranswer");
			mes = br2.readLine();
			while(!mes.equals("end")) {
				answer.add(mes);
				mes = br2.readLine();
			}
		}catch (IOException e) {  
	        e.printStackTrace();
		}
		return answer;
	}
	
	//接收系统消息
	public List<String> getSysinfo() {
		List<String> sysInfo = new ArrayList<>();
		String mes = null;
		try {
			ps2.println("rsysInfo");
			mes = br2.readLine();
			while(!mes.equals("end")) {
				sysInfo.add(mes);
				mes = br2.readLine();
			}
		}catch (IOException e) {  
	        e.printStackTrace();
		}
		return sysInfo;
	}
	
	//提示服务器开始游戏
	public void sendStart() {
		ps1.println("wstart");
	}
	
	//提示服务器房间关闭
	public void sendExit() {
		ps1.println("wexit");
	}
	
	//获取服务器倒计时
	public String getTime() {
		String time = null;
		try {
			ps2.println("rtime");
			time = br2.readLine();
		}catch (IOException e) {  
	        e.printStackTrace();
		}
		return time;
	}
	
	//获取当前绘画者id
	public String getPainter() {
		String painter = null;
		try {
			ps2.println("rpainter");
			painter = br2.readLine();
		}catch (IOException e) {  
	        e.printStackTrace();
		}
		return painter;
	}
	
	//获取提示
	public String getTips() {
		String tip = null;
		try {
			ps2.println("rtips");
			tip = br2.readLine();
		}catch (IOException e) {  
	        e.printStackTrace();
		}
		return tip;
	}
	
	//获取谜题
	public String getQuestion() {
		String question = null;
		try {
			ps2.println("rquestion");
			question = br2.readLine();
		}catch (IOException e) {  
	        e.printStackTrace();
		}
		return question;
	}
	
	//获取游戏结束信号
	public boolean getEnd() {
		String str = null;
		try {
			ps2.println("rend");
			str = br2.readLine();
		}catch (IOException e) {  
	        e.printStackTrace();
		}
		if(str.equals("end"))
			return true;
		else
			return false;
	}
	
	//关闭所有信息流，并告知服务器该玩家已退出游戏
	public void close() {
		try {
			ps1.println("exit");
			ps2.println("exit");
			br1.close();
			ps1.close();
			socket1.close();
			br2.close();
			ps2.close();
			socket2.close();
		}catch (IOException e) {  
	        e.printStackTrace();
		}
	}
}
