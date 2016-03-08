
package examplejdbc;

import java.sql.*;
import oracle.jdbc.pool.OracleDataSource;
import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

class ExampleJDBC {
    
    private static Connection conn = null;
    private static Statement stmt = null;
    private static String query;
    private static PreparedStatement pstmt = null;
    private static Scanner scan = new Scanner(System.in);
    private static ResultSet rset = null;
    
  public static void main(String args[]) throws SQLException, IOException {
      
      
      try {
          connectDB();
          createTable();
          insertTable();
          query = "SELECT * FROM beer";
          rset = stmt.executeQuery(query);
          System.out.println("------------- TABLE beer CONTENTS -------------");
          while (rset.next()) {
              int beerId = rset.getInt("beer_id");
              String beerName = rset.getString("beer_name");
              String beerStyle = rset.getString("beer_style");
              System.out.print("beer_id: " + beerId);
              System.out.print(", beer_name: " + beerName);
              System.out.println(", beer_style: " + beerStyle);
          }
          System.out.println("-----------------------------------------------");
      } catch(SQLException ex) {
          System.out.println(ex);
      } finally {
          //closeConn();
      }    
          
        JOptionPane.showMessageDialog(null,"JDBC Showing Query Options");
        int choice = -1;	
	do{
            choice = getChoice();
            if (choice != 0){
                getSelected(choice);
            }
	} while ( choice !=  0);
            closeConn();System.exit(0);
            
     ;
                        
    }

	public static int getChoice()
	{
		String choice;
		int ch;
		choice = JOptionPane.showInputDialog(null,
			"1. Insert a Record\n"+
			"2. Update a Record\n"+
			"3. Delete a Record\n"+
			"4. Execute Custom Query\n"+
			
			"0. Exit\n\n"+
			"Enter your choice");
		ch = Integer.parseInt(choice);
		return ch;

	}

	public static void getSelected(int choice) throws IOException, SQLException{
		if(choice==1){
			insertRecord();
		}
		if(choice==2){
			updateRecord();
		}
		if(choice==3){
			deleteRecord();
		}
		if(choice==4){
			queryRecord();
		}
		
	}  
      /*try {
          connectDB();
          createTable();
          insertTable();
          insertRecord();
          updateRecord();
          deleteRecord();
          queryRecord();
          closeConn();
      } catch (IOException ex) {
          Logger.getLogger(ExampleJDBC.class.getName()).log(Level.SEVERE, null, ex);
      } */
  
  public static void connectDB() throws SQLException, IOException {
    
    System.out.println("Please enter information to connect to the database");
    String user;
    String password;
    String database;

    user = readEntry("user: ");
    int slash_index = user.indexOf('/');
    if (slash_index != -1)
    {
      password = user.substring(slash_index + 1);
      user = user.substring(0, slash_index);
    }
    else
      password = readEntry("password: ");
    database = readEntry("database(a TNSNAME entry): ");

    System.out.print("Connecting to the database...");
    System.out.flush();
    System.out.println("Connecting...");
    // Open an OracleDataSource and get a connection
    OracleDataSource ods = new OracleDataSource();
    ods.setURL("jdbc:oracle:oci:@" + database);
    ods.setUser(user);
    ods.setPassword(password);
    conn = ods.getConnection();
    System.out.println("connected.");  
}
  
