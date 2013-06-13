package DBConection;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import Controller.Controller;


/**
 * Connection für eine Postgres Datenbank
 * @author Dominik Backhasuen, Alexander Rieppel
 * @version 0.1
 */
public class POSTConection implements Conection{
	private String jdriv = "org.postgresql.Driver", url, uname , pwd, dbname;
	private ArrayList<String> tab = new ArrayList<String>();
	private ArrayList<Integer> tabc = new ArrayList<Integer>();
	
	private Connection con;
	private Statement st;
	private ResultSet rs;
	private Controller c;
	
	/**
	 * Konstruktor zum Erstellen einer Postgres Connection
	 * @param url IP-Addresse oder Hostname des Datenbank Servers
	 * @param dbname Datenbank Name
	 * @param uname Benutzername zum Einloggen auf dem Server
	 * @param pwd Passwort zum Einloggen auf dem Server
	 * @param c Controller um mit der anderen Verbindung kommunizieren zu können
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public POSTConection(String url, String dbname, String uname, String pwd,Controller c) throws ClassNotFoundException, SQLException{
		this.url = "jdbc:postgresql://" + url + "/" + dbname;
		this.uname = uname;
		this.pwd = pwd;
		this.dbname = dbname;
		this.c = c;
	}
	
	@Override
	public void connect()throws ClassNotFoundException, SQLException{
		System.out.println("Postgres Treiber wird geladen......");
		
		Class.forName(jdriv);
		
		System.out.println("Postgres wird verbunden......");
		con = DriverManager.getConnection(url , uname, pwd);
		
		st = con.createStatement( 
		         ResultSet.TYPE_SCROLL_INSENSITIVE,
		         ResultSet.CONCUR_READ_ONLY );
		
		
		System.out.println("Postgres conected!");
		System.out.println("Es wird Datenbank " + dbname + " verwendet!");
	}
	
	@Override
	public ArrayList<String> getTables() throws SQLException{
		rs = st.executeQuery("SELECT * FROM information_schema.tables WHERE table_schema = 'public';");
		while(rs.next()){
			tab.add(rs.getString(3));
		}
		return tab;
	}
	
	@Override
	public ResultSetMetaData getMeta(String tabn) throws SQLException{
		return st.executeQuery("SELECT * FROM " + tabn).getMetaData(); 
	}
	
	@Override
	public ResultSet exeQuarry(String q) throws SQLException{
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
	public DatabaseMetaData getMe() throws SQLException {
		return con.getMetaData();
	}
	
	@Override
	public void setC(int id, int wert){
		tabc.set(id, wert);
	}
	
	@Override
	public void run() {
		System.out.println("Postgres Check gestartet!");
		while(!Thread.interrupted()){
			ResultSet rs1;
			try {
				for(int i  = 0; i < tab.size();i++){
					rs1 = this.exeQuarry("SELECT count(*) FROM " + tab.get(i));
					rs1.next();
					if(rs1.getInt(1) < tabc.get(i)){
						System.out.println("Hallo 1");
						tabc.set(i,rs1.getInt(1));
						c.setMYSQLC(i,rs1.getInt(1));
						c.match(false, false, tab.get(i));
					}else if(rs1.getInt(1) > tabc.get(i)){
						System.out.println("Hallo 2");
						tabc.set(i,rs1.getInt(1));
						c.setMYSQLC(i,rs1.getInt(1));
						c.match(false, true, tab.get(i));
					}
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println("ERROR 02: " + e.getMessage());
			} catch (SQLException e) {
				//System.err.println("ERROR Postgre CHECK:" + e.getMessage());
				e.printStackTrace();
			}
		}
		
		
	}
	
}
