package Connection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Run.RunMiddleware;

/**
 * This class is for the Connection for MySQl here and of course for getting the
 * Data for the Synchronising with PostgreSQL
 * 
 * @author AHMED ALY, Daniel Dimitrijevic
 * 
 */
public class MySQLcon {
	private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private String DATABASE_URL;
	private String USERNAME;
	private String PASSWORD;
	private String DATABASE;
	private ArrayList<String> tabeles = new ArrayList<String>();

	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private boolean connected;
	private RunMiddleware rm;

	/**
	 * This Constructor is just for the initialize here.
	 * 
	 * @param url
	 * @param dbname
	 * @param uname
	 * @param pwd
	 * @param rm
	 */
	public MySQLcon(String url, String dbname, String uname, String pwd,
			RunMiddleware rm) {
		this.DATABASE_URL = url + "/" + dbname;
		this.USERNAME = uname;
		this.PASSWORD = pwd;
		this.DATABASE = dbname;
		this.connected = false;
	}

	/**
	 * This function is for the connection with the DATABASE PostgreSQL
	 * 
	 * @throws ClassNotFoundException
	 *             if the class cannot be located
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public void connect() throws ClassNotFoundException, SQLException {
		System.out.println("MySQL Treiber is loading ...");

		Class.forName(JDBC_DRIVER);

		System.out.println("MySQL is connecting......");
		connection = DriverManager.getConnection(DATABASE_URL, USERNAME,
				PASSWORD);

		statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE);

		this.connected = true;

		System.out.println("MySQL is connected!");
		System.out.println("You are using the DATABASE " + DATABASE);
	}

	/**
	 * You use this function if you have a SHOW or a SELECT query
	 * 
	 * @param query
	 *            here you give the query
	 * @return a ResultSet
	 * @throws SQLException
	 *             if the SQL query is wrong
	 */
	public ResultSet executeQuery(String query) throws SQLException {
		return statement.executeQuery(query);
	}

	/**
	 * You use this function if you have a INSERT, UPDATE, DELETE, CREATE, DROP,
	 * ALTER or a GRANT query.
	 * 
	 * @param query
	 *            here you give the query
	 * @return either the row count for INSERT, UPDATE, DELETE, CREATE, DROP,
	 *         ALTER or a GRANT statements, or 0 for SQL statements that return
	 *         nothing
	 * @throws SQLException
	 *             if the SQL query is wrong
	 */
	public int executeUpdate(String query) throws SQLException {
		return statement.executeUpdate(query);
	}

	/**
	 * This function is for getting the MetaData from the table you give as a
	 * parameter
	 * 
	 * @param tabelname
	 *            the tabelname from you getting the MetaData
	 * @return a ResultSetMetaData
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public ResultSetMetaData getMetaData(String tablename) throws SQLException {
		return getDatabaseMetaData().getColumnPrivileges(null, null, tablename,
				null).getMetaData();

		// return statement.executeQuery("SELECT * FROM " + tablename)
		// .getMetaData();
	}

	/**
	 * This function is for getting the MetaData from the DATABSE you using
	 * 
	 * @return a DatabaseMetaData
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public DatabaseMetaData getDatabaseMetaData() throws SQLException {
		return connection.getMetaData();
	}

	/**
	 * This function is for returning the columns MetaData in a ResultSet
	 * 
	 * @param tablename
	 *            the table name you want the columns MetaData form
	 * @return a ResultSet with the columns MetaData
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public ResultSet getColumnMetaData(String tablename) throws SQLException {
		ResultSet columns = getDatabaseMetaData().getColumns(null, null,
				tablename, null);
		return columns;
	}

	/**
	 * This function is for creating a table that not exist
	 * 
	 * @param rs
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public void setColumnMetaData(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		String query = "CREATE TABLE " + rsmd.getTableName(1);

		for (int i = 1; i <= columnCount; i++) {
			if (i > 1)
				query += ", ";
			String columnName = rsmd.getColumnLabel(i);
			String columnType = rsmd.getColumnTypeName(i);

			query += columnName + " " + columnType;

			int precision = rsmd.getPrecision(i);
			if (precision != 0) {
				query += "( " + precision + " )";
			}
		}
		query += " ) ";
		statement.executeUpdate(query);
	}

	/**
	 * This function returns a ArrayList with the tables from the DATABASE you
	 * use.
	 * 
	 * @return a ArrayList with the tables
	 * @throws SQLException
	 *             if the query is wrong
	 */
	public ArrayList<String> getTables() throws SQLException {
		resultSet = statement.executeQuery("SHOW TABLES;");
		while (resultSet.next()) {
			tabeles.add(resultSet.getString(1));
		}
		return tabeles;
	}

	public void run() {
		System.out.println("MySQL started the checking!");
		while (!Thread.interrupted()) {
			try {

				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println("ERROR 02: " + e.getMessage());
			}
		}

	}

}
