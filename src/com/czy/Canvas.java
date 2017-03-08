package com.czy;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

//游戏主界面，在此进行游戏
public class Canvas extends JFrame{
	
	public Color color = Color.BLACK;//设置初始颜色  
	public BasicStroke stroke = new BasicStroke(3);
	public boolean begin = false;
	public JPanel paintArea = new JPanel();
	private boolean showOnce = false;
	private StringBuffer id = new StringBuffer();
	private Graphics g = null;
	private Graphics2D g2 = null;
	private BufferedImage image;
	private Canvas canvas = this;
	private JPanel login = new JPanel();
	private JTextField inputID = new JTextField(10);
	private JButton btnlogin = new JButton("加入");
	private JPanel game = new JPanel();
	private JLabel painterId = new JLabel();
	private JPanel colorBar = new JPanel();
	private JPanel strokeBar = new JPanel();
	private ButtonGroup strokeGroup = new ButtonGroup();
	private JRadioButton pancel = new JRadioButton("画笔"); 
	private JRadioButton rubber = new JRadioButton("橡皮"); 
	private ButtonGroup toolGroup = new ButtonGroup();
	private JButton colorchooserbtn = new JButton(); 
	private JLabel timeCount = new JLabel();
	private JPanel toolBar = new JPanel();
	private JButton btnStart = new JButton("开始");
	private JButton btnBack = new JButton("退出");
	private JPanel btnArea = new JPanel();
	private JList playerList = new JList();
	private JList scoreList = new JList();
	private JPanel listArea = new JPanel();
	private JTextPane textAnswer = new JTextPane();
	SimpleAttributeSet attrSystem = new SimpleAttributeSet();   
    SimpleAttributeSet attrPlayer = new SimpleAttributeSet();   
	private JTextArea textInput = new JTextArea(5, 5);
	private JPanel talkArea = new JPanel();
	private List<String> playerID = new ArrayList<>();
    private List<String> score = new ArrayList<>();
    private Color colorselector;
    private SocketIO socketIO;
    private boolean owner;
    private int roomId;
	
