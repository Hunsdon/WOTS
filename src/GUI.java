import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class GUI extends JFrame {
	private JFrame mainFrame;
	private JLabel headerLabel;
	private JLabel statusLabel;
	private JPanel controlPanel;
	
	static WOTS wots = new WOTS();
	
	public GUI() {
		prepareGUI();
	}

	public static void main(String[] args) {
		GUI gui = new GUI();
		gui.showEvent1();
	}

	private void prepareGUI() {
		mainFrame = new JFrame("NB Garden's Warehouse");
		mainFrame.setSize(400, 210);
		mainFrame.setLayout(new GridLayout(3, 1));
		headerLabel = new JLabel("", JLabel.CENTER);
		statusLabel = new JLabel("", JLabel.CENTER);
		statusLabel.setSize(350, 100);

		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				System.exit(0);
			}
		});
		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());
		mainFrame.add(headerLabel);
		mainFrame.add(controlPanel);
		mainFrame.add(statusLabel);
		mainFrame.setVisible(true);
	}

	public void showEvent1() {
		prepareGUI();
		headerLabel.setText("Welcome");
		JButton logButton = new JButton("Login");
		JButton shutButton = new JButton("Shut Down");
	
		logButton.setActionCommand("Login");
		shutButton.setActionCommand("Shut Down");
	
		logButton.addActionListener(new BCL());
		shutButton.addActionListener(new BCL());

		controlPanel.add(logButton);
		controlPanel.add(shutButton);

		mainFrame.setVisible(true);
	}
	

	public void showEvent2(){
		prepareGUI();
	      headerLabel.setText("Input log in details."); 

	      JLabel  namelabel= new JLabel("User ID: ", JLabel.RIGHT);
	      JLabel  passwordLabel = new JLabel("Password: ", JLabel.CENTER);
	      final JTextField userText = new JTextField(6);
	      final JPasswordField passwordText = new JPasswordField(10);      

	      JButton loginButton = new JButton("Login");
	    

	      controlPanel.add(namelabel);
	      controlPanel.add(userText);
	      controlPanel.add(passwordLabel);       
	      controlPanel.add(passwordText);
	      controlPanel.add(loginButton);
	      mainFrame.setVisible(true); 
	      
	      loginButton.addActionListener(new ActionListener() {
	         public void actionPerformed(ActionEvent e) {     
	            String data = "Username " + userText.getText();
	            data += ", Password: " + new String(passwordText.getPassword()); 
	            statusLabel.setText(data); 
	            String userID = new String(userText.getText());
	            String password = new String(passwordText.getPassword());
	            int i = Integer.parseInt(userID);
	            wots.login(i,password);
	         }
	      });  
	   }
	
	public void showEvent3() {
		prepareGUI();
		headerLabel.setText("Main Menu");
		JButton button1 = new JButton("Customer Order");
		JButton button2 = new JButton("Stock Order");
		JButton button3 = new JButton("Damaged Stock");
		
		button1.setActionCommand("Customer");
		button2.setActionCommand("Stock");
		button3.setActionCommand("Damaged");
	
		button1.addActionListener(new BCL());
		button2.addActionListener(new BCL());
		button3.addActionListener(new BCL());

		controlPanel.add(button1);
		controlPanel.add(button2);
		controlPanel.add(button3);

		mainFrame.setVisible(true);
	}
	
	public void showEvent4(){
		prepareGUI();
	      headerLabel.setText("Input Order to process."); 

	      JLabel  orderLabel = new JLabel("Order ID: ", JLabel.CENTER);
	      final JTextField userText = new JTextField(6);
     

	      JButton enterButton = new JButton("Enter");
	    

	      controlPanel.add(orderLabel);
	      controlPanel.add(userText);
	      controlPanel.add(enterButton);
	      mainFrame.setVisible(true); 
	      
	      enterButton.addActionListener(new ActionListener() {
	         public void actionPerformed(ActionEvent e) {     
	            String userID = new String(userText.getText());
	            int i = Integer.parseInt(userID);
	            wots.update(i);
	         }
	      });  
	   }
	public class BCL implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
			String command = ae.getActionCommand();
			switch (command) {
			case "Login":
				showEvent2();
				break;
			case "Shut Down":
				statusLabel.setText("Shutting Down");
				System.exit(0);
				break;
			case "Customer":
				wots.printcOrders();
				break;
			case "Stock":
				wots.upstock();
				break;
			case "Damaged":
				wots.downstock();
				break;
			}
		}
	}

}
