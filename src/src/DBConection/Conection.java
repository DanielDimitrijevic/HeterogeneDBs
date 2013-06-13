package DBConection;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * Dieses Interface gibt die Connection f�r die Datenbanken vor
 * @author Dominik Backhausen
 * @version 0.1
 */
public interface Conection extends Runnable{
	/**
	 * Baut Verbindung zu Datenbank auf
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void connect()throws ClassNotFoundException, SQLException;
	/**
	 * Gibt die Tabellen Namen zur�ck die sich in Der Datenbank befinden
	 * @return Liste von Tabellennamen
	 * @throws SQLException
	 */
	public ArrayList<String> getTables() throws SQLException;
	/**
	 * Gibt die MetaDaten von einer bestimmten Tabelle zur�ck
	 * @param tabn Tabellenname
	 * @return Metadaten der Tabelle
	 * @throws SQLException
	 */
	public ResultSetMetaData getMeta(String tabn) throws SQLException;
	/**
	 * F�hrt eine Query aus und gibt ein Resultset zur�ck
	 * @param q die auszuf�hrende Query
	 * @return das Ergebniss der Query
	 * @throws SQLException
	 */
	public ResultSet exeQuarry(String q)throws SQLException;
	/**
	 * F�hrt ein Update aus und gibt die Anzahl der �nderungen zur�ck
	 * @param q Updatebefehl
	 * @return Anzahl der �nderungen
	 * @throws SQLException
	 */
	public int exeUpdate(String q)throws SQLException;
	/**
	 * Initialisiert den Z�hler f�r die Tabelleneintr�ge
	 * @throws SQLException
	 */
	public void initTC()throws SQLException;
	/**
	 * Gibt die Metadaten der Datenbank zur�ck
	 * @return Metadaten der Datenbank
	 * @throws SQLException
	 */
	public DatabaseMetaData getMe() throws SQLException;
	/**
	 * Setzt die Tabellenzeilen Z�hler f�r eine bestimmte Tabelle
	 * @param id index der Tabelle
	 * @param wert neuer wert
	 */
	public void setC(int id, int wert);
}
