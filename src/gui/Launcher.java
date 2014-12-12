package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import logic.Platform;

/**
 * Nesta classe são criados os elementos da janela inicial.
 * 
 * Nesta janela poderá ser escolhido:
 * 	- Tempo de atraso entre movimentos (em milisegundos)
 * 	- Definir o modo com ou sem alianças
 * 	- O tipo de cada jogador, sendo que o número de jogadores é igual ao número
 * 		de jogadores com tipo definido 
 *  
 * @author João Ladeiras
 * @author Rui Lima
 * 
 */

public class Launcher extends JDialog {

	private static final long serialVersionUID = 3873885555141009503L;
	private final JPanel contentPanel = new JPanel();
	private JPanel buttonPane;
	private JButton okButton;
	private JButton cancelButton;
	private JComboBox<String> comboBoxDelay;
	private JComboBox<String> comboBoxAllies;
	private JComboBox<String> comboBox1;
	private JComboBox<String> comboBox2;
	private JComboBox<String> comboBox3;
	private JComboBox<String> comboBox4;
	private JComboBox<String> comboBox5;
	private JComboBox<String> comboBox6;
	
	public static String[] delayTimes = {"1000", "1", "100", "2000", "3000"};
	public static String[] allies = {"No", "Yes"};
	
	/**
	 * Criar a janela
	 */
	public Launcher()
	{
		setBounds(100, 100, 300, 400);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 1, 0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			{
				JLabel lblDelay = new JLabel("Delay (miliseconds)");
				panel.add(lblDelay);
			}
			{
				comboBoxDelay = new JComboBox<String>(delayTimes);
				panel.add(comboBoxDelay);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			{
				JLabel lblDelay = new JLabel("Play with Alliances");
				panel.add(lblDelay);
			}
			{
				comboBoxAllies = new JComboBox<String>(allies);
				panel.add(comboBoxAllies);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			{
				JLabel lblPlayer_1 = new JLabel("Player 1");
				panel.add(lblPlayer_1);
			}
			{
				comboBox1 = new JComboBox<String>(Platform.playerType);
				panel.add(comboBox1);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			{
				JLabel lblPlayer_2 = new JLabel("Player 2");
				panel.add(lblPlayer_2);
			}
			{
				comboBox2 = new JComboBox<String>(Platform.playerType);
				panel.add(comboBox2);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			{
				JLabel lblPlayer_3 = new JLabel("Player 3");
				panel.add(lblPlayer_3);
			}
			{
				comboBox3 = new JComboBox<String>(Platform.playerType);
				panel.add(comboBox3);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			{
				JLabel lblPlayer_4 = new JLabel("Player 4");
				panel.add(lblPlayer_4);
			}
			{
				comboBox4 = new JComboBox<String>(Platform.playerType);
				panel.add(comboBox4);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			{
				JLabel lblPlayer_5 = new JLabel("Player 5");
				panel.add(lblPlayer_5);
			}
			{
				comboBox5 = new JComboBox<String>(Platform.playerType);
				panel.add(comboBox5);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			{
				JLabel lblPlayer_6 = new JLabel("Player 6");
				panel.add(lblPlayer_6);
			}
			{
				comboBox6 = new JComboBox<String>(Platform.playerType);
				panel.add(comboBox6);
			}
		}
		{
			buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		//Listeners
		okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		
		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
	}
	public JComboBox<String> getComboBox1() {
		return comboBox1;
	}
	public JComboBox<String> getComboBox2() {
		return comboBox2;
	}
	public JComboBox<String> getComboBox3() {
		return comboBox3;
	}
	public JComboBox<String> getComboBox4() {
		return comboBox4;
	}
	public JComboBox<String> getComboBox5() {
		return comboBox5;
	}
	public JComboBox<String> getComboBox6() {
		return comboBox6;
	}
	public JComboBox<String> getComboBoxDelay() {
		return comboBoxDelay;
	}
	public JComboBox<String> getComboBoxAllies() {
		return comboBoxAllies;
	}
}
