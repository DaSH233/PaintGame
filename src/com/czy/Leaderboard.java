package com.czy;

import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.Templates;

//���а�
public class Leaderboard extends JFrame{

	JTable table = new JTable();
	String[] columnNames = {"����", "ID", "�÷�"};
	
	public Leaderboard(List<String> playerId, List<String> playerScore) {
		String temp;
		Object[][] cellData = new Object[playerId.size()][3];
		//���÷ֶ��û���������
		for(int i = 0; i < playerScore.size(); i++){
			for(int j = i; j < playerScore.size(); j++){
				if(Integer.valueOf(playerScore.get(i)).intValue() < Integer.valueOf(playerScore.get(j)).intValue()){
					temp = playerId.get(j);
					playerId.set(j, playerId.get(i));
					playerId.set(i, temp);
					temp = playerScore.get(j);
					playerScore.set(j, playerScore.get(i));
					playerScore.set(i, temp);
				}
			}
		}
		//���ñ������
		for(int i = 0; i < playerScore.size(); i++){
			cellData[i][0] = i + 1;
			cellData[i][1] = playerId.get(i);
			cellData[i][2] = playerScore.get(i);
		}
		DefaultTableModel model = new DefaultTableModel(cellData, columnNames) {
		  public boolean isCellEditable(int row, int column) {
		    return false;
		  }
		};
		table = new JTable(model);
		table.setRowHeight(30);
		table.setFont(new Font("΢���ź�", Font.BOLD, 20));
		JScrollPane new_table = new JScrollPane(table);
		this.add(new_table);
        this.setTitle("���а�");  
        this.setSize(300,400);  
        this.setLocationRelativeTo(null);  
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);   
        this.setVisible(true);
	}
}
