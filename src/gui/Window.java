package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import logic.Country;
import logic.World;

public class Window
{
	public JFrame frmRisk;
	private static JLabel turn = new JLabel();
	private static JLabel phase = new JLabel();
	private static ArrayList<Piece> pieces = new ArrayList<Piece>();

	/**
	 * Create the application.
	 */
	public Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		JPanel p = new JPanel();
        p.setLayout(null);
        
        //Wait for Agent Game
        while(pieces.size() < World.NR_OF_COUNTRIES){};

        for(int i=0;i<pieces.size();i++)
        	p.add(pieces.get(i).getCircle());
        
        JPanel phasePanel = new JPanel();
        phasePanel.setOpaque(false);
        phasePanel.setBounds(660, 650, 170, 50);
        phase.setForeground(Color.black);
        phase.setText("<html><h2>Phase: </h2></html>");
        phasePanel.add(phase);
        p.add(phasePanel);
        
        JPanel turnPanel = new JPanel();
        turnPanel.setOpaque(false);
        turnPanel.setBounds(660, 600, 170, 50);
        turn.setVerticalAlignment(SwingConstants.TOP);
        turn.setForeground(Color.black);
        turn.setText("<html><h2>Turn: </h2></html>");
        turnPanel.add(turn);
        p.add(turnPanel);
        
        JLabel backgroundImage = new JLabel();
        backgroundImage.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundImage.setIcon(new ImageIcon(Window.class.getResource("/gui/backGround.jpg")));
        backgroundImage.setOpaque(true);
        backgroundImage.setBounds(0, 0, 1024, 717);
        p.add(backgroundImage);
        
		frmRisk = new JFrame();
		frmRisk.setResizable(false);
		frmRisk.setTitle("RISK");
		frmRisk.setBounds(100, 100, 1024, 745);
		frmRisk.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmRisk.setContentPane(p);
		frmRisk.setLocationRelativeTo(null);
	}
	
	public static void updateBoard(ArrayList<Country> countries)
	{
		for(int i=0;i<countries.size();i++)
		{
			for(int j=0;j<pieces.size();j++)
			{
				if(pieces.get(j).getCountry().equals(countries.get(i).getName()))
				{
					pieces.get(j).setText(String.valueOf(countries.get(i).getTroops()));
					pieces.get(j).setColor(countries.get(i).getOwner().getColor());
				}
			}
		}
	}
	
	public static void createPieces(ArrayList<Country> countries)
	{
		for(int i=0;i<countries.size();i++)
		{
			pieces.add(new Piece(countries.get(i).getName(), 
					countries.get(i).getCoord().getX(), 
					countries.get(i).getCoord().getY(), 
					Color.black));
		}
	}
	
	public static void setTurn(String text)
	{
		turn.setText("<html><h2>Turn: "+text+"</h2></html>");
	}
	
	public static void setPhase(String text)
	{
		phase.setText("<html><h2>Phase: "+text+"</h2></html>");
	}
	
	static class CirclePanel extends JPanel
	{
		private static final long serialVersionUID = 2103587008487753156L;

		protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            int x = 10;
            int y = 10;
            int width = getWidth() - 20;
            int height = getHeight() - 20;
            g.fillArc(x, y, width, height, 0, 360);
        }
    }
}