	public Canvas (boolean owner, int roomId, String roomName, String ip) {
		//获取当前用户是否为房主，房间id，并创建自定义socket通信类
		this.owner = owner;
		this.roomId = roomId;
		socketIO = new SocketIO(roomId);
		//-----------------login---------------   
		//输入用户id，若不输入则不能操作，只能观看
		btnlogin.addActionListener(loginlistener);
		login.add(inputID);
		login.add(btnlogin);
		login.setVisible(true);
        //-----------------colorBar---------------   
		//颜色选择框
        Color colorArray[] = new Color[]{Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.GREEN, 
        		Color.LIGHT_GRAY, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.YELLOW};  
        colorBar.setPreferredSize(new Dimension(180,60));  
        //设置布局类型为2行6列
        colorBar.setLayout(new GridLayout(2, 6, 0, 0));  
        //创建一个颜色组      
        for(int i = 0; i < colorArray.length; i++){  
            JButton colorbtn = new JButton();  
            //设置按钮背景色
            colorbtn.setBackground(colorArray[i]);  
            colorbtn.setPreferredSize(new Dimension(30,30));  
            colorBar.add(colorbtn);
            colorbtn.addActionListener(colorbtnlistener);  
        }
        //-----------------colorchooserbtn--------------- 
        //自定义颜色选择框，并且通过按钮颜色显示当前画笔颜色
        colorchooserbtn.setBackground(Color.BLACK);
        colorchooserbtn.setPreferredSize(new Dimension(60,60)); 
        colorchooserbtn.addActionListener(colorlistener);
        //-----------------strokeBar---------------    
        //选择画笔的粗细，使用按钮组
        strokeBar.setPreferredSize(new Dimension(120,60));  
        strokeBar.setLayout(new GridLayout(3, 2, 0, 0));  
        for(int i = 0; i < 6; i++){  
            JRadioButton strokebtn = new JRadioButton();
            strokebtn.setBackground(Color.LIGHT_GRAY);
            strokebtn.setText(String.valueOf(i + 1));
            if(i == 0)
            	strokebtn.setSelected(true);
            strokebtn.addActionListener(strokelistener);
            strokeGroup.add(strokebtn);
            strokeBar.add(strokebtn);
        }
        //-----------------paintTool---------------  
        //选择画笔和橡皮，实现方式与粗细的选择相同
        pancel.setBackground(Color.LIGHT_GRAY);
        pancel.addActionListener(toollistener);
        pancel.setSelected(true);
        rubber.setBackground(Color.LIGHT_GRAY);
        rubber.addActionListener(toollistener);
        toolGroup.add(pancel);
        toolGroup.add(rubber);
        //-----------------toolBar---------------  
        //上方的整个工具栏
        //显示当前绘画者的id
        painterId.setFont(new Font("微软雅黑", Font.BOLD, 20));
        //显示倒计时
        timeCount.setFont(new Font("微软雅黑", Font.BOLD, 40));
        toolBar.add(painterId);
        toolBar.add(pancel);
        toolBar.add(rubber);
        toolBar.add(colorBar);
        toolBar.add(colorchooserbtn);
        toolBar.add(strokeBar);
        toolBar.add(timeCount);
        toolBar.setPreferredSize(new Dimension(200,80));
        toolBar.setBackground(Color.LIGHT_GRAY);
        toolBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //-----------------btnArea---------------
        //左边区域的按钮区，包括开始和退出按钮
        btnStart.addActionListener(startlistener);
        //只有房主才能点击该按钮
        if(!owner)
        	btnStart.setEnabled(false);
        btnBack.addActionListener(backlistener);
        btnArea.add(btnStart);
        btnArea.add(btnBack);
        btnArea.setPreferredSize(new Dimension(100,75));
        btnArea.setBackground(Color.LIGHT_GRAY);
        btnArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //-----------------playerList---------------
        //显示玩家列表
        playerList.setPreferredSize(new Dimension(100,800));
        playerList.setBackground(Color.LIGHT_GRAY);
        playerList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //-----------------scoreList---------------
        //显示分数列表，与玩家列表对齐
        scoreList.setPreferredSize(new Dimension(30,800));
        scoreList.setBackground(Color.LIGHT_GRAY);
        scoreList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //-----------------listArea---------------
        //整个左边区域
        listArea.setLayout(new BorderLayout());
        listArea.add(playerList, BorderLayout.CENTER);
        listArea.add(scoreList, BorderLayout.EAST);
        listArea.add(btnArea, BorderLayout.NORTH);
        listArea.setPreferredSize(new Dimension(100,800));
        listArea.setBackground(Color.LIGHT_GRAY);
        listArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //-----------------textAnswer--------------- 
        //右边聊天框的文字显示框
        StyleConstants.setForeground(attrSystem, Color.RED);  
        StyleConstants.setForeground(attrPlayer, Color.BLACK); 
        textAnswer.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        textAnswer.setEditable(false);
        //-----------------textInput---------------
        //右边聊天框的文字输入框
        textInput.setLineWrap(true);
        textInput.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        textInput.setWrapStyleWord(true);
        textInput.addKeyListener(keyListener);
        //-----------------talkArea---------------
        //整个右边聊天框
        talkArea.setLayout(new BorderLayout());
        talkArea.add(new JScrollPane(textAnswer), BorderLayout.CENTER);
        talkArea.add(new JScrollPane(textInput), BorderLayout.SOUTH);
        talkArea.setPreferredSize(new Dimension(100,800));
        talkArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //-----------------game---------------  
        //整个游戏区域
        paintArea.setBackground(Color.WHITE);
        game.setLayout(new BorderLayout());  
        game.add(paintArea, BorderLayout.CENTER);
        game.add(toolBar, BorderLayout.NORTH);
        game.add(listArea, BorderLayout.WEST);
        game.add(talkArea, BorderLayout.EAST);
        game.setBackground(Color.WHITE); 
        game.setVisible(true);
        //-----------------canvas---------------  
        //整个窗口
        this.setLayout(new BorderLayout());  
        this.add(game, BorderLayout.CENTER);
        this.add(login, BorderLayout.SOUTH);
        this.setTitle("房间 ：" + String.valueOf(roomId));  
        this.setSize(800,600);  
        this.setLocationRelativeTo(null);  
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);   
        this.setVisible(true);
        //设置缩放监听
        this.addComponentListener(new ComponentAdapter(){
        	@Override public void componentResized(ComponentEvent e){
        		//改变画布和图像大小，以适应窗口
        		image.getScaledInstance(paintArea.getWidth(), paintArea.getHeight(), BufferedImage.SCALE_DEFAULT);
        		g = paintArea.getGraphics();
		        g.drawImage(image, 0, 0, paintArea.getWidth(), paintArea.getHeight(), paintArea);
        	}});
        //设置窗口关闭监听
        this.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		//如果是房主，则在关闭窗口时也删除房间
        		if(owner) {
            		socketIO.sendExit();
                	closeRoom();
            	}
        		//关闭socket连接
        		socketIO.close();
                System.out.println("end");
        	}
		});
        //初始化画布
        g = paintArea.getGraphics();
        image = new BufferedImage(canvas.paintArea.getWidth(), canvas.paintArea.getHeight(), BufferedImage.TYPE_INT_RGB);
        g2 = image.createGraphics();
        g2.fillRect(0, 0, canvas.paintArea.getWidth(), canvas.paintArea.getHeight());
        g2.setStroke(stroke);
        g2.setColor(color);
        //-----------------socket---------------  
        //接收socket信息
        getsocket();
        //进程自然停止需要执行的操作，与前部分中断操作类似
        if(owner) {
    		socketIO.sendExit();
        	closeRoom();
    	}
        socketIO.close();
        //弹出排行榜
        Leaderboard leaderboard = new Leaderboard(playerID, score);
        dispose();
    }
	
	public void getsocket() {
		int start = 0;
        int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
        List<String> answer = new ArrayList<>();
        List<String> sysInfo = new ArrayList<>();
        StringBuffer painter = new StringBuffer();
        int answer_count = 0;
        int sysInfo_count = 0;
        //循环接收各种信息
        while(true) {
        	//若游戏结束，则退出循环
        	if(socketIO.getEnd())
        		break;
        	//获取当前绘画者，并判断绘画者是否已经变换
        	if(!painter.toString().equals(socketIO.getPainter())) {
	        	showOnce = true;
	        	painter = new StringBuffer(socketIO.getPainter());
        	}
        	//显示当前绘画者
        	painterId.setText("当前绘画者：" + painter.toString());
        	//判断当前用户是否是绘画者
        	if(id.toString().equals(painter.toString()))
        		begin = true;
        	else
        		begin = false;
        	//若当前用户不是绘画者
        	if(!begin) {
        		//-----------------xy---------------
        		//获取绘画信息：坐标，画笔，颜色，在屏幕上显示图像
        		int[] xy = socketIO.getXY();
        		x1 = xy[0];
        		y1 = xy[1];
        		x2 = xy[2];
        		y2 = xy[3];
        		color = new Color(socketIO.getColor());
        		stroke = new BasicStroke(socketIO.getStroke());
        		image.getScaledInstance(paintArea.getWidth(), paintArea.getHeight(), BufferedImage.SCALE_DEFAULT);
        		g2.setColor(color);
                g2.setStroke(stroke);
		        g2.drawLine(x1, y1, x2, y2);
		        g = paintArea.getGraphics();
		        g.drawImage(image, 0, 0, paintArea.getWidth(), paintArea.getHeight(), paintArea);
		        x1 = y1 = x2 = y2 = 0;
	        	//-----------------tips---------------
		        //若谜题刚开始，则获取当前谜题的提示并显示
		        if(showOnce && !socketIO.getTips().equals("null")) {
		        	g2.setColor(Color.WHITE);
		        	g2.fillRect(0, 0, canvas.paintArea.getWidth(), canvas.paintArea.getHeight());
		            insert("提示：" + socketIO.getTips() + "\n", attrSystem);   
		        	showOnce = false;
		        }
        	}
        	//若当前用户为绘画者，并且谜题刚刚开始，则显示当前谜题
        	else {
        		if(showOnce && !socketIO.getQuestion().equals("null")) {
        			g2.setColor(Color.WHITE);
        			g2.fillRect(0, 0, canvas.paintArea.getWidth(), canvas.paintArea.getHeight());
        			image.getScaledInstance(paintArea.getWidth(), paintArea.getHeight(), BufferedImage.SCALE_DEFAULT);
            		g2.setColor(color);
                    g2.setStroke(stroke);
    		        g2.drawLine(x1, y1, x2, y2);
    		        g = paintArea.getGraphics();
    		        g.drawImage(image, 0, 0, paintArea.getWidth(), paintArea.getHeight(), paintArea);
        			insert("本回合题目：" + socketIO.getQuestion() + "\n", attrSystem); 
        			showOnce = false;
    	        }
        	}
        	//-----------------id---------------
        	//获取用户列表
        	playerID = socketIO.getID();
	        playerList.setListData(playerID.toArray());
	        //-----------------score---------------
	        //获取分数列表
        	score = socketIO.getScore();
	        scoreList.setListData(score.toArray());
	        //-----------------answer---------------
	        //获取聊天内容
	        answer = socketIO.getAnsewer();
	        for(int i = answer_count; i < answer.size(); i++)
	        	insert(answer.get(i) + "\n", attrPlayer);
	        answer_count = answer.size();
	        //-----------------answer---------------
	        //获取系统信息
	        sysInfo = socketIO.getSysinfo();
	        for(int i = sysInfo_count; i < sysInfo.size(); i++)
	        	insert(sysInfo.get(i) + "\n", attrSystem);
	        sysInfo_count = sysInfo.size();
	        //-----------------time---------------
	        //获取倒计时
	        timeCount.setText(socketIO.getTime());
        }
	}
	
	//按输入的文字样式向聊天显示框插入文字
	public void insert(String str, SimpleAttributeSet attrSet){  
		Document doc = textAnswer.getDocument();
		try{   
            doc.insertString(doc.getLength(), str, attrSet);   
        }   
        catch(BadLocationException e){   
            System.out.println( "BadLocationException:" + e);   
        }
    }   
	
	//关闭并删除房间
	public void closeRoom() {
		//这里再建立一次socket的目的是让服务器的循环等待再一次回到等待头进行判断并退出循环，否则服务器就会一直等待着socket的连接
		SocketIO socketIO = new SocketIO(roomId);
		socketIO.close();
		LoginSocketIO loginSocketIO = new LoginSocketIO();
		loginSocketIO.removeRoom(roomId);
		loginSocketIO.close();
	}
	
	//输入框监听，输入回车发送信息
	KeyListener keyListener = new KeyListener() {
		
		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_ENTER){
				socketIO.sendAnswer(textInput.getText());
				textInput.setText("");
			}
			
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
	}; 
    
	//退出按钮监听，点击关闭窗口
	ActionListener backlistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //获取事件源，发生事件的对象  
            Object obj = e.getSource();  
            if(obj instanceof JButton){  
            	if(owner) {
            		socketIO.sendExit();
                	closeRoom();
            	}
            	socketIO.close();
            	dispose();
            }
        }  
    };  
    
    //开始按钮监听，点击开始游戏
	ActionListener startlistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //获取事件源，发生事件的对象  
            Object obj = e.getSource();  
            if(obj instanceof JButton){
            	socketIO.sendStart();
            	btnStart.setEnabled(false);
            	g2.setColor(Color.WHITE);
            	g2.fillRect(0, 0, canvas.paintArea.getWidth(), canvas.paintArea.getHeight());
            }
        }  
    };  
    
    //确认输入id按钮监听，即用户输完id之后需要点击的确认按钮，点击之后加入游戏，并进行初始化
	ActionListener loginlistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //获取事件源，发生事件的对象  
            Object obj = e.getSource();  
            if(obj instanceof JButton){  
            	if(!inputID.getText().isEmpty()) {
            		//登录之后设置登录id框为不可见
	                game.setVisible(true);
	                login.setVisible(false);
	                canvas.repaint();
	                canvas.validate();
	                //-----------------paintArea---------------
	                //获取当前画布并进行初始化
	                g = paintArea.getGraphics();
	                image = new BufferedImage(canvas.paintArea.getWidth(), canvas.paintArea.getHeight(), BufferedImage.TYPE_INT_RGB);
	                g2 = image.createGraphics();
	                g2.fillRect(0, 0, canvas.paintArea.getWidth(), canvas.paintArea.getHeight());
	                g2.setStroke(stroke);
	                g2.setColor(color);
	                Canvaslistener listener = new Canvaslistener(g, g2, image, canvas, socketIO);  
	                paintArea.addMouseListener(listener);
	                paintArea.addMouseMotionListener(listener);
	                id = new StringBuffer(inputID.getText());
	                //发送信息告知服务器，该id的用户已加入游戏
	                socketIO.sendID(inputID.getText());
	                socketIO.sendColor(String.valueOf(color.getRGB()));
	                socketIO.sendStroke(String.valueOf(stroke.getLineWidth()));
	                inputID.setText("");
            	}
            }
        }  
    };  
    
    //监听画笔和橡皮的切换
    ActionListener toollistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //获取事件源，发生事件的对象  
            Object obj = e.getSource();  
            if(obj instanceof JRadioButton){  
            	if(((JRadioButton)obj).getText().equals("画笔"))
            		color = colorchooserbtn.getBackground();
            	else if(((JRadioButton)obj).getText().equals("橡皮"))
            		color = Color.WHITE;
            }
        }  
    };  
    
    //添加按钮监听，获取按钮的背景颜色  
    ActionListener colorbtnlistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //获取事件源，发生事件的对象  
            Object obj = e.getSource();  
            if(obj instanceof JButton){  
                color=((JButton) obj).getBackground();  
                colorchooserbtn.setBackground(color);
            }
        }  
    };  
    
    //监听自定义颜色按钮，调用颜色选择器组件
    ActionListener colorlistener = new ActionListener(){   
        public void actionPerformed(ActionEvent e){  
                colorselector = JColorChooser.showDialog(null, "颜色选择器", Color.BLACK);  
                color = colorselector;
                colorchooserbtn.setBackground(colorselector);;  
        }  
    };  
    
    //监听画笔粗细选择按钮
    ActionListener strokelistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //获取事件源，发生事件的对象  
            Object obj = e.getSource();  
            if(obj instanceof JRadioButton){  
	    		stroke = new BasicStroke(Integer.valueOf(((JRadioButton)obj).getText()) * 2 + 1);
	            socketIO.sendStroke(String.valueOf(stroke.getLineWidth()));
            }
        }  
    };  
}
