package gui;

import java.awt.Color;

import gui.Window.CirclePanel;

import javax.swing.JLabel;

public class Piece
{
	private String country;
	private JLabel label;
	private Window.CirclePanel circle;
	
	public Piece(String country, int x, int y, Color color)
	{
		super();
		this.country = country;
		this.circle = new CirclePanel();
		this.circle.setForeground(color);
		this.circle.setOpaque(false);
		this.circle.setBounds(x, y, 50, 50);
        
		this.label = new JLabel();
		this.label.setForeground(Color.white);
		label.setText("<html><h3>NaN</h3></html>");
		this.circle.add(label);
	}

	public Window.CirclePanel getCircle() {
		return circle;
	}
	
	public void setText(String text)
	{
		label.setText("<html><h3>"+text+"</h3></html>");
	}

	public String getCountry() {
		return country;
	}
	
	public void setColor(Color color)
	{
		this.circle.setForeground(color);
	}
}
