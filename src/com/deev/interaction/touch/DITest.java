package com.deev.interaction.touch;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

public class DITest
{
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		DIModernPlaf.initModernLookAndFeel();
		
		JFrame frame = new JFrame("Testing DIButton...");
		frame.setBounds(10, 10, 300, 200);
		
		JPanel panel = new DIModernPanel();
		
		JButton button;
		
		button = new JButton("Test");
		panel.add(button);
		
		JToggleButton tbutton = new JToggleButton("Test");
		panel.add(tbutton);
		
		JLabel jlbl = new  JLabel("Un gros label bien gros");
		jlbl.setName("Bold");
		panel.add(jlbl);
		
		button = new JButton("Test");
		panel.add(button);
		
		button = new JButton("Test");
		button.setName("BlueButton");
		panel.add(button);
		
		panel.add(new JLabel("Un test"));
		


		String[] data = {"one", "two", "three", "four", "oone", "otwo", "othree", "ofour"};
		JComboBox combo = new JComboBox(data);
		panel.add(combo);
		
		frame.add(panel);
		
		frame.setVisible(true);
	}
}
