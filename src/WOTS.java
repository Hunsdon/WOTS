import java.sql.DriverManager;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class WOTS {
	private Scanner sc = new Scanner(System.in);	//sets up Scanner as sc for inputs

	private int assignedWorker;		//creates variable for worker ID 

	public void init() {
		System.out.println("\n1.Login\n2.Shut Down"); //print out options for menu
		while (!sc.hasNextInt()) { // while the next token is not an int 
			sc.next(); // just skip it
			System.out.println("Incorrect input, try again"); // asks for a second attempt at input
		}
		int temp = sc.nextInt(); //inputed option
		switch (temp) {  
		case 1:
			System.out.println("Input worker ID");
			while (!sc.hasNextInt()) { // while the next token is not an int
				sc.next(); // just skip it
				System.out.println("Incorrect input, try again");
			}
			int i = sc.nextInt(); //sets the worker ID
			String pass = sc.next(); //sets the password
			login(i, pass); //runs login method
			break;
		case 2:
			System.out.println("Shutting Down"); //stops program
			System.exit(0);
			break;
		default:
			System.out.println("Shutting Down"); //stops program
			System.exit(0);
			break;
		}
	}

	
	public void custOrStock() {
		System.out.println("\n1. Process Customer Order\n2. Process Stock Order\n3. Report Unsellable Stock"); //menu options
		while (!sc.hasNextInt()) { // while the next token is not an int
			sc.next(); // just skip it
			System.out.println("Incorrect input, try again");
		}
		int temp = sc.nextInt(); // scans input of option
		switch (temp) {
		case 1:
			update(1); // runs update method (for customer orders)
		case 2:
			upstock(); // runs upstock method (for stock orders)
			break;
		case 3:
			downstock(); // runs downstock method (for reducing stock if damaged)
			break;
		default:
			System.out.println("\nInvaild option. \nPlease login again.");

			init(); //starts program again
		}
	}
	

	public void login(int i, String pass) {
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");	//opens database connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.USER, DBConnection.PASS);
			assignedWorker = i; //sets current worker to worker ID 
			stmt = conn.createStatement();
			String sql2 = "SELECT password FROM workers WHERE workerID =" + assignedWorker; //command line for sql to select password
			ResultSet rs = stmt.executeQuery(sql2);
			while (rs.next()) {
				String password = rs.getString("password");
				System.out.println("Input password");		//checks password matches correct password 
				String wpass = pass;
				if (!wpass.equalsIgnoreCase(password)) {
					System.out.println("Incorrect password");
					Main.gui.showEvent2();
				} else {
					stmt = conn.createStatement();
					String sql3 = "UPDATE workers "	+ "SET loggedOn =1 WHERE workerID in ("	+ assignedWorker + ")";	//mark worker as online
					stmt.executeUpdate(sql3);
					System.out.println("workerID: " + assignedWorker + " Online");
					Main.gui.showEvent3();
				}
			}
			rs.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}	//close database connection
		}
	}

	public void update(int i) {
		Connection conn = null;
		Statement stmt = null;
		try {	//open database connection
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.USER, DBConnection.PASS);
			int oID = i;	//sets order from orderID ID
			stmt = conn.createStatement();
			String sql4 = "UPDATE customerorders " + "SET orderStatus = 'ASSIGNED' WHERE orderID =" + oID;	//assigns order to worker
			stmt.executeUpdate(sql4);
			System.out.println("Order ID: " + oID + " assigned to " + "worker " + assignedWorker);
			stmt = conn.createStatement();
			String prod = "SELECT productID, noOfProduct FROM orderlines WHERE orderID=" + oID; //prints each product on the order
			ResultSet rs = stmt.executeQuery(prod);
			while (rs.next()) {
				int pid = rs.getInt("productID");
				int quanity = rs.getInt("noOfProduct");
				System.out.print(" productID: " + pid + ", quanity: " + quanity);
				stmt = conn.createStatement();
				String sql5 = "SELECT forklift, location, size FROM products WHERE productID=" + pid;	
				ResultSet r1 = stmt.executeQuery(sql5);
				while (r1.next()) {
					int flift = r1.getInt("forklift");
					int location = r1.getInt("location");
					int size = r1.getInt("size");
					System.out.println(", location: " + location + ", size: " + size);
					if (flift == 1) {
						System.out.println(", forklift needed");
					} else {
						System.out.println(" ");
					}
				}
				r1.close();
			}
			rs.close();
			System.out
					.println("1. Order being picked\n2. Order being packed\n3. Order awaiting dispatch\n4. Order dispatched");
			while (!sc.hasNextInt()) { // while the next token is not an int
				sc.next(); // just skip it
				System.out.println("Incorrect input, try again");
			}
			int temp = sc.nextInt();
			stmt = conn.createStatement();
			switch (temp) {
			case 1://sets order to be picking, packing, awaiting dispatch, dispatched or pending
				String picking = "UPDATE customerorders " + "SET orderStatus = 'PICKING' WHERE orderID =" + oID; stmt.executeUpdate(picking);
				break;
			case 2:
				String packing = "UPDATE customerorders " + "SET orderStatus = 'PACKING' WHERE orderID =" + oID;
				stmt.executeUpdate(packing);
				break;
			case 3:
				String awaiting = "UPDATE customerorders " + "SET orderStatus = 'AWAITING_DISPATCH' WHERE orderID =" + oID;
				stmt.executeUpdate(awaiting);
				break;
			case 4:
				String dispatched = "UPDATE customerorders " + "SET orderStatus = 'DISPATCHED' WHERE orderID =" + oID;
				stmt.executeUpdate(dispatched);
				break;
			default:
				String pending = "UPDATE customerorders " + "SET orderStatus = 'PENDING' WHERE orderID =" + oID;
				stmt.executeUpdate(pending);
				break;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}		//closes database connection
		end();
	}

	public void printcOrders() {
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.USER, DBConnection.PASS);
			System.out.println("Select Order");
			stmt = conn.createStatement();
			String sql2 = "SELECT orderID, orderStatus FROM customerorders";
			ResultSet rs = stmt.executeQuery(sql2);
			while (rs.next()) {
				int id = rs.getInt("orderID");
				String status = rs.getString("orderStatus");
				String printed = "\norderID: " + id + ", Status: " + status;
				System.out.print("\n" + printed);
				stmt = conn.createStatement();
				String sql3 = "SELECT productID FROM orderlines WHERE orderID=" + id ;
				ResultSet r2 = stmt.executeQuery(sql3);
				int forkcount = 0;
				while (r2.next()) {
					int pid = r2.getInt("productID");
					stmt = conn.createStatement();
					String sql5 = "SELECT forklift FROM products WHERE productID=" + pid;
					ResultSet r1 = stmt.executeQuery(sql5);
					while (r1.next()) {
						int flift = r1.getInt("forklift");
						if ((flift == 1) && (forkcount == 0)) {
							System.out.print(", forklift needed");
							forkcount = 1;
						}
					}
					r1.close();
				}
				r2.close();
			}
			rs.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		Main.gui.showEvent4(); //run GUI for selecting order ID
	}

	public void upstock() {
		Connection conn = null;
		Statement stmt = null;
		int id = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DBConnection.DB_URL,
					DBConnection.USER, DBConnection.PASS);
			System.out.println("Input Stock Order ID");
			stmt = conn.createStatement();
			String sql2 = "SELECT stockID FROM stockorders";
			ResultSet rs = stmt.executeQuery(sql2);
			while (rs.next()) {
				id = rs.getInt("stockID");
				System.out.println("Stock Order ID:" + id);
			}
			rs.close();
			while (!sc.hasNextInt()) { // while the next token is not an int...
				sc.next(); // just skip it
				System.out.println("Incorrect input, try again");
			}
			id = sc.nextInt();
			System.out.println("1. Order Processed\n2. Order Incomplete");
			while (!sc.hasNextInt()) { // while the next token is not an int...
				sc.next(); // just skip it
				System.out.println("Incorrect input, try again");
			}
			int temp = sc.nextInt();
			stmt = conn.createStatement();
			switch (temp) {
			case 1:
				String sql4 = "UPDATE stockorders " + "SET orderStatus = 'PROCESSED' WHERE stockID =" + id;
				stmt.executeUpdate(sql4);
				break;
			case 2:
				String sql5 = "UPDATE stockorders "	+ "SET orderStatus = 'INCOMPLETE' WHERE stockID =" + id;
				stmt.executeUpdate(sql5);
				break;
			default:
				String pend = "UPDATE stockorders " + "SET orderStatus = 'PENDING' WHERE stockID =" + id;
				stmt.executeUpdate(pend);
				break;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}		//closes database connection
		}
		end();		//runs end method
	}

	public void downstock() {
		Connection conn = null;
		Statement stmt = null;
		int stock = 0, damaged = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DBConnection.DB_URL,
					DBConnection.USER, DBConnection.PASS);
			System.out.println("ProductID to be reduced");
			while (!sc.hasNextInt()) { // while the next token is not an int...
				sc.next(); // just skip it
				System.out.println("Incorrect input, try again");
			}
			int temp = sc.nextInt();		//accepts input for product ID of removed product
			stmt = conn.createStatement();
			String sql2 = "SELECT inStock FROM products WHERE productID ="
					+ temp;
			ResultSet rs = stmt.executeQuery(sql2);
			while (rs.next()) {
				stock = rs.getInt("inStock");
				System.out.println("In Stock:" + stock);
			}
			rs.close();
			System.out.println("Input number of damages");
			while (!sc.hasNextInt()) { // while the next token is not an int...
				sc.next(); // just skip it
				System.out.println("Incorrect input, try again");
			}
			damaged = sc.nextInt();		//accepts input for amount to reduce product by
			int newStock = stock - damaged;
			stmt = conn.createStatement();
			String sql3 = "UPDATE products " + "SET inStock =" + newStock + " WHERE productID in (" + temp + ")";	//updates database
			stmt.executeUpdate(sql3);
			System.out.println("Stock reduced");
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}			//closes database connection
		}
		end();	//runs end method
	}

	public void end() {
		System.out.println("Input accepted, continue to main menu?");
		String temp = sc.next();
		while (!temp.equalsIgnoreCase("yes") && (!temp.equalsIgnoreCase("no"))) {
			System.out
					.println("Invaild input, please try again (Input yes or no)");
			temp = sc.next();
		}
		if (temp.equalsIgnoreCase("yes")) {
			Main.gui.showEvent3();
		} else if (temp.equalsIgnoreCase("no")) {
			System.out.println("Login Off");
			Connection conn = null;
			Statement stmt = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.USER, DBConnection.PASS);
				stmt = conn.createStatement();
				String sql3 = "UPDATE workers " + "SET loggedOn =0 WHERE workerID in ("	+ assignedWorker + ")"; //marks worker as no longer online
				stmt.executeUpdate(sql3);
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (stmt != null)
						conn.close();
				} catch (SQLException se) {
				}
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
			Main.gui.showEvent1();
		}
	}
}