package DBConection;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * Dieses Interface gibt die Connection für die Datenbanken vor
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
	 * Gibt die Tabellen Namen zurück die sich in Der Datenbank befinden
	 * @return Liste von Tabellennamen
	 * @throws SQLException
	 */
	public ArrayList<String> getTables() throws SQLException;
	/**
	 * Gibt die MetaDaten von einer bestimmten Tabelle zurück
	 * @param tabn Tabellenname
	 * @return Metadaten der Tabelle
	 * @throws SQLException
	 */
	public ResultSetMetaData getMeta(String tabn) throws SQLException;
	/**
	 * Führt eine Query aus und gibt ein Resultset zurück
	 * @param q die auszuführende Query
	 * @return das Ergebniss der Query
	 * @throws SQLException
	 */
	public ResultSet exeQuarry(String q)throws SQLException;
	/**
	 * Führt ein Update aus und gibt die Anzahl der Änderungen zurück
	 * @param q Updatebefehl
	 * @return Anzahl der Änderungen
	 * @throws SQLException
	 */
	public int exeUpdate(String q)throws SQLException;
	/**
	 * Initialisiert den Zähler für die Tabelleneinträge
	 * @throws SQLException
	 */
	public void initTC()throws SQLException;
	/**
	 * Gibt die Metadaten der Datenbank zurück
	 * @return Metadaten der Datenbank
	 * @throws SQLException
	 */
	public DatabaseMetaData getMe() throws SQLException;
	/**
	 * Setzt die Tabellenzeilen Zähler für eine bestimmte Tabelle
	 * @param id index der Tabelle
	 * @param wert neuer wert
	 */
	public void setC(int id, int wert);
}
