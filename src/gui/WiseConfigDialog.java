package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Random;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import util.WiseData;

import java.awt.Font;

@SuppressWarnings("serial")
public class WiseConfigDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textName;
	private JTextField textAddition;
	private JTextField textSubtractions;
	private JTextField textMultiplication;
	private JTextField textDivisions;
	private JTextField textFactor;

	@SuppressWarnings({ "unused", "rawtypes" })
	private Map wiseAgents;
	private JTextField textRestore;
	private JTextField textRounds;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		try {
			WiseConfigDialog dialog = new WiseConfigDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * Create the dialog.
	 */
	public WiseConfigDialog(final Map<String, WiseData> wiseAgents, final WiseData agentName) {

		this.wiseAgents = wiseAgents;
		setTitle("New Wise Agent");
		setBounds(100, 100, 487, 340);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblName = new JLabel("Name:");
			lblName.setBounds(31, 27, 46, 14);
			contentPanel.add(lblName);
		}

		textName = new JTextField("");
		textName.setBounds(93, 25, 188, 20);
		contentPanel.add(textName);
		textName.setColumns(10);

		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setBounds(31, 80, 188, 138);
		horizontalBox.setBorder(new TitledBorder(null, "Accuracy", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPanel.add(horizontalBox);

		JPanel panel = new JPanel();
		horizontalBox.add(panel);
		panel.setLayout(null);

		JLabel lblSums = new JLabel("Additions:");
		lblSums.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblSums.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSums.setBounds(22, 5, 77, 14);
		panel.add(lblSums);

		JLabel lblSubtaractions = new JLabel("Subtaractions:");
		lblSubtaractions.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblSubtaractions.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSubtaractions.setBounds(-49, 30, 148, 14);
		panel.add(lblSubtaractions);

		JLabel lblMultiplications = new JLabel("Multiplications:");
		lblMultiplications.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblMultiplications.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMultiplications.setBounds(-13, 55, 112, 14);
		panel.add(lblMultiplications);

		JLabel lblDivisions = new JLabel("Divisions:");
		lblDivisions.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblDivisions.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDivisions.setBounds(-13, 84, 112, 14);
		panel.add(lblDivisions);

		textAddition = new JTextField();
		textAddition.setHorizontalAlignment(SwingConstants.CENTER);
		textAddition.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				acceptOnlyNumbers(arg0);
			}

			/**
			 * @param arg0
			 */
		});
		textAddition.setBounds(105, 5, 46, 20);
		panel.add(textAddition);
		textAddition.setColumns(10);

		textSubtractions = new JTextField();
		textSubtractions.setHorizontalAlignment(SwingConstants.CENTER);
		textSubtractions.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				acceptOnlyNumbers(arg0);
			}
		});
		textSubtractions.setColumns(10);
		textSubtractions.setBounds(105, 30, 46, 20);
		panel.add(textSubtractions);

		textMultiplication = new JTextField();
		textMultiplication.setHorizontalAlignment(SwingConstants.CENTER);
		textMultiplication.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				acceptOnlyNumbers(e);
			}
		});
		textMultiplication.setColumns(10);
		textMultiplication.setBounds(105, 55, 46, 20);
		panel.add(textMultiplication);

		textDivisions = new JTextField();
		textDivisions.setHorizontalAlignment(SwingConstants.CENTER);
		textDivisions.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				acceptOnlyNumbers(e);
			}
		});
		textDivisions.setColumns(10);
		textDivisions.setBounds(105, 84, 46, 20);
		panel.add(textDivisions);

		Box horizontalBox_1 = Box.createHorizontalBox();
		horizontalBox_1.setBorder(new TitledBorder(null, "Tiredness", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		horizontalBox_1.setBounds(264, 80, 209, 138);
		contentPanel.add(horizontalBox_1);

		JPanel panel_1 = new JPanel();
		horizontalBox_1.add(panel_1);
		panel_1.setLayout(null);

		JLabel lblFactor = new JLabel("Tiredeness rate:");
		lblFactor.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblFactor.setBounds(10, 11, 129, 14);
		panel_1.add(lblFactor);

		textFactor = new JTextField();
		textFactor.setHorizontalAlignment(SwingConstants.CENTER);
		textFactor.setText("0");
		textFactor.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				acceptOnlyNumbers(e);
			}
		});
		textFactor.setColumns(10);
		textFactor.setBounds(141, 9, 46, 20);
		panel_1.add(textFactor);
		
		JLabel lblRestoreRate = new JLabel("Restore rate:");
		lblRestoreRate.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblRestoreRate.setBounds(10, 37, 102, 15);
		panel_1.add(lblRestoreRate);
		
		JLabel lblRoundsToRestore = new JLabel("Rounds to restore:");
		lblRoundsToRestore.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblRoundsToRestore.setBounds(10, 64, 129, 15);
		panel_1.add(lblRoundsToRestore);
		
		textRestore = new JTextField();
		textRestore.setText("0");
		textRestore.setHorizontalAlignment(SwingConstants.CENTER);
		textRestore.setColumns(10);
		textRestore.setBounds(141, 35, 46, 20);
		panel_1.add(textRestore);
		
		textRounds = new JTextField();
		textRounds.setText("0");
		textRounds.setHorizontalAlignment(SwingConstants.CENTER);
		textRounds.setColumns(10);
		textRounds.setBounds(141, 62, 46, 20);
		panel_1.add(textRounds);

		JButton btnRandom = new JButton("Random");
		btnRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Random r = new Random();
				textAddition.setText(Integer.toString(r.nextInt(90)));
				textSubtractions.setText(Integer.toString(r.nextInt(90)));
				textMultiplication.setText(Integer.toString(r.nextInt(90)));
				textDivisions.setText(Integer.toString(r.nextInt(90)));
			}
		});
		btnRandom.setBounds(164, 229, 117, 23);
		contentPanel.add(btnRandom);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Save");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if(!areAllFieldsFilled()) {
							JOptionPane.showMessageDialog(new JFrame(), "You must fill all fields", "ERROR",
							        JOptionPane.ERROR_MESSAGE);
							return;
						}
						String name = textName.getText();
						float addition = Float.parseFloat(textAddition.getText());
						float subtraction = Float.parseFloat(textSubtractions.getText()); 
						float divisions = Float.parseFloat(textDivisions.getText()); 
						float multiplications = Float.parseFloat(textMultiplication.getText());
						float tiredenessRate = Float.parseFloat(textFactor.getText());
						float restoreRate = Float.parseFloat(textRestore.getText());
						float rounds = Float.parseFloat(textRounds.getText());
						
						if(!(isPercentage(addition) && isPercentage(subtraction) && isPercentage(multiplications) 
								&& isPercentage(divisions))) {
							JOptionPane.showMessageDialog(new JFrame(), "Values must be between 0 and 100", "ERROR",
							        JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						wiseAgents.put(name, new WiseData(name, addition, subtraction, multiplications, divisions,restoreRate,tiredenessRate,rounds));
						agentName.setName(name);
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	private void acceptOnlyNumbers(KeyEvent arg0) {
		char c = arg0.getKeyChar();
		if (!((c >= '0') && (c <= '9') || (c == '.') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
			arg0.consume();
		}
	}
	
	private boolean isPercentage(float x) {
		return 0 <= x && x <= 100; 
	}
	
	private boolean areAllFieldsFilled() {
		return !(textAddition.getText().equals("") || textDivisions.getText().equals("")
				|| textFactor.getText().equals("") || textMultiplication.getText().equals("")
				|| textName.getText().equals("") || textRestore.getText().equals("")
				|| textRounds.getText().equals("") || textSubtractions.equals(""));
	}
}
