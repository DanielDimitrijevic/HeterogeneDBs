

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.table.AbstractTableModel;

public class ResultSetTableModel extends AbstractTableModel 
{
   private Connection connection;
   private Statement statement;
   private ResultSet resultSet;
   private ResultSetMetaData metaData;
   private int numberOfRows;

   private boolean connectedToDatabase = false;
   
   public ResultSetTableModel( String driver, String url, 
      String username, String password, String query ) 
      throws SQLException, ClassNotFoundException
   {         
      Class.forName( driver );

      connection = DriverManager.getConnection( url, username, password );

      statement = connection.createStatement( 
         ResultSet.TYPE_SCROLL_INSENSITIVE,
         ResultSet.CONCUR_READ_ONLY );

      connectedToDatabase = true;

      setQuery( query );
   }

   public Class getColumnClass( int column ) throws IllegalStateException
   {
      if ( !connectedToDatabase ) 
         throw new IllegalStateException( "Not Connected to Database" );

      try 
      {
         String className = metaData.getColumnClassName( column + 1 );
         
         return Class.forName( className );
      }
      catch ( Exception exception ) 
      {
         exception.printStackTrace();
      }
      
      return Object.class;
   }

   public int getColumnCount() throws IllegalStateException
   {   
      if ( !connectedToDatabase ) 
         throw new IllegalStateException( "Not Connected to Database" );

      try 
      {
         return metaData.getColumnCount(); 
      }
      catch ( SQLException sqlException ) 
      {
         sqlException.printStackTrace();
      }
      
      return 0;
   }

   public String getColumnName( int column ) throws IllegalStateException
   {    
      if ( !connectedToDatabase ) 
         throw new IllegalStateException( "Not Connected to Database" );

      try 
      {
         return metaData.getColumnName( column + 1 );  
      }
      catch ( SQLException sqlException ) 
      {
         sqlException.printStackTrace();
      }
      
      return "";
   }

   public int getRowCount() throws IllegalStateException
   {      
      if ( !connectedToDatabase ) 
         throw new IllegalStateException( "Not Connected to Database" );
 
      return numberOfRows;
   }

   public Object getValueAt( int row, int column ) 
      throws IllegalStateException
   {
      if ( !connectedToDatabase ) 
         throw new IllegalStateException( "Not Connected to Database" );

      try 
      {
         resultSet.absolute( row + 1 );
         return resultSet.getObject( column + 1 );
      }
      catch ( SQLException sqlException ) 
      {
         sqlException.printStackTrace();
      }
      
      return "";
   }
   
   public void setQuery( String query ) 
      throws SQLException, IllegalStateException 
   {
	   if ( !connectedToDatabase ) 
	         throw new IllegalStateException( "Not Connected to Database" );
	      
	      if(query.toUpperCase().startsWith("SELECT") || query.toUpperCase().startsWith("SHOW")){
	    	  resultSet = statement.executeQuery( query );// schickt die abfrage zur db
	      }else if(query.toUpperCase().startsWith("INSERT") || query.toUpperCase().startsWith("UPDATE") || query.toUpperCase().startsWith("DELETE") || query.toUpperCase().startsWith("CREATE") || query.toUpperCase().startsWith("DROP") || query.toUpperCase().startsWith("ALTER") || query.toUpperCase().startsWith("GRANT")){
	    	  System.out.println("" + statement.executeUpdate( query ));
	    	  String tb = "";
	    	  boolean ab1 = false, ab2 = false;
	    	  for(int i = 0; i < query.length();i++){
	    		  if(query.toUpperCase().startsWith("INSERT") || query.toUpperCase().startsWith("CREATE")){
	    			  if(query.charAt(i) == ' '){
	    				  if(ab1)
	    					  ab2 = ab1;
	    				  else
	    					  ab1 =true;
	    				  }
	    			  if(ab1 && ab2){
	    				  if(query.charAt(i) != ' ')
	    					  tb += "" + query.charAt(i);
	    				  else
	    					  break;
	    				  }
	    			  }else if(query.toUpperCase().startsWith("UPDATE")){
	    				  if(query.charAt(i) == ' '){
	    					  ab1=true;
	    				  }
	    				  if(ab1){
	    					  if(query.charAt(i) != ' ')
	    						  tb += "" + query.charAt(i);
	    					  else
	    						  break;
	    					  }
	    				  }else{
	    					  break;
	    				  }
	    		  }
	    	  resultSet = statement.executeQuery( "SELECT * FROM " + tb );
	      }else{
	    	  System.err.println("Nicht zulässige anweisung!");
	      }
	      metaData = resultSet.getMetaData();
	      resultSet.last();
	      numberOfRows = resultSet.getRow();
	      fireTableStructureChanged();
	   }

   public void disconnectFromDatabase()            
   {              
      if ( !connectedToDatabase )                  
         return;

      try                                          
      {                                            
         statement.close();                        
         connection.close();                       
      }
      catch ( SQLException sqlException )          
      {                                            
         sqlException.printStackTrace();           
      }
      finally
      {                                            
         connectedToDatabase = false;              
      }
   }
}

/**************************************************************************
 * (C) Copyright 1992-2005 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *************************************************************************/
