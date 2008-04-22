package com.ubisoa.activecloud.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;

public class LineSquarePanel extends JXPanel{
	private static final Dimension squareSize = new Dimension(140,140);
	private static final int panelOffset= 80;
	private static final float lineWidth = 5.0f;
	private static Color lineColor = Color.GRAY.brighter();
	private static final float[] dashs = {10f};
	private static final float mitterLimit = 1.0f;
	private static final float dashOffset = 0.0f;
	protected String message = "Drop Capsule Here";
	private static final Color backgroundColor = new Color(0,0,0,220);
	boolean withArrow = false;
	
	public LineSquarePanel(boolean withArrow, String message){
		setOpaque(false);
		this.message = message;
		this.withArrow = withArrow;
	}
	
	@Override
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D)g.create();
        
		//Paint square centered
        Point center = new Point(getWidth()/2, getHeight()/2);
        //topleft corner of the square
        Point topleft = new Point(center.x - (squareSize.width/2), 
        		center.y - (squareSize.height/2));
        
		//Paint the background
		//Smooth crimin... I mean, lines
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setColor(backgroundColor);
        g2d.fillRoundRect(topleft.x - 30,topleft.y - 
        		30,squareSize.width + 60, squareSize.height + 60, 10, 10);
		g2d.setStroke(new BasicStroke(3f));
		g2d.setColor(lineColor);
		g2d.drawRoundRect(topleft.x - 30,topleft.y - 
				30,squareSize.width + 60, squareSize.height + 60, 10, 10);

        //Dashed stroke
        g2d.setColor(lineColor);
        g2d.setStroke(new BasicStroke(lineWidth,BasicStroke.CAP_ROUND, 
        		BasicStroke.JOIN_ROUND,mitterLimit,dashs,dashOffset));
        //Draw square
        g2d.drawRoundRect(topleft.x, topleft.y, squareSize.width, 
        		squareSize.height, 10, 10);
        
        if(withArrow){
            //Draw arrow
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, 
            		BasicStroke.JOIN_MITER));
            //The square part
            g2d.drawLine(topleft.x + 60, topleft.y + 20, topleft.x + 
            		squareSize.width - 120 + 60, topleft.y + 20);
            g2d.drawLine(topleft.x + 60, topleft.y + 20, topleft.x + 
            		60, topleft.y + squareSize.height - 80);
            g2d.drawLine(topleft.x + squareSize.width - 120 + 60, topleft.y + 
            		20, topleft.x + squareSize.width - 120 + 60, topleft.y + 
            		squareSize.height - 80);
            //The triangle part
            g2d.drawLine(topleft.x + 40, topleft.y + squareSize.height - 80, 
            		topleft.x + 60, topleft.y+squareSize.height - 80);
            g2d.drawLine(topleft.x + squareSize.width - 120 + 80, topleft.y + 
            		squareSize.height - 80, topleft.x + squareSize.width - 120 + 60, 
            		topleft.y+squareSize.height - 80);
            g2d.drawLine(topleft.x + 40, topleft.y + squareSize.height - 80, 
            		center.x, topleft.y + squareSize.height -40);
            g2d.drawLine(topleft.x + squareSize.width - 120 + 80, topleft.y + 
            		squareSize.height - 80, center.x, topleft.y + 
            		squareSize.height -40);	
        }
        
        //Draw message
        g2d.setColor(g2d.getColor().darker());
        g2d.setFont(g.getFont().deriveFont(Font.PLAIN, 18f));
        FontMetrics fm = g2d.getFontMetrics();
        //Calculate messages's length
        int messageWidth = fm.stringWidth(message);
        int messageHeight = fm.getHeight();
        g2d.drawString(message, center.x - (messageWidth / 2), 
        		topleft.y + squareSize.height + messageHeight);
        
        g2d.dispose();
	}
	
	public void paintOk(){
		LineSquarePanel.lineColor = Color.GREEN.darker();
		repaint();
	}
	
	public void paintNormal(){
		LineSquarePanel.lineColor = Color.GRAY.brighter();
		repaint();
	}
	
	public void paintNotOk(){
		LineSquarePanel.lineColor = Color.RED.darker();
		repaint();
	}
	
	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.add(new LineSquarePanel(false, "Hola"));
				f.setPreferredSize(new Dimension(400,300));
				f.pack();
				f.setVisible(true);
			}
		});
	}
}