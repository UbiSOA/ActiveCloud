package com.ubisoa.activecloud.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class CapsuleConfigPanel extends JFrame{
	private ReflectionPanel reflectionPanel;
	
	public CapsuleConfigPanel(JPanel configPanel){
		super();
		setContentPane(new GradientPanel());
		getContentPane().setLayout(new GridBagLayout());
		add(buildReflectionPanel(configPanel), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(96, 96, 96, 96), 0, 0));
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}
	
	private JComponent buildReflectionPanel(JPanel panel){
		reflectionPanel = new ReflectionPanel();
		reflectionPanel.add(panel);
		return reflectionPanel;
	}
	
    private static class GradientPanel extends JPanel {
        GradientPanel() {
            super(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0.0f, getHeight() * 0.22f,
                                          new Color(0x202737),
                                          0.0f, getHeight() * 0.7f,
                                          Color.BLACK, true));
            Rectangle clip = g.getClipBounds();
            g2.fillRect(clip.x, clip.y, clip.width, clip.height);
            g2.dispose();
        }
    }

}
