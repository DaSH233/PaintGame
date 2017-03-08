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

//��Ϸ�����棬�ڴ˽�����Ϸ
public class Canvas extends JFrame{
	
	public Color color = Color.BLACK;//���ó�ʼ��ɫ  
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
	private JButton btnlogin = new JButton("����");
	private JPanel game = new JPanel();
	private JLabel painterId = new JLabel();
	private JPanel colorBar = new JPanel();
	private JPanel strokeBar = new JPanel();
	private ButtonGroup strokeGroup = new ButtonGroup();
	private JRadioButton pancel = new JRadioButton("����"); 
	private JRadioButton rubber = new JRadioButton("��Ƥ"); 
	private ButtonGroup toolGroup = new ButtonGroup();
	private JButton colorchooserbtn = new JButton(); 
	private JLabel timeCount = new JLabel();
	private JPanel toolBar = new JPanel();
	private JButton btnStart = new JButton("��ʼ");
	private JButton btnBack = new JButton("�˳�");
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
		//��ȡ��ǰ�û��Ƿ�Ϊ����������id���������Զ���socketͨ����
		this.owner = owner;
		this.roomId = roomId;
		socketIO = new SocketIO(roomId);
		//-----------------login---------------   
		//�����û�id�������������ܲ�����ֻ�ܹۿ�
		btnlogin.addActionListener(loginlistener);
		login.add(inputID);
		login.add(btnlogin);
		login.setVisible(true);
        //-----------------colorBar---------------   
		//��ɫѡ���
        Color colorArray[] = new Color[]{Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.GREEN, 
        		Color.LIGHT_GRAY, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.YELLOW};  
        colorBar.setPreferredSize(new Dimension(180,60));  
        //���ò�������Ϊ2��6��
        colorBar.setLayout(new GridLayout(2, 6, 0, 0));  
        //����һ����ɫ��      
        for(int i = 0; i < colorArray.length; i++){  
            JButton colorbtn = new JButton();  
            //���ð�ť����ɫ
            colorbtn.setBackground(colorArray[i]);  
            colorbtn.setPreferredSize(new Dimension(30,30));  
            colorBar.add(colorbtn);
            colorbtn.addActionListener(colorbtnlistener);  
        }
        //-----------------colorchooserbtn--------------- 
        //�Զ�����ɫѡ��򣬲���ͨ����ť��ɫ��ʾ��ǰ������ɫ
        colorchooserbtn.setBackground(Color.BLACK);
        colorchooserbtn.setPreferredSize(new Dimension(60,60)); 
        colorchooserbtn.addActionListener(colorlistener);
        //-----------------strokeBar---------------    
        //ѡ�񻭱ʵĴ�ϸ��ʹ�ð�ť��
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
        //ѡ�񻭱ʺ���Ƥ��ʵ�ַ�ʽ���ϸ��ѡ����ͬ
        pancel.setBackground(Color.LIGHT_GRAY);
        pancel.addActionListener(toollistener);
        pancel.setSelected(true);
        rubber.setBackground(Color.LIGHT_GRAY);
        rubber.addActionListener(toollistener);
        toolGroup.add(pancel);
        toolGroup.add(rubber);
        //-----------------toolBar---------------  
        //�Ϸ�������������
        //��ʾ��ǰ�滭�ߵ�id
        painterId.setFont(new Font("΢���ź�", Font.BOLD, 20));
        //��ʾ����ʱ
        timeCount.setFont(new Font("΢���ź�", Font.BOLD, 40));
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
        //�������İ�ť����������ʼ���˳���ť
        btnStart.addActionListener(startlistener);
        //ֻ�з������ܵ���ð�ť
        if(!owner)
        	btnStart.setEnabled(false);
        btnBack.addActionListener(backlistener);
        btnArea.add(btnStart);
        btnArea.add(btnBack);
        btnArea.setPreferredSize(new Dimension(100,75));
        btnArea.setBackground(Color.LIGHT_GRAY);
        btnArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //-----------------playerList---------------
        //��ʾ����б�
        playerList.setPreferredSize(new Dimension(100,800));
        playerList.setBackground(Color.LIGHT_GRAY);
        playerList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //-----------------scoreList---------------
        //��ʾ�����б�������б����
        scoreList.setPreferredSize(new Dimension(30,800));
        scoreList.setBackground(Color.LIGHT_GRAY);
        scoreList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //-----------------listArea---------------
        //�����������
        listArea.setLayout(new BorderLayout());
        listArea.add(playerList, BorderLayout.CENTER);
        listArea.add(scoreList, BorderLayout.EAST);
        listArea.add(btnArea, BorderLayout.NORTH);
        listArea.setPreferredSize(new Dimension(100,800));
        listArea.setBackground(Color.LIGHT_GRAY);
        listArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //-----------------textAnswer--------------- 
        //�ұ�������������ʾ��
        StyleConstants.setForeground(attrSystem, Color.RED);  
        StyleConstants.setForeground(attrPlayer, Color.BLACK); 
        textAnswer.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        textAnswer.setEditable(false);
        //-----------------textInput---------------
        //�ұ����������������
        textInput.setLineWrap(true);
        textInput.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        textInput.setWrapStyleWord(true);
        textInput.addKeyListener(keyListener);
        //-----------------talkArea---------------
        //�����ұ������
        talkArea.setLayout(new BorderLayout());
        talkArea.add(new JScrollPane(textAnswer), BorderLayout.CENTER);
        talkArea.add(new JScrollPane(textInput), BorderLayout.SOUTH);
        talkArea.setPreferredSize(new Dimension(100,800));
        talkArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //-----------------game---------------  
        //������Ϸ����
        paintArea.setBackground(Color.WHITE);
        game.setLayout(new BorderLayout());  
        game.add(paintArea, BorderLayout.CENTER);
        game.add(toolBar, BorderLayout.NORTH);
        game.add(listArea, BorderLayout.WEST);
        game.add(talkArea, BorderLayout.EAST);
        game.setBackground(Color.WHITE); 
        game.setVisible(true);
        //-----------------canvas---------------  
        //��������
        this.setLayout(new BorderLayout());  
        this.add(game, BorderLayout.CENTER);
        this.add(login, BorderLayout.SOUTH);
        this.setTitle("���� ��" + String.valueOf(roomId));  
        this.setSize(800,600);  
        this.setLocationRelativeTo(null);  
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);   
        this.setVisible(true);
        //�������ż���
        this.addComponentListener(new ComponentAdapter(){
        	@Override public void componentResized(ComponentEvent e){
        		//�ı仭����ͼ���С������Ӧ����
        		image.getScaledInstance(paintArea.getWidth(), paintArea.getHeight(), BufferedImage.SCALE_DEFAULT);
        		g = paintArea.getGraphics();
		        g.drawImage(image, 0, 0, paintArea.getWidth(), paintArea.getHeight(), paintArea);
        	}});
        //���ô��ڹرռ���
        this.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		//����Ƿ��������ڹرմ���ʱҲɾ������
        		if(owner) {
            		socketIO.sendExit();
                	closeRoom();
            	}
        		//�ر�socket����
        		socketIO.close();
                System.out.println("end");
        	}
		});
        //��ʼ������
        g = paintArea.getGraphics();
        image = new BufferedImage(canvas.paintArea.getWidth(), canvas.paintArea.getHeight(), BufferedImage.TYPE_INT_RGB);
        g2 = image.createGraphics();
        g2.fillRect(0, 0, canvas.paintArea.getWidth(), canvas.paintArea.getHeight());
        g2.setStroke(stroke);
        g2.setColor(color);
        //-----------------socket---------------  
        //����socket��Ϣ
        getsocket();
        //������Ȼֹͣ��Ҫִ�еĲ�������ǰ�����жϲ�������
        if(owner) {
    		socketIO.sendExit();
        	closeRoom();
    	}
        socketIO.close();
        //�������а�
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
        //ѭ�����ո�����Ϣ
        while(true) {
        	//����Ϸ���������˳�ѭ��
        	if(socketIO.getEnd())
        		break;
        	//��ȡ��ǰ�滭�ߣ����жϻ滭���Ƿ��Ѿ��任
        	if(!painter.toString().equals(socketIO.getPainter())) {
	        	showOnce = true;
	        	painter = new StringBuffer(socketIO.getPainter());
        	}
        	//��ʾ��ǰ�滭��
        	painterId.setText("��ǰ�滭�ߣ�" + painter.toString());
        	//�жϵ�ǰ�û��Ƿ��ǻ滭��
        	if(id.toString().equals(painter.toString()))
        		begin = true;
        	else
        		begin = false;
        	//����ǰ�û����ǻ滭��
        	if(!begin) {
        		//-----------------xy---------------
        		//��ȡ�滭��Ϣ�����꣬���ʣ���ɫ������Ļ����ʾͼ��
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
		        //������տ�ʼ�����ȡ��ǰ�������ʾ����ʾ
		        if(showOnce && !socketIO.getTips().equals("null")) {
		        	g2.setColor(Color.WHITE);
		        	g2.fillRect(0, 0, canvas.paintArea.getWidth(), canvas.paintArea.getHeight());
		            insert("��ʾ��" + socketIO.getTips() + "\n", attrSystem);   
		        	showOnce = false;
		        }
        	}
        	//����ǰ�û�Ϊ�滭�ߣ���������ոտ�ʼ������ʾ��ǰ����
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
        			insert("���غ���Ŀ��" + socketIO.getQuestion() + "\n", attrSystem); 
        			showOnce = false;
    	        }
        	}
        	//-----------------id---------------
        	//��ȡ�û��б�
        	playerID = socketIO.getID();
	        playerList.setListData(playerID.toArray());
	        //-----------------score---------------
	        //��ȡ�����б�
        	score = socketIO.getScore();
	        scoreList.setListData(score.toArray());
	        //-----------------answer---------------
	        //��ȡ��������
	        answer = socketIO.getAnsewer();
	        for(int i = answer_count; i < answer.size(); i++)
	        	insert(answer.get(i) + "\n", attrPlayer);
	        answer_count = answer.size();
	        //-----------------answer---------------
	        //��ȡϵͳ��Ϣ
	        sysInfo = socketIO.getSysinfo();
	        for(int i = sysInfo_count; i < sysInfo.size(); i++)
	        	insert(sysInfo.get(i) + "\n", attrSystem);
	        sysInfo_count = sysInfo.size();
	        //-----------------time---------------
	        //��ȡ����ʱ
	        timeCount.setText(socketIO.getTime());
        }
	}
	
	//�������������ʽ��������ʾ���������
	public void insert(String str, SimpleAttributeSet attrSet){  
		Document doc = textAnswer.getDocument();
		try{   
            doc.insertString(doc.getLength(), str, attrSet);   
        }   
        catch(BadLocationException e){   
            System.out.println( "BadLocationException:" + e);   
        }
    }   
	
	//�رղ�ɾ������
	public void closeRoom() {
		//�����ٽ���һ��socket��Ŀ�����÷�������ѭ���ȴ���һ�λص��ȴ�ͷ�����жϲ��˳�ѭ��������������ͻ�һֱ�ȴ���socket������
		SocketIO socketIO = new SocketIO(roomId);
		socketIO.close();
		LoginSocketIO loginSocketIO = new LoginSocketIO();
		loginSocketIO.removeRoom(roomId);
		loginSocketIO.close();
	}
	
	//��������������س�������Ϣ
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
    
	//�˳���ť����������رմ���
	ActionListener backlistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //��ȡ�¼�Դ�������¼��Ķ���  
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
    
    //��ʼ��ť�����������ʼ��Ϸ
	ActionListener startlistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //��ȡ�¼�Դ�������¼��Ķ���  
            Object obj = e.getSource();  
            if(obj instanceof JButton){
            	socketIO.sendStart();
            	btnStart.setEnabled(false);
            	g2.setColor(Color.WHITE);
            	g2.fillRect(0, 0, canvas.paintArea.getWidth(), canvas.paintArea.getHeight());
            }
        }  
    };  
    
    //ȷ������id��ť���������û�����id֮����Ҫ�����ȷ�ϰ�ť�����֮�������Ϸ�������г�ʼ��
	ActionListener loginlistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //��ȡ�¼�Դ�������¼��Ķ���  
            Object obj = e.getSource();  
            if(obj instanceof JButton){  
            	if(!inputID.getText().isEmpty()) {
            		//��¼֮�����õ�¼id��Ϊ���ɼ�
	                game.setVisible(true);
	                login.setVisible(false);
	                canvas.repaint();
	                canvas.validate();
	                //-----------------paintArea---------------
	                //��ȡ��ǰ���������г�ʼ��
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
	                //������Ϣ��֪����������id���û��Ѽ�����Ϸ
	                socketIO.sendID(inputID.getText());
	                socketIO.sendColor(String.valueOf(color.getRGB()));
	                socketIO.sendStroke(String.valueOf(stroke.getLineWidth()));
	                inputID.setText("");
            	}
            }
        }  
    };  
    
    //�������ʺ���Ƥ���л�
    ActionListener toollistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //��ȡ�¼�Դ�������¼��Ķ���  
            Object obj = e.getSource();  
            if(obj instanceof JRadioButton){  
            	if(((JRadioButton)obj).getText().equals("����"))
            		color = colorchooserbtn.getBackground();
            	else if(((JRadioButton)obj).getText().equals("��Ƥ"))
            		color = Color.WHITE;
            }
        }  
    };  
    
    //��Ӱ�ť��������ȡ��ť�ı�����ɫ  
    ActionListener colorbtnlistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //��ȡ�¼�Դ�������¼��Ķ���  
            Object obj = e.getSource();  
            if(obj instanceof JButton){  
                color=((JButton) obj).getBackground();  
                colorchooserbtn.setBackground(color);
            }
        }  
    };  
    
    //�����Զ�����ɫ��ť��������ɫѡ�������
    ActionListener colorlistener = new ActionListener(){   
        public void actionPerformed(ActionEvent e){  
                colorselector = JColorChooser.showDialog(null, "��ɫѡ����", Color.BLACK);  
                color = colorselector;
                colorchooserbtn.setBackground(colorselector);;  
        }  
    };  
    
    //�������ʴ�ϸѡ��ť
    ActionListener strokelistener = new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            //��ȡ�¼�Դ�������¼��Ķ���  
            Object obj = e.getSource();  
            if(obj instanceof JRadioButton){  
	    		stroke = new BasicStroke(Integer.valueOf(((JRadioButton)obj).getText()) * 2 + 1);
	            socketIO.sendStroke(String.valueOf(stroke.getLineWidth()));
            }
        }  
    };  
}
