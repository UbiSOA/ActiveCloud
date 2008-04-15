package com.ubisoa.activecloud.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class InfoButton extends JButton implements MouseListener{
	private static final Dimension ovalSize = new Dimension(40,40);
	
	public InfoButton(){
		setOpaque(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
		addMouseListener(this);
	}
	
	@Override
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g.create();
		
//		g2.setColor(Color.BLUE);
//		g2.fillRect(0, 0, getWidth(), getHeight());
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.DARK_GRAY);
		
		//The main oval
		g2.fillOval(getWidth() - 3 - ovalSize.width, getHeight() - 3 - ovalSize.height, ovalSize.width, ovalSize.height);
		//The border
		//If the mouse is pressed, paint the border dark gray
		g2.setColor(getModel().isPressed() ? Color.DARK_GRAY : Color.WHITE);
		g2.setStroke(new BasicStroke(2f));
		g2.drawOval(getWidth() - 3 - ovalSize.width, getHeight() - 3 - ovalSize.height, ovalSize.width, ovalSize.height);
		
		//The Message
		g2.setColor(Color.WHITE);
		g2.setFont(getFont());
		FontMetrics fm = g2.getFontMetrics();
		int ascent = fm.getMaxAscent();
		int descent = fm.getMaxDescent();
		int msgX = getWidth() - 3 - (ovalSize.width / 2);
		int msgY = getHeight() - 3 - (ovalSize.height / 2) - (descent / 2) + (ascent / 2);
		g2.drawString("i",msgX, msgY);
		
		g2.dispose();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(50,50);
	}
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(50, 50);
	}

	
	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setLayout(new BorderLayout());
				InfoButton info = new InfoButton();
				info.setFont(new Font(Font.SERIF, Font.BOLD | Font.ITALIC, 12));
				f.setPreferredSize(new Dimension(130,600));
				final CapsulePanel cp = new CapsulePanel();
				f.add(cp, BorderLayout.CENTER);
				f.add(info, BorderLayout.SOUTH);
				f.pack();
				f.setVisible(true);
			}
		});
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
