package Controller;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import DBConection.Conection;
import DBConection.MYSQLConection;
import DBConection.POSTConection;
/**
 * Diese Klasse ist die Hauptklasse des Programms und wird als erste gestartet
 * Sie Übernimmt unter anderem die Kommunikation und Verwaltung der Connections sowie die Hauptaufgaben der Synchronisation und Überprüfung
 * @author Dominik Backhausen, Alexander Rieppel
 * @version 0.1
 */
public class Controller {
	private Conection m, p;
	
	public static void main(String[] args){
		boolean a = true;
		String mun = "", mup ="", mip ="", mdb ="", pun ="", pup ="", pip ="", pdb ="";
		if(args.length > 0)
			if(args[0].charAt(0) == 'd'){
				a = false;
				mip = "localhost";
				mun = "root";
				mup = "root";
				mdb = "tgmbank";
				pip = "localhost";
				pun = "postgres";
				pup = "postgres";
				pdb = "tgmbank";
			}else{
				if(args.length == 8){
					a = false;
					mip = args[0];
					mun = args[3];
					mup = args[4];
					mdb = args[2];
					pip = args[5];
					pun = args[7];
					pup = args[8];
					pdb = args[6];
				}
			}
		
		if(a)
			System.out.println("Falsche eingabe! \n Bitte Server informationen angeben eingeben:\n " +
					"<MYSQL Ip-Addrese/hostname> <MYSQL Datenbank> <MYSQL UserName> <MYSQL Passwort> " +
					"<Postgres Ip-Addrese/hostname> <Postgres Datenbank> <Postgres UserName> <postgres Passwort>   \n d defaultwert");
		else
			new Controller(mip,pip,mdb,pdb,mun,pun,mup,pup);
		}
	/**
	 * Konstruktor zum Erstellen eines Controllers
	 * Erstellt die Connections, baut die Verbindung auf, macht einen Tabellencheck und startet die Threads
	 */
	public Controller(String mip, String pip, String mdb, String pdb, String mun, String pun, String mup, String pup){
		try {
			m = new MYSQLConection(mip, mdb,mun, mup,this);
			m.connect();
			p = new POSTConection(pip, pdb,pun, pup,this);
			p.connect();
			//p.getTables();
			this.compareTabels();
			m.initTC();
			p.initTC();
			Thread mt = new Thread(m);
			Thread pt = new Thread(p);
			mt.start();
			pt.start();
			
		} catch (ClassNotFoundException e) {
			System.err.println("ERROR01:" + e.getMessage());
		} catch (SQLException e) {
			System.err.println("ERROR02:" + e.getMessage());
			//e.printStackTrace();
		}
	}
	/**
	 * Um die Count Anzahl in MYSQL Connection zu ändern
	 * @param id Position in der Liste
	 * @param wert neuer wert
	 */
	public void setMYSQLC(int id, int wert){
		m.setC(id, wert);
	}
	/**
	 * Um die Count Anzahl in Postgres Connection zu ändern
	 * @param id Position in der Liste
	 * @param wert neuer wert
	 */
	public void setPOSTC(int id, int wert){
		p.setC(id, wert);
	}
	/**
	 * Überprüft alle Tabellen ob sie von Name und Struktur her zusammenpassen 
	 * @throws SQLException
	 */
	public void compareTabels() throws SQLException{
		ArrayList<String> mtab = m.getTables();
		ArrayList<String> ptab = p.getTables();
		Collections.sort(mtab);
		Collections.sort(ptab);
		if(mtab.size() == ptab.size()){
			for(int i  = 0; i < mtab.size();i++){
				if(!mtab.get(i).equalsIgnoreCase(ptab.get(i))){
					System.out.println("Tabellen passen nicht zusammen!\nProgramm wird beendet");
					System.exit(0);
				}else{
					ResultSetMetaData mrmd = m.getMeta(mtab.get(i));
					ResultSetMetaData prmd = p.getMeta(ptab.get(i));
					if(mrmd.getColumnCount() == prmd.getColumnCount()){
						for(int ii = 1; i < mrmd.getColumnCount();i++){
							if(!((mrmd.getColumnName(ii).equalsIgnoreCase(prmd.getColumnName(ii)))&&
									(mrmd.getColumnType(ii) == prmd.getColumnType(ii)))){
								System.out.println("Tabellen informationen stimmen nicht überin!\nProgramm wird beendet!");
								System.exit(0);
							}
						}
					}
				}
			}
			System.out.println("Tabellen check bestanden Programm wird weiter ausgeführt!");
			for(int i = 0 ; i < mtab.size();i++){
				this.match(true,true, mtab.get(i));
				this.match(true,false, mtab.get(i));
				this.match(true,true, mtab.get(i));
				this.match(true,false, mtab.get(i));
			}
		}
	}
	/**
	 * Diese Methode wird aufgerufen um die Tabellen zu synchronisieren
	 * Mit Hilfe von Abfragen wird ermittelt welche Datensätze unterschiedlich sind und somit wird die andere Datenbank entsprechend angepasst
	 * @param mainmysql gibt an ob die Quelle MySQL oder Postgres sein soll
	 * @param insert gibt an ob ein Insert oder ein Delete Benötigt wird
	 * @param tabn gibt den Namen der Tabelle an in der es Änderungen gab
	 * @throws SQLException SQL Fehler
	 */
	public void match(boolean mainmysql, boolean insert, String tabn) throws SQLException{
		ResultSet mrs = m.exeQuarry("SELECT * FROM " + tabn);
		ResultSet prs = p.exeQuarry("SELECT * FROM " + tabn);
		ResultSetMetaData mrsm = mrs.getMetaData();
		ResultSetMetaData prsm = prs.getMetaData();
		DatabaseMetaData meta = m.getMe();
		ResultSet prim = meta.getPrimaryKeys(null, null, tabn);
		prim.next();
		//Sucht den Primary Key in der Tabelle
		String pmr = prim.getString(4);
		int primid = 0;
		for(int i = 1; i <= mrsm.getColumnCount();i++){
			if(mrsm.getColumnName(i).equalsIgnoreCase(pmr)){
				primid = i;
			}
		}
		//Abfrage geordnet nach Primary Key um sicherzustellen, dass beide Inhalte gleich geordnet sind
		mrs = m.exeQuarry("SELECT * FROM " + tabn + " ORDER BY " + mrsm.getColumnName(primid));
		prs = p.exeQuarry("SELECT * FROM " + tabn + " ORDER BY " + prsm.getColumnName(primid));
		if(primid > 0){
			//Synchronisation für benötigten Insert
			if(insert == true){
				if(mainmysql == true){
					int durch = 0;
					while(mrs.next()){
						prs = p.exeQuarry("SELECT * FROM " + tabn + " ORDER BY " + prsm.getColumnName(primid));
						for(int id = 0; id < durch;id++)
							prs.next();
						if(!prs.next()){
							String qu = "INSERT INTO " + tabn + " VALUES (";
							for(int i = 1; i <= prsm.getColumnCount(); i ++){
								if(prsm.getColumnType(i) == java.sql.Types.INTEGER){
									qu+=mrs.getInt(i) ;
								}else if( prsm.getColumnType(i) == java.sql.Types.DOUBLE || prsm.getColumnType(i) == java.sql.Types.FLOAT){
									qu += mrs.getDouble(i) ;
								}else{
									qu += "'" + mrs.getString(i) + "'";
								}
								
								if(!(i == prsm.getColumnCount() ))
									qu += ",";
							}
							qu+=")";
							p.exeUpdate(qu);
						}
						durch ++;
					}
				}else{
					int durch = 0;
					while(prs.next()){
						mrs = m.exeQuarry("SELECT * FROM " + tabn + " ORDER BY " + mrsm.getColumnName(primid));
						for(int id = 0; id < durch;id++)
							mrs.next();
						if(!mrs.next()){
							String qu = "INSERT INTO " + tabn + " VALUES (";
							for(int i = 1; i <= mrsm.getColumnCount(); i ++){
								if(mrsm.getColumnType(i) == java.sql.Types.INTEGER){
									qu+=prs.getInt(i) ;
								}else if( mrsm.getColumnType(i) == java.sql.Types.DOUBLE || mrsm.getColumnType(i) == java.sql.Types.FLOAT){
									qu += prs.getDouble(i) ;
								}else{
									qu += "'" + prs.getString(i) + "'";
								}
								
								if(!(i == mrsm.getColumnCount() ))
									qu += ",";
							}
							qu+=")";
							m.exeUpdate(qu);
						}
						durch ++;
						
					}
				}
				//Synchronisation für benötigtes Update
			}else if(insert == false){
				if(mainmysql == true){
					int durch = 0;
					boolean mb = true , pb = true;
					while(mb || pb){
						mb = mrs.next();
						prs = p.exeQuarry("SELECT * FROM " + tabn + " ORDER BY " + prsm.getColumnName(primid));
						for(int id = 0; id <= durch;id++)
							pb = prs.next();
						if(pb == true && mb == true){
							if(mrs.getInt(primid) != prs.getInt(primid)){
								p.exeUpdate("DELETE FROM " + tabn + " WHERE " + prsm.getColumnName(primid) + " = " + prs.getInt(primid));
							}
							
						}else if(mb == false && pb==true){
							p.exeUpdate("DELETE FROM " + tabn + " WHERE " + prsm.getColumnName(primid) + " = " + prs.getInt(primid));
						}
						durch ++;
					}
				}else if(mainmysql == false){
					int durch = 0;
					boolean mb = true , pb = true;
					while(mb || pb){
						pb = prs.next();
						mrs = m.exeQuarry("SELECT * FROM " + tabn + " ORDER BY " + prsm.getColumnName(primid));
						for(int id = 0; id <= durch;id++)
							mb = mrs.next();
						if(pb == true && mb == true){
							if(mrs.getInt(primid) != prs.getInt(primid)){
								m.exeUpdate("DELETE FROM " + tabn + " WHERE " + prsm.getColumnName(primid) + " = " + prs.getInt(primid));
							}
							
						}else if(pb == false && mb == true){
							m.exeUpdate("DELETE FROM " + tabn + " WHERE " + prsm.getColumnName(primid) + " = " + prs.getInt(primid));
						}
						durch ++;
					}
				}
			}
		}else{
			System.err.println("ERROR: Primarykey nicht gefunden!\nProgramm wird beendet!");
			System.exit(0);
		}		
	}
}
