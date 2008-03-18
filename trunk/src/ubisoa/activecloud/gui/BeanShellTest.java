package ubisoa.activecloud.gui;

import javax.swing.SwingUtilities;

import bsh.Console;

public class BeanShellTest{
	
	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				Console.main(new String[]{""});	
			}
		});
	}
}
