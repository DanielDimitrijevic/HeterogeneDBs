package DBConection;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.rowset.JdbcRowSet;

import Controller.Controller;


/**
 * Connection für eine MySQL Datenbank
 * @author Dominik Backhausen, Alexander Rieppel
 * @version 0.1
 */
public class MYSQLConection implements Conection{
	private String jdriv = "com.mysql.jdbc.Driver", url, uname , pwd, dbname;
	private ArrayList<String> tab = new ArrayList<String>();
	private ArrayList<Integer> tabc = new ArrayList<Integer>();
	
	private Connection con;
	private Statement st;
	private ResultSet rs;
	private Controller c;
	
	private boolean connected = false;
	/**
	 * Konstruktor zum Erstellen einer MySQL Connection
	 * @param url IP-Addresse oder Hostname des Datenbank Servers
	 * @param dbname Datenbank Name
	 * @param uname Benutzername zum Einloggen auf dem Server
	 * @param pwd Passwort zum Einloggen auf dem Server
	 * @param c Controller um mit der anderen Verbindung kommunizieren zu können
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public MYSQLConection(String url, String dbname, String uname, String pwd,Controller c) {
		this.url = "jdbc:mysql://" + url + "/" + dbname;
		this.uname = uname;
		this.pwd = pwd;
		this.dbname = dbname;
		this.c = c;
	}
	
	@Override
	public void connect()throws ClassNotFoundException, SQLException{
		System.out.println("Mysql Treiber wird geladen......");
		
		Class.forName(this.jdriv);
		
		System.out.println("Mysql wird verbunden......");
		con = DriverManager.getConnection(this.url , this.uname, this.pwd);
		
		st = con.createStatement( 
		         ResultSet.TYPE_SCROLL_SENSITIVE,
		         ResultSet.CONCUR_UPDATABLE);
		
		this.connected = true;
		
		System.out.println("Mysql conected!");
		System.out.println("Es wird Datenbank " + dbname + " verwendet!");
	}
	
	@Override
	public void setC(int id, int wert){
		tabc.set(id, wert);
	}
	@Override
	public ArrayList<String> getTables() throws SQLException{
		rs = st.executeQuery("show tables");
		while(rs.next()){
			tab.add(rs.getString(1));
		}
		return tab;
	}
	
	@Override
	public ResultSetMetaData getMeta(String tabn) throws SQLException{
		return st.executeQuery("SELECT * FROM " + tabn).getMetaData(); 
	}
	
	@Override
	public ResultSet exeQuarry(String q) throws SQLException {
		return st.executeQuery(q);
	}
	
	@Override
	public int exeUpdate(String q) throws SQLException{
		return st.executeUpdate(q);
	}
	
	@Override
	public void initTC() throws SQLException{
		for(int i =0;i < tab.size();i++){
			ResultSet rs = this.exeQuarry("SELECT count(*) FROM " + tab.get(i));
			rs.next();
			tabc.add(i, rs.getInt(1));
		}
	}
	
	@Override
	public void run() {
		System.out.println("Mysql Check gestartet!");
		while(!Thread.interrupted()){
			ResultSet rs1;
			try {
				for(int i  = 0; i < tab.size();i++){
					rs1 = this.exeQuarry("SELECT count(*) FROM " + tab.get(i));
					rs1.next();
					if(rs1.getInt(1) < tabc.get(i)){
						System.out.println("Hallo 3");
						tabc.set(i,rs1.getInt(1));
						c.setPOSTC(i,rs1.getInt(1));
						c.match(true, false, tab.get(i));
						
					}else if(rs1.getInt(1) > tabc.get(i)){
						System.out.println("Hallo 4");
						tabc.set(i,rs1.getInt(1));
						c.setPOSTC(i,rs1.getInt(1));
						c.match(true, true, tab.get(i));
					}
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println("ERROR 02: " + e.getMessage());
			} catch (SQLException e) {
				System.err.println("ERROR MYSQL CHECK:" + e.getMessage());
			}
		}
		
	}

	@Override
	public DatabaseMetaData getMe() throws SQLException {
		return con.getMetaData();
	}
	
}
