package com.czy;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JLabel;

//����������
public class Canvaslistener extends MouseAdapter{
	private int x1,x2,y1,y2;  
	private Graphics g;
	private Graphics2D g2;
	private BufferedImage image;
	private Canvas canvas;
	private SocketIO socketIO;
	
	public Canvaslistener(Graphics g, Graphics2D g2, BufferedImage image, Canvas canvas, SocketIO socketIO){  
	        this.g = g;
	        this.canvas = canvas;
	        this.socketIO = socketIO;
	        this.g2 = g2;
	        this.image = image;
	}  
	//��갴��  
	public void mousePressed(MouseEvent e){  
		//��ȡ��ǰ����
	    x1 = e.getX();  
	    y1 = e.getY();  
	    //���ͻ�����ɫ��Ϣ
	    socketIO.sendColor(String.valueOf(canvas.color.getRGB()));
        g2.setStroke(canvas.stroke);
        g2.setColor(canvas.color);
	}  
	          
	//����ͷ�  
	public void mouseReleased(MouseEvent e){  
	    x2 = e.getX();  
	    y2 = e.getY();  
	}  
	          
	//����ƶ�
	public void mouseMoved(MouseEvent e){  
	}  
	          
	//����϶�  
	public void mouseDragged(MouseEvent e){  
		//��ȡ�µ�����
	    x2 = e.getX();  
	    y2 = e.getY();
	    //��ǰ�û�Ϊ�滭�߲�ִ��
	    if(canvas.begin) {
	    	//�ȷ���������Ϣ
		    socketIO.sendXY(x1, y1, x2, y2);
		    //��image�ϻ���ֱ��
		    g2.drawLine(x1, y1, x2, y2);
		    //�ڻ����ϻ���image
		    g.drawImage(image, 0, 0, canvas.paintArea.getWidth(), canvas.paintArea.getHeight(), canvas.paintArea);
	    }
	    //���µ�ǰ��ȡ����
	    x1 = x2;  
	    y1 = y2;  
	}  
}
