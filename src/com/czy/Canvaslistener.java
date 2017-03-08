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

//画布监听类
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
	//鼠标按下  
	public void mousePressed(MouseEvent e){  
		//获取当前坐标
	    x1 = e.getX();  
	    y1 = e.getY();  
	    //发送画笔颜色信息
	    socketIO.sendColor(String.valueOf(canvas.color.getRGB()));
        g2.setStroke(canvas.stroke);
        g2.setColor(canvas.color);
	}  
	          
	//鼠标释放  
	public void mouseReleased(MouseEvent e){  
	    x2 = e.getX();  
	    y2 = e.getY();  
	}  
	          
	//鼠标移动
	public void mouseMoved(MouseEvent e){  
	}  
	          
	//鼠标拖动  
	public void mouseDragged(MouseEvent e){  
		//获取新的坐标
	    x2 = e.getX();  
	    y2 = e.getY();
	    //当前用户为绘画者才执行
	    if(canvas.begin) {
	    	//先发送坐标信息
		    socketIO.sendXY(x1, y1, x2, y2);
		    //在image上画出直线
		    g2.drawLine(x1, y1, x2, y2);
		    //在画布上画出image
		    g.drawImage(image, 0, 0, canvas.paintArea.getWidth(), canvas.paintArea.getHeight(), canvas.paintArea);
	    }
	    //重新当前获取坐标
	    x1 = x2;  
	    y1 = y2;  
	}  
}
