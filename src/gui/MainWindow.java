package gui;

import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JButton;

public class MainWindow {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		DefaultListModel listModel = new DefaultListModel();
		listModel.addElement("Jane Doe");
		listModel.addElement("John Smith");
		listModel.addElement("Kathy Green");
		
		JList list = new JList(listModel);
		list.setBorder(UIManager.getBorder("InternalFrame.border"));
		
		list.setBounds(36, 47, 133, 149);
		frame.getContentPane().add(list);
		
		JLabel lblWiseAgents = new JLabel("Wise Agents");
		lblWiseAgents.setBounds(36, 25, 110, 14);
		frame.getContentPane().add(lblWiseAgents);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.setBounds(191, 51, 89, 23);
		frame.getContentPane().add(btnAdd);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.setBounds(191, 85, 89, 23);
		frame.getContentPane().add(btnRemove);
		
		JButton btnStartSimulation = new JButton("Start simulation");
		btnStartSimulation.setBounds(282, 227, 142, 23);
		frame.getContentPane().add(btnStartSimulation);
		
		listModel.addElement("Kathy Green");
	}
}
