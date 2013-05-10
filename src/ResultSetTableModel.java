

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

public class ResultSetTableModel extends AbstractTableModel {
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private ResultSetMetaData metaData;
	private int numberOfRows;
	private String text;

	private boolean connectedToDatabase = false;

	public ResultSetTableModel(String driver, String url, String username,
			String password, String query) throws SQLException,
			ClassNotFoundException {
		Class.forName(driver);

		connection = DriverManager.getConnection(url, username, password);

		statement = connection.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);// CONCUR_UPDATABLE
																				// damit
																				// man
																				// auch
																				// was
																				// verändern
																				// kann

		connectedToDatabase = true;

		setQuery(query);
	}

	public Class getColumnClass(int column) throws IllegalStateException {
		if (!connectedToDatabase)
			throw new IllegalStateException("Not Connected to Database");

		try {
			String className = metaData.getColumnClassName(column + 1);

			return Class.forName(className);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return Object.class;
	}

	public int getColumnCount() throws IllegalStateException {
		if (!connectedToDatabase)
			throw new IllegalStateException("Not Connected to Database");

		try {
			return metaData.getColumnCount();
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}

		return 0;
	}

	public String getColumnName(int column) throws IllegalStateException {
		if (!connectedToDatabase)
			throw new IllegalStateException("Not Connected to Database");

		try {
			return metaData.getColumnName(column + 1);
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}

		return "";
	}

	public int getRowCount() throws IllegalStateException {
		if (!connectedToDatabase)
			throw new IllegalStateException("Not Connected to Database");

		return numberOfRows;
	}

	public Object setValueAt(int row, int column) {
		try {
			resultSet.absolute(row + 1);
			return resultSet.getObject(column + 1);
		} catch (SQLException sql) {
			sql.printStackTrace();
		}
		return true;
	}

	public Object getValueAt(int row, int column) throws IllegalStateException {
		if (!connectedToDatabase)
			throw new IllegalStateException("Not Connected to Database");

		try {
			resultSet.absolute(row + 1);
			return resultSet.getObject(column + 1);
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}

		return "";
	}

	public void setQuery(String query) throws SQLException,
			IllegalStateException {
		if (!connectedToDatabase)
			throw new IllegalStateException("Not Connected to Database");

		if (query.toUpperCase().startsWith("SHOW")
				|| query.toUpperCase().startsWith("SELECT"))
			resultSet = statement.executeQuery(query);
		else if (query.toUpperCase().startsWith("INSERT")
				|| query.toUpperCase().startsWith("UPDATE")
				|| query.toUpperCase().startsWith("DELETE")
				|| query.toUpperCase().startsWith("CREATE")
				|| query.toUpperCase().startsWith("DROP")
				|| query.toUpperCase().startsWith("ALTER")
				|| query.toUpperCase().startsWith("GRANT")) {
			int aenderung = statement.executeUpdate(query);
			if (aenderung == 1)
				text = "Tabele wurde aktualisiert. Es wurde " + aenderung
						+ " Werte geändert.";
			else
				text = "Tabele wurde aktualisiert. Es wurden " + aenderung
						+ " Werte geändert.";
			String[] s = query.split(" ");
			String select = null;
			if (query.toUpperCase().startsWith("UPDATE")) {
				select = "SELECT * FROM " + s[1];
			} else if (!query.toUpperCase().startsWith("GRANT")) {
				select = "use mysql;SELECT user, password FROM user;";
			} else if (!query.toUpperCase().startsWith("UPDATE")) {
				select = "SELECT * FROM " + s[2];
			}
			resultSet = statement.executeQuery(select);
		}
		metaData = resultSet.getMetaData();

		resultSet.last();
		numberOfRows = resultSet.getRow();

		fireTableStructureChanged();
	}
	@Override
	public boolean isCellEditable(int row, int col) {
	     switch (col) {
	         case 0:
	         case 1:
	             return true;
	         default:
	             return false;
	      }
	}

	public void disconnectFromDatabase() {
		if (!connectedToDatabase)
			return;

		try {
			statement.close();
			connection.close();
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} finally {
			connectedToDatabase = false;
		}
	}

	public String getText() {
		return text;
	}

}

/**************************************************************************
 * (C) Copyright 1992-2005 by Deitel & Associates, Inc. and * Pearson Education,
 * Inc. All Rights Reserved. *
 *************************************************************************/
