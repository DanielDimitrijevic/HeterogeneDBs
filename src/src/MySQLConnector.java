
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.Box;

public class MySQLConnector extends JFrame 
{
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
   static final String DATABASE_URL = "jdbc:mysql://127.0.0.1/tvprog";
   static final String USERNAME= "root";
   static final String PASSWORD= "root";
   
   static final String DEFAULT_QUERY = "SELECT * FROM genre";
   
   private ResultSetTableModel tableModel;
   private JTextArea queryArea;
   
   public MySQLConnector() 
   {   
      super( "Displaying Query Results" );
        
      try 
      {
         tableModel = new ResultSetTableModel( JDBC_DRIVER, DATABASE_URL, 
            USERNAME, PASSWORD, DEFAULT_QUERY );

         queryArea = new JTextArea( DEFAULT_QUERY, 3, 100 );
         queryArea.setWrapStyleWord( true );
         queryArea.setLineWrap( true );
         
         JScrollPane scrollPane = new JScrollPane( queryArea,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
         
         JButton submitButton = new JButton( "Submit Query" );

         Box box = Box.createHorizontalBox();
         box.add( scrollPane );
         box.add( submitButton );

         JTable resultTable = new JTable( tableModel );
         
         add( box, BorderLayout.NORTH );
         add( new JScrollPane( resultTable ), BorderLayout.CENTER );

         submitButton.addActionListener( 
         
            new ActionListener() 
            {
               public void actionPerformed( ActionEvent event )
               {
                  try 
                  {
                     tableModel.setQuery( queryArea.getText() );
                  }
                  catch ( SQLException sqlException ) 
                  {
                     JOptionPane.showMessageDialog( null, 
                        sqlException.getMessage(), "Database error", 
                        JOptionPane.ERROR_MESSAGE );
                     
                     try 
                     {
                        tableModel.setQuery( DEFAULT_QUERY );
                        queryArea.setText( DEFAULT_QUERY );
                     }
                     catch ( SQLException sqlException2 ) 
                     {
                        JOptionPane.showMessageDialog( null, 
                           sqlException2.getMessage(), "Database error", 
                           JOptionPane.ERROR_MESSAGE );
         
                        tableModel.disconnectFromDatabase();
         
                        System.exit( 1 );
                     }                   
                  }
               }
            }
         );

         setSize( 500, 250 );
         setVisible( true );
      }
      catch ( ClassNotFoundException classNotFound ) 
      {
         JOptionPane.showMessageDialog( null, 
            "MySQL driver not found", "Driver not found",
            JOptionPane.ERROR_MESSAGE );
         
         System.exit( 1 );
      }
      catch ( SQLException sqlException ) 
      {
         JOptionPane.showMessageDialog( null, sqlException.getMessage(), 
            "Database error", JOptionPane.ERROR_MESSAGE );
               
         tableModel.disconnectFromDatabase();
         
         System.exit( 1 );
      }
      
      setDefaultCloseOperation( DISPOSE_ON_CLOSE );
      
      addWindowListener(
      
         new WindowAdapter() 
         {
            public void windowClosed( WindowEvent event )
            {
               tableModel.disconnectFromDatabase();
               System.exit( 0 );
            }
         }
      );
   }
   
   public static void main( String args[] ) 
   {
      new MySQLConnector();    
   }
}

/**************************************************************************
 * (C) Copyright 1992-2005 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *************************************************************************/
