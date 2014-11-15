package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import java.awt.Insets;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.GridLayout;
import javax.swing.DefaultComboBoxModel;

public class Launcher extends JDialog {

	private static final long serialVersionUID = 3873885555141009503L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public Launcher() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 1, 0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			{
				JLabel lblNumerOfPlayers = new JLabel("Numer of Players");
				panel.add(lblNumerOfPlayers);
			}
			{
				JComboBox comboBox = new JComboBox();
				comboBox.setModel(new DefaultComboBoxModel(new String[] {"2", "3", "4", "5", "6"}));
				panel.add(comboBox);
			}
		}
		{
			JPanel panel = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panel.getLayout();
			contentPanel.add(panel);
			{
				JLabel lblPlayer_1 = new JLabel("Player 1");
				panel.add(lblPlayer_1);
			}
			{
				JComboBox comboBox = new JComboBox();
				panel.add(comboBox);
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
				JComboBox comboBox = new JComboBox();
				panel.add(comboBox);
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
				JComboBox comboBox = new JComboBox();
				panel.add(comboBox);
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
				JComboBox comboBox = new JComboBox();
				panel.add(comboBox);
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
				JComboBox comboBox = new JComboBox();
				panel.add(comboBox);
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
				JComboBox comboBox = new JComboBox();
				panel.add(comboBox);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
