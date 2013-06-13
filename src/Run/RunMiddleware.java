package Run;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import Connection.MySQLcon;
import Connection.PostgreSQLcon;

/**
 * 
 * @author AHMED ALY, Daniel Dimitrijevic
 * 
 */
public class RunMiddleware {
	private MySQLcon mcon;
	private PostgreSQLcon pcon;

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

	public RunMiddleware() {
		mcon = new MySQLcon(mUrl, mDatabase, mUsername, mPassword, this);
		pcon = new PostgreSQLcon(pUrl, pDatabase, pUsername, pPassword, this);

		try {
			mcon.connect();
		} catch (ClassNotFoundException e) {
			System.err.println("The MySQL Driver could not be found.");
		} catch (SQLException e) {
			System.err.println("MySQL could not connect to the Database.");
		}

		try {
			pcon.connect();
		} catch (ClassNotFoundException e) {
			System.err.println("The PostgreSQL Driver could not be found.");
		} catch (SQLException e) {
			System.err.println("PostgreSQL could not connect to the Database.");
		}

	}

	/**
	 * This function is for compare the tables from MySQL and PostgrSQL. The
	 * first step is that we sort the two Collections to see if MySQL and
	 * PostgrSQL have the same tables. Then we check how many tables the both
	 * have.
	 * 
	 * @throws SQLException
	 */
	public void compareTabels() throws SQLException {
		ArrayList<String> mtables = mcon.getTables();
		ArrayList<String> ptables = pcon.getTables();
		Collections.sort(mtables);
		Collections.sort(ptables);

		for (int i = 0; i < mtables.size(); i++) {
			if (!mtables.get(i).toUpperCase()
					.equals(ptables.get(i).toUpperCase())) {
				boolean bool = false;

				for (int j = 0; j < ptables.size(); j++)
					if (mtables.get(i).toUpperCase()
							.equals(ptables.get(j).toUpperCase()))
						bool = true;
				if (!bool)
					pcon.setColumnMetaData(mcon.getColumnMetaData(mtables
							.get(i)));
			}
		}
		for (int i = 0; i < ptables.size(); i++) {
			if (!ptables.get(i).toUpperCase()
					.equals(mtables.get(i).toUpperCase())) {
				boolean bool = false;

				for (int j = 0; j < mtables.size(); j++)
					if (ptables.get(i).toUpperCase()
							.equals(mtables.get(j).toUpperCase()))
						bool = true;
				if (!bool)
					mcon.setColumnMetaData(pcon.getColumnMetaData(mtables
							.get(i)));
			}
		}

		for (int i = 0; i < mtables.size(); i++) {
			ResultSetMetaData mrmd = mcon.getMetaData(mtables.get(i));
			ResultSetMetaData prmd = pcon.getMetaData(ptables.get(i));
			if (mrmd.getColumnCount() == prmd.getColumnCount()) {
				for (int ii = 1; i < mrmd.getColumnCount(); i++) {
					if (!((mrmd.getColumnName(ii).equalsIgnoreCase(prmd
							.getColumnName(ii))) && (mrmd.getColumnType(ii) == prmd
							.getColumnType(ii)))) {
						System.err
								.println("The table information are not simular!\nProgramm will be closed!");
						System.exit(0);
					}
				}
			}
		}

		System.out.println("The table information are simular!\n");
		System.out.println("Now the data information will be checked.");
	}

	public void match(boolean mainmysql, boolean insert, String tablename)
			throws SQLException {
		ResultSetMetaData mrsm = mcon.getMetaData(tablename);
		ResultSetMetaData prsm = pcon.getMetaData(tablename);
	}

	public static void main(String[] args) {
		Properties configFile = new Properties();
		try {
			configFile.load(new FileInputStream("configuration.conf"));

			mLocation = configFile.getProperty("mysql.location");
			mDatabase = configFile.getProperty("mysql.database");
			mUsername = configFile.getProperty("mysql.dbuser");
			mPassword = configFile.getProperty("mysql.dbpassword");
			mUrl = "jdbc:mysql://" + mLocation;

			pLocation = configFile.getProperty("postgresql.location");
			pDatabase = configFile.getProperty("postgresql.database");
			pUsername = configFile.getProperty("postgresql.dbuser");
			pPassword = configFile.getProperty("postgresql.dbpassword");
			pUrl = "jdbc:postgresql://" + pLocation + ":5432";

			RunMiddleware rm = new RunMiddleware();
			try {
				rm.compareTabels();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			System.err.println("The File you want could not be found.");
		} catch (IOException e) {
			System.err
					.println("An error occurred when you try to read from the File.");
		}

	}

}
