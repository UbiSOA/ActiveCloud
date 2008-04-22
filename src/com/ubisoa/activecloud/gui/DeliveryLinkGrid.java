package com.ubisoa.activecloud.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;

public class DeliveryLinkGrid extends JXPanel{
	private GridLayout layout;
	
	public DeliveryLinkGrid(){
		layout = new GridLayout(0,3);
		for(int i=0;i<2;i++){
			addAnotherRow();
		}
		setLayout(layout);
		setBackgroundPainter(Painters.matteDark());
	}
	
	protected void addAnotherRow(){
		layout.setRows(layout.getRows()+1);
		for(int i=0;i<3;i++){
			DeliveryLinkPanel dlp = new DeliveryLinkPanel();
			dlp.addMouseListener(new MouseListener(){
				public void mouseClicked(MouseEvent arg0) {
					//addAnotherRow();
				}

				public void mouseEntered(MouseEvent arg0) {}

				public void mouseExited(MouseEvent arg0) {}

				public void mousePressed(MouseEvent arg0) {}

				public void mouseReleased(MouseEvent arg0) {}
				
			});
			add(dlp);
		}
		revalidate();
	}
	
	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JScrollPane scroll = new JScrollPane(new DeliveryLinkGrid());
				f.add(scroll);
				f.setPreferredSize(new Dimension(400,300));
				f.pack();
				f.setVisible(true);
			}
		});
	}
}
