package com.czy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//�Զ���socketͨ���࣬����canvas
public class SocketIO {
	
	private String serverAddress = null;
	private Socket socket1 = null;
	private Socket socket2 = null;
	private BufferedReader br1 = null;
	private PrintStream ps1 = null;
	private BufferedReader br2 = null;
	private PrintStream ps2 = null;
	
	//���ݷ���ż�����˿ںţ�Ȼ����뷿�䣬�ù���Ĭ�����ӱ���ip
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

	//ͬ�ϣ���������ip
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
	
	//������ɫ
	public void sendColor(String color) {
		ps1.println("wcolor");
		ps1.println(color);
	}
	
	//������ɫ
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
	
	//���ʹ�ϸ
	public void sendStroke(String stroke) {
		ps1.println("wstroke");
		ps1.println(stroke);
	}
	
	//���մ�ϸ
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
	
	//����ǰ������
	public void sendXY(int x1, int y1, int x2, int y2) {
		ps1.println("wxy");
		ps1.println(String.valueOf(x1));
		ps1.println(String.valueOf(y1));
		ps1.println(String.valueOf(x2));
		ps1.println(String.valueOf(y2));
	}
	
	//����ǰ������
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
	
	//���͵�ǰ�û�id
	public void sendID(String id) {
		ps1.println("wid");
		ps1.println(id);
	}
	
	//����id�б�
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
	
	//���յ÷��б�
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
	
	//����������Ϣ�������ش�
	public void sendAnswer(String answer) {
		ps1.println("wanswer");
		ps1.println(answer);
	}
	
	//������ҵ���Ϣ�б�
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
	
	//����ϵͳ��Ϣ
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
	
	//��ʾ��������ʼ��Ϸ
	public void sendStart() {
		ps1.println("wstart");
	}
	
	//��ʾ����������ر�
	public void sendExit() {
		ps1.println("wexit");
	}
	
	//��ȡ����������ʱ
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
	
	//��ȡ��ǰ�滭��id
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
	
	//��ȡ��ʾ
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
	
	//��ȡ����
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
	
	//��ȡ��Ϸ�����ź�
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
	
	//�ر�������Ϣ��������֪��������������˳���Ϸ
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
