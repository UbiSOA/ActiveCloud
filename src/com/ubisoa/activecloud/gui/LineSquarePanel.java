package com.ubisoa.activecloud.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;

public class LineSquarePanel extends JXPanel{
	private static final Dimension squareSize = new Dimension(140,140);
	private static final float lineWidth = 5.0f;
	private static final Color lineColor = Color.GRAY.brighter();
	private static final float[] dashs = {10f};
	private static final float mitterLimit = 1.0f;
	private static final float dashOffset = 0.0f;
	private static final String message = "Drop Capsule Here";
	private static final int messageYOffset = 20;
	private static final int messageXOffset = 10;
	private static final Color backgroundColor = new Color(0,0,0,220);
	
	public LineSquarePanel(){
		setOpaque(false);
	}
	
	@Override
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D)g.create();
		
		//Paint the background
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(backgroundColor);
		g2d.fillRoundRect(34,34,getWidth()-68,getHeight()-68,30,30);
        //Paint white border
		g2d.setStroke(new BasicStroke(3f));
		g2d.setColor(Color.WHITE);
		g2d.drawRoundRect(34, 34, getWidth()-68, getHeight()-68, 30, 30);
        //Paint square centered
        Point center = new Point(getWidth()/2, getHeight()/2);
        //topleft corner of the square
        Point topleft = new Point(center.x - (squareSize.width/2), 
        		center.y - (squareSize.height/2));
        //Smooth crimin... I mean, lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        //Dashed stroke
        g2d.setColor(lineColor);
        g2d.setStroke(new BasicStroke(lineWidth,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
        		mitterLimit,dashs,dashOffset));
        //Draw square
        g2d.drawRoundRect(topleft.x, topleft.y, squareSize.width, squareSize.height, 10, 10);
        //Draw message
        g2d.setColor(g2d.getColor().darker());
        g2d.setFont(g.getFont().deriveFont(Font.PLAIN, 18f));
        g2d.drawString(message, topleft.x - messageXOffset, 
        		topleft.y + squareSize.height + messageYOffset);
        g2d.dispose();
	}
	
	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.add(new LineSquarePanel());
				f.setPreferredSize(new Dimension(800,600));
				f.pack();
				f.setVisible(true);
			}
		});
	}
}
