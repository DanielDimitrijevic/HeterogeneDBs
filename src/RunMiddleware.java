import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class RunMiddleware {
	private static final String JDBC_MysqlDRIVER = "com.mysql.jdbc.Driver";
	private static final String JDBC_PostgreDRIVER = "com.postgresql.jdbc.Driver";
	private static String mPassword = "";
	private static String mUsername = "";
	private static String mDatabase = "";
	private static String mLocation = "";
	private static String mUrl = "";

	private static String pDatabase = "";
	private static String pLocation = "";
	private static String pPassword = "";
	private static String pUsername = "";
	private static String pUrl = "";

	private static Connection connection;
	private static Statement statement;
	private static boolean mconnectedToDatabase;
	private static boolean pconnectedToDatabase;

	public static void main(String[] args) {
		Properties configFile = new Properties();
		try {
			configFile.load(new FileInputStream("configuration.conf"));

			mLocation = configFile.getProperty("mysql.location");
			mDatabase = configFile.getProperty("mysql.databse");
			mUsername = configFile.getProperty("mysql.dbuser");
			mPassword = configFile.getProperty("mysql.dbpassword");
			mUrl = "jdbc:mysql://" + mLocation + "/" + mDatabase;

			pLocation = configFile.getProperty("postgresql.location");
			pDatabase = configFile.getProperty("postgresql.databse");
			pUsername = configFile.getProperty("postgresql.dbuser");
			pPassword = configFile.getProperty("postgresql.dbpassword");
			pUrl = "jdbc:postgresql://" + pLocation + "/" + pDatabase;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Class.forName(JDBC_MysqlDRIVER);
			connection = DriverManager
					.getConnection(mUrl, mUsername, mPassword);

			statement = connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mconnectedToDatabase = true;
		pconnectedToDatabase = true;

		// setQuery(query);

	}

}