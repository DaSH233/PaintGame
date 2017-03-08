package com.czy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//�Զ���socketͨ���࣬����login
public class LoginSocketIO {
	private String serverAddress = null;
	private Socket socket1 = null;
	private Socket socket2 = null;
	private BufferedReader br1 = null;
	private PrintStream ps1 = null;
	private BufferedReader br2 = null;
	private PrintStream ps2 = null;
	
	//�������������socket�����������������Ĭ��ip��ַΪ����
	public LoginSocketIO() {
		InetAddress address = null;
		try {
			address = InetAddress.getLocalHost();
			this.serverAddress = address.getHostAddress();
			socket1 = new Socket(serverAddress, 10000);
			ps1 = new PrintStream(socket1.getOutputStream());
			br1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
			socket2 = new Socket(serverAddress, 11000);
			ps2 = new PrintStream(socket2.getOutputStream());
			br2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
		}catch (Exception e) {  
	        e.printStackTrace();
		}
	}

	//������ͬ�������ֶ�����ip��ַ
	public LoginSocketIO(String serverAddress) {
		try {
			this.serverAddress = serverAddress;
			socket1 = new Socket(serverAddress, 10000);
			ps1 = new PrintStream(socket1.getOutputStream());
			br1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
			socket2 = new Socket(serverAddress, 11000);
			ps2 = new PrintStream(socket2.getOutputStream());
			br2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
		}catch (Exception e) {  
	        e.printStackTrace();
		}
	}
	
	//��������
	public String createRoom(String roomName) {
		String roomId = null;
		try {
			ps1.println("createRoom");
			ps1.println(roomName);
			roomId = br1.readLine();
		}catch (IOException e) {  
	        e.printStackTrace();
		}
		return roomId;
	}
	
	//ɾ���б��ж�Ӧid�ķ��䣬�������˳���Ϸʱ����
	public void removeRoom(int roomId) {
		ps1.println("removeRoom");
		ps1.println(roomId);
	}
	
	//��ȡ�������б�
	public List<String> getRoomName() {
		List<String> roomName = new ArrayList<>();
		String mes = null;
		try {
			ps2.println("rroomName");
			mes = br2.readLine();
			while(!mes.equals("end")) {
				roomName.add(mes);
				mes = br2.readLine();
			}
		}catch (IOException e) {  
	        e.printStackTrace();
		}
		return roomName;
	}
	
	//��ȡ����id�б�
	public List<String> getRoomId() {
		List<String> roomId = new ArrayList<>();
		String mes = null;
		try {
			ps2.println("rroomId");
			mes = br2.readLine();
			while(!mes.equals("end")) {
				roomId.add(mes);
				mes = br2.readLine();
			}
		}catch (IOException e) {  
	        e.printStackTrace();
		}
		return roomId;
	}
	
	//�ر�socket���������������
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
