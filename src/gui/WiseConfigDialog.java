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

public class WiseConfigDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textName;
	private JTextField textAddition;
	private JTextField textSubtractions;
	private JTextField textMultiplication;
	private JTextField textDivisions;
	private JTextField textFactor;

	private Map wiseAgents;

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

		textName = new JTextField();
		textName.setBounds(77, 24, 188, 20);
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
		lblSums.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSums.setBounds(10, 11, 77, 14);
		panel.add(lblSums);

		JLabel lblSubtaractions = new JLabel("Subtaractions:");
		lblSubtaractions.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSubtaractions.setBounds(0, 36, 87, 14);
		panel.add(lblSubtaractions);

		JLabel lblMultiplications = new JLabel("Multiplications:");
		lblMultiplications.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMultiplications.setBounds(0, 61, 87, 14);
		panel.add(lblMultiplications);

		JLabel lblDivisions = new JLabel("Divisions:");
		lblDivisions.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDivisions.setBounds(0, 90, 87, 14);
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
		textAddition.setBounds(97, 8, 46, 20);
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
		textSubtractions.setBounds(97, 33, 46, 20);
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
		textMultiplication.setBounds(97, 58, 46, 20);
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
		textDivisions.setBounds(97, 87, 46, 20);
		panel.add(textDivisions);

		Box horizontalBox_1 = Box.createHorizontalBox();
		horizontalBox_1.setBorder(new TitledBorder(null, "Tiredness", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		horizontalBox_1.setBounds(264, 80, 182, 138);
		contentPanel.add(horizontalBox_1);

		JPanel panel_1 = new JPanel();
		horizontalBox_1.add(panel_1);
		panel_1.setLayout(null);

		JLabel lblFactor = new JLabel("Factor:");
		lblFactor.setBounds(10, 11, 46, 14);
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
		textFactor.setBounds(55, 8, 46, 20);
		panel_1.add(textFactor);

		JButton btnRandom = new JButton("Random");
		btnRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Random r = new Random();
				textAddition.setText(Integer.toString(r.nextInt(70)));
				textSubtractions.setText(Integer.toString(r.nextInt(70)));
				textMultiplication.setText(Integer.toString(r.nextInt(70)));
				textDivisions.setText(Integer.toString(r.nextInt(70)));
			}
		});
		btnRandom.setBounds(192, 229, 89, 23);
		contentPanel.add(btnRandom);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Save");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						String name = textName.getText();
						float addition = Float.parseFloat(textAddition.getText());
						float subtraction = Float.parseFloat(textSubtractions.getText()); 
						float divisions = Float.parseFloat(textDivisions.getText()); 
						float multiplications = Float.parseFloat(textMultiplication.getText());
						float factor = Float.parseFloat(textFactor.getText());
						
						if(!(isPercentage(addition) && isPercentage(subtraction) && isPercentage(multiplications) 
								&& isPercentage(divisions))) {
							JOptionPane.showMessageDialog(new JFrame(), "Values must be between 0 and 100", "ERROR",
							        JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						wiseAgents.put(name, new WiseData(name, addition, subtraction, multiplications, divisions,factor));
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
}
