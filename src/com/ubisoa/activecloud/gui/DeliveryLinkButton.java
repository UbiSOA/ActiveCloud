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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.swingx.JXButton;

public class DeliveryLinkButton extends JXButton{
	private static Logger log = Logger.getLogger(DeliveryLinkButton.class);
	protected String message = "Drop Capsule Here";
	private static final float lineWidth = 5.0f;
	private static Color lineColor = Color.GRAY.brighter();
	private static final float[] dashs = {10f};
	private static final float mitterLimit = 1.0f;
	private static final float dashOffset = 0.0f;
	private static final Color backgroundColor = new Color(0,0,0,220);
	private float alpha = 1.0f;
	
	public DeliveryLinkButton(String message){
		super();
		setBorder(null);
		setOpaque(false);
		this.message = message;
	}
	
	@Override
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g.create();
		
		//Paint square centered
        Point center = new Point(getWidth()/2, getHeight()/2);
        //topleft corner of the square
        Point topleft = new Point(center.x - (getWidth()/2), 
        		center.y - (getHeight()/2));
        
		//Paint the background
		//Smooth crimin... I mean, lines
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);

		//The background
		g2.setColor(backgroundColor);
        g2.fillRoundRect(topleft.x + 3,topleft.y + 3,
        		getWidth() - 6, getHeight() - 6, 10, 10);
        
        //The border
		g2.setStroke(new BasicStroke(3f));
		g2.setColor(lineColor);
		g2.drawRoundRect(topleft.x + 3,topleft.y + 3,
        		getWidth() - 6, getHeight() - 6, 10, 10);
		
        //Dashed stroke
        g2.setColor(lineColor);
        g2.setStroke(new BasicStroke(lineWidth,BasicStroke.CAP_ROUND, 
        		BasicStroke.JOIN_ROUND,mitterLimit,dashs,dashOffset));
        //Draw square
        Dimension squareDimension = new Dimension(getWidth() / 2, getHeight() / 2);
        g2.drawRoundRect(center.x - (squareDimension.width / 2), 
        		center.y - (squareDimension.height / 2), squareDimension.width, 
        		squareDimension.height, 10, 10);
        
        //Draw message
        g2.setColor(g2.getColor().darker());
        g2.setFont(g.getFont().deriveFont(Font.PLAIN, 18f));
        FontMetrics fm = g2.getFontMetrics();
        //Calculate messages's length
        int messageWidth = fm.stringWidth(message);
        int messageHeight = fm.getHeight();
        g2.drawString(message, center.x - (messageWidth / 2), 
        		center.y + (squareDimension.height / 2) + messageHeight);
        
        g2.dispose();
	}
	
	public void paintOk(){
		DeliveryLinkButton.lineColor = Color.GREEN.darker();
		repaint();
	}
	
	public void paintNormal(){
		DeliveryLinkButton.lineColor = Color.GRAY.brighter();
		repaint();
	}
	
	public void paintNotOk(){
		DeliveryLinkButton.lineColor = Color.RED.darker();
		repaint();
	}
	
	public float getAlpha(){
		return alpha;
	}
	
	public void setAlpha(float alpha){
		this.alpha = alpha;
		repaint();
	}
	
	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.add(new DeliveryLinkButton("Hola"));
				f.setPreferredSize(new Dimension(400,300));
				f.pack();
				f.setVisible(true);
			}
		});
	}
}
