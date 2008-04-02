/*
 * BottomTabbedPanel.java
 *
 * Created on March 31, 2008, 12:03 PM
 */

package com.ubisoa.activecloud.gui;

import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;

import bsh.Interpreter;
import bsh.util.JConsole;

/**
 *
 * @author  cesar
 */
public class BottomTabbedPanel extends JXPanel {
    private Interpreter interpreter;

    public BottomTabbedPanel() {
        initComponents();
        interpreter = new Interpreter(scriptingEditor);
        SwingUtilities.invokeLater(new Runnable(){
        	public void run(){
        		new Thread(interpreter).start();	
        	}	
        });
    }
    
    public JXPanel getCapsuleConfigUI(){
    	return capsuleConfigUI;
    }
    
    public void setCapsuleConfigUI(JXPanel capsuleConfigUI){
    	this.capsuleConfigUI = capsuleConfigUI;
    }
    
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        tabPane = new javax.swing.JTabbedPane();
        outputScrollPane = new javax.swing.JScrollPane();
        outputEditor = new org.jdesktop.swingx.JXEditorPane();
        scriptingScrollPane = new javax.swing.JScrollPane();
        scriptingEditor = new JConsole();
        capsuleConfigScrollPane = new javax.swing.JScrollPane();
        capsuleConfigUI = new org.jdesktop.swingx.JXPanel();

        splitPane.setDividerLocation(200);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        outputScrollPane.setViewportView(outputEditor);

        tabPane.addTab("Output", outputScrollPane);

        scriptingScrollPane.setViewportView(scriptingEditor);

        tabPane.addTab("Scripting", scriptingScrollPane);

        splitPane.setBottomComponent(tabPane);

        javax.swing.GroupLayout capsuleConfigUILayout = new javax.swing.GroupLayout(capsuleConfigUI);
        capsuleConfigUI.setLayout(capsuleConfigUILayout);
        capsuleConfigUILayout.setHorizontalGroup(
            capsuleConfigUILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 395, Short.MAX_VALUE)
        );
        capsuleConfigUILayout.setVerticalGroup(
            capsuleConfigUILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 146, Short.MAX_VALUE)
        );

        capsuleConfigScrollPane.setViewportView(capsuleConfigUI);

        splitPane.setLeftComponent(capsuleConfigScrollPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }
    
    private javax.swing.JScrollPane capsuleConfigScrollPane;
    private org.jdesktop.swingx.JXPanel capsuleConfigUI;
    private org.jdesktop.swingx.JXEditorPane outputEditor;
    private javax.swing.JScrollPane outputScrollPane;
    private JConsole scriptingEditor;
    private javax.swing.JScrollPane scriptingScrollPane;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTabbedPane tabPane;
}