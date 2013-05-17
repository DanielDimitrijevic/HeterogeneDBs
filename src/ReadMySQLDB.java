import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadMySQLDB {
	private final String JDBC_MysqlDRIVER = "com.mysql.jdbc.Driver";
	private String password;
	private String username;
	private String database;
	private String location;
	private String url;
	private String query;
	private String mysqlLocation;

	private Connection connection;
	private Statement statement;
	private boolean connectedToDatabase = false;

	private ResultSet resultSet;

	public ReadMySQLDB(String mysqlLocation, String username, String password,
			String database, String location) {
		this.password = password;
		this.username = username;
		this.database = database;
		this.location = location;
		this.mysqlLocation = mysqlLocation;
		url = "jdbc:mysql://" + location + "/" + database;
		query = "SHOW TABLES";
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		// get current date time with Date()
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		File test = new File("D:\\MySQLBackup" + dateFormat.format(date)
				+ ".sql");
		FileWriter fw = null;
		try {
			fw = new FileWriter(test);
			Runtime rt = Runtime.getRuntime();
			Process child = rt.exec(mysqlLocation + "mysqldump -h" + location
					+ " -u" + username + " -p" + password + " " + database
					+ " > MySQLBackup" + dateFormat.format(date) + ".sql");
			InputStream in = child.getInputStream();
			InputStreamReader xx = new InputStreamReader(in, "latin1");
			char[] chars = new char[1024];
			int ibyte = 0;
			while ((ibyte = xx.read(chars)) > 0) {
				fw.write(chars);
			}
			fw.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

		connectedToDatabase = true;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
