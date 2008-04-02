package com.ubisoa.activecloud.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXFrame;

public class AcordionTest{
	
	private JXCollapsiblePane cp;
	
	public AcordionTest(){
		initComponents();
	}
	
	private void initComponents(){
		 cp = new JXCollapsiblePane();
		 cp.setLayout(new BorderLayout());
		 
		 JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		 controls.add(new JLabel("Search:"));
		 controls.add(new JTextField(10));
		 controls.add(new JButton("Refresh"));
		 controls.setBorder(new TitledBorder("Filters"));
		 cp.add("Center", controls);
		 
		 JXFrame frame = new JXFrame();
		 frame.setLayout(new BorderLayout());

		 // Put the "Controls" first
		 frame.add("North", cp);

		 // Then the tree - we assume the Controls would somehow filter the tree
		 JScrollPane scroll = new JScrollPane(new JTree());
		 frame.add("Center", scroll);

		 // Show/hide the "Controls"
		 JButton toggle = new JButton(cp.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
		 toggle.setText("Show/Hide Search Panel");
		 frame.add("South", toggle);

		 frame.pack();
		 frame.setVisible(true);
	}
	
	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new AcordionTest();
			}
		});
	}

}