  public static void createTable() {
      
      query = "CREATE TABLE beer (beer_id NUMBER, beer_name VARCHAR2(20), "
              + "beer_style VARCHAR2(20))";
      String drop = "DROP TABLE beer";
        try {
            stmt = conn.createStatement();
            stmt.executeQuery(drop);
            stmt.executeQuery(query);
            System.out.println("Query: createTable Executed");
        } catch (SQLException ex) {
            Logger.getLogger(ExampleJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
  }
  
  public static void insertTable() {
      query = "INSERT INTO beer "
              + "VALUES ("
              + "1, 'Motif', 'Dark Belgian')";
      String query2;
      query2 = "INSERT INTO beer "
              + "VALUES ("
              + "2, 'Skully Barrel', 'Coconut Sour')";
        try {
            stmt = conn.createStatement();
            stmt.executeQuery(query);
            stmt.executeQuery(query2);
            System.out.println("INSERT statements executed");
        } catch (SQLException ex) {
            Logger.getLogger(ExampleJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
  }
  
  public static void insertRecord() throws SQLException, IOException {
      String table;
      int beerId = 0;
      String beerName;
      String beerStyle;
      try {
          while (beerId == 0) {
              System.out.print("Enter new beer_id:> ");
              beerId = scan.nextInt();
          }
            
          beerName = readEntry("Enter new beer_name: ");
          beerStyle = readEntry("Enter new beer_style: ");
          pstmt = conn.prepareStatement("insert into beer (beer_id, beer_name, "
                  + "beer_style) values (?, ?, ?)");
          pstmt.setInt(1, beerId);
          pstmt.setString(2, beerName);
          pstmt.setString(3, beerStyle);
          pstmt.execute();
      } finally {
          pstmt.close();
      }
  }
  
  public static void updateRecord() throws SQLException {
      
      String newBeerName;
      String newBeerStyle;
      int newBeerId;
      
      try {
          stmt = conn.createStatement();
          rset = stmt.executeQuery("SELECT * FROM beer");
          //ResultSet rset2 = stmt.executeQuery("SELECT beer_name FROM beer");
          
          System.out.println("------- Current Beers --------");
          while (rset.next()) {
              int beerId = rset.getInt("beer_id");
              String beerName = rset.getString("beer_name");
              String beerStyle = rset.getString("beer_style");
              System.out.print("beer_id: " + beerId);
              System.out.print(", beer_name: " + beerName);
              System.out.println(", beer_style: " + beerStyle);
          }
          System.out.println("------------------------------");
          System.out.print("Enter beer_id of Beer you'd like to update:> ");
          newBeerId = scan.nextInt();
          newBeerName = readEntry("Enter new beer_name: ");
          newBeerStyle = readEntry("Enter new beer_style: ");
          query = "UPDATE beer SET beer_name='" + newBeerName + "', beer_style"
                  + "='" + newBeerStyle + "' WHERE beer_id=" + newBeerId;
          stmt.executeQuery(query);
          System.out.println("Update Executed");
          
      } catch (SQLException ex) {
            Logger.getLogger(ExampleJDBC.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        } finally {
          if (rset != null) rset.close();
          if (rset != null) stmt.close();
          
      }
      
  }
  
  public static void deleteRecord() {
      int delBeerId;
      try {
          stmt = conn.createStatement();
          rset = stmt.executeQuery("SELECT * FROM beer");
          System.out.println("------- Current Beers --------");
          while (rset.next()) {
              int beerId = rset.getInt("beer_id");
              String beerName = rset.getString("beer_name");
              String beerStyle = rset.getString("beer_style");
              System.out.print("beer_id: " + beerId);
              System.out.print(", beer_name: " + beerName);
              System.out.println(", beer_style: " + beerStyle);
          }
          System.out.println("------------------------------");
          System.out.print("Enter beer_id of Beer you'd like to delete:> ");
          delBeerId = scan.nextInt();
          query = "DELETE FROM beer WHERE beer_id=" + delBeerId;
          stmt.executeQuery(query);
      } catch (SQLException ex) {
            Logger.getLogger(ExampleJDBC.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
      } finally {
          try {
              if (rset != null) rset.close();
              if (rset != null) stmt.close();
          } catch (SQLException ex) {
              Logger.getLogger(ExampleJDBC.class.getName()).log(Level.SEVERE, null, ex);
          }
          
      }
  }
  
  public static void queryRecord() {
      try {
          stmt = conn.createStatement();
          rset = stmt.executeQuery("SELECT * FROM beer");
          System.out.println("------- Current Beers --------");
          while (rset.next()) {
              int beerId = rset.getInt("beer_id");
              String beerName = rset.getString("beer_name");
              String beerStyle = rset.getString("beer_style");
              System.out.print("beer_id: " + beerId);
              System.out.print(", beer_name: " + beerName);
              System.out.println(", beer_style: " + beerStyle);
          }
          System.out.println("------------------------------");
          System.out.println("Please enter query you'd like to execute");
          query = readEntry(": ");
          stmt.executeQuery(query);
      } catch (SQLException ex) {
          Logger.getLogger(ExampleJDBC.class.getName()).log(Level.SEVERE, null, ex);
          System.out.println(ex);
      } finally {
          try {
              if (rset != null) rset.close();
              if (rset != null) stmt.close();
          } catch (SQLException ex) {
              Logger.getLogger(ExampleJDBC.class.getName()).log(Level.SEVERE, null, ex);
          }
      }
  }
  
  public static void closeConn() {
        try {
            stmt.close();
            conn.close();
            System.out.println("DB Connections closed: Exiting");
        } catch (SQLException ex) {
            Logger.getLogger(ExampleJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
      
  }
  
  public static String wrapper() {
      String hello = "Hello from Java";
      try {
          
      
      
        try {
            connectDB();
        } catch (SQLException ex) {
            Logger.getLogger(ExampleJDBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExampleJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
      createTable();
      insertTable();
      closeConn();
      //insertRecord();
      } catch(NullPointerException ex) {
          System.out.println(ex);
      }
      return hello;
  }

  // Utility function to read a line from standard input
  static String readEntry(String prompt)
  {
    try
    {
      StringBuffer buffer = new StringBuffer();
      System.out.print(prompt);
      System.out.flush();
      int c = System.in.read();
      while (c != '\n' && c != -1)
      {
        buffer.append((char)c);
        c = System.in.read();
      }
      return buffer.toString().trim();
    }
    catch(IOException e)
    {
      return "";
    }
  }
}
