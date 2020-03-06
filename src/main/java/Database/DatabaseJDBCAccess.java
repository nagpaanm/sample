package Database;
/**
 * 
 * @author Anmol Nagpal
 * @date March 2nd 2020
 * @purpose To connect to a postgresql database and retrieve data
 * 
 * 
 * 
 * This class can make a connection to any postgresql database and is able to 
 * retrieve information. The user must input the database path, database name,
 * the username to the database and the password to the database upon
 * initialization of this class.
 * 
 * 
 * Once initialized, the user can use the methods of this class to retrieve
 * the tables in the database, along with the columns and rows.
 * 
 * 
 * E.g. Use Case:
 * 
 * DatabaseJDBCAccess jdbc = new DatabaseJDBCAccess(
 * 						"jdbc:postgresql://localhost:5432/",
 * 						"mydb",
 * 						"username"
 * 						"password");
 * 
 * jdbc.getTableNames();
 * -> ArrayList<String> containing all names of tables in the database
 * 
 * 
 * String table = "Census_val_2006";
 * jdbc.getColumnNames(table);
 * -> ArrayList<String> containing all column names of 'table' (if it exists)
 * 
 * 
 * String table = "Census_val_2006";
 * ArrayList<String> columns = new ArrayList<String>();
 * columns.add("AGEGRP");
 * columns.add("EMPIN");
 * String whereClause = "WHERE EMPIN::integer > 100000::integer";
 * jdbc.getResults(table, columns, whereClause, -1);
 * -> ArrayList<String> containing the out rows. Note: whereClause can be empty
 * 
 */


import java.util.ArrayList;
import java.sql.*;


public class DatabaseJDBCAccess {
	

	private String databasePath;
	private String databaseName;
	private String username;
	private String password;
	private Connection connection;
	private Statement statement;
	private DatabaseMetaData metaData;
	private ResultSet resultSet;
	
	
	public DatabaseJDBCAccess(
			String databasePath,
			String databaseName,
			String username,
			String password) {
		
		/**
		 * Intiailize DatabaseJDBCAccess.
		 */
		
		try {
			connection = 
					DriverManager.getConnection(
							databasePath + databaseName, 
							username, 
							password);
            statement = connection.createStatement();
            this.databasePath = databasePath;
            this.databaseName = databaseName;
            this.username = username;
            this.password = password;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getTableNames(){
		
		/**
		 * This function returns an ArrayList of the table names for all of the 
		 * tables in the database.
		 */
		
		ArrayList<String> tableNames = new ArrayList<String>();
		String[] types = {"TABLE"};
		try {
			metaData = connection.getMetaData();
			resultSet = metaData.getTables(this.databaseName, null, "%", types);
			while (resultSet.next()) {
				tableNames.add(resultSet.getString(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tableNames;
	}
	
	
	public ArrayList<String> getColumnNames(String table){
		
		/**
		 * This function takes a valid table name as an input and returns an 
		 * ArrayList consisting of all of the column names in that table.
		 */
		
		ArrayList<String> columnNames = new ArrayList<String>();
		PreparedStatement pstmt;
		try {
			pstmt = connection.prepareStatement("SELECT * FROM " + table);
			ResultSetMetaData meta = pstmt.getMetaData();
			//start at index 1 since column indicies start at 1 (not 0)
			for (int index = 1; index <= meta.getColumnCount(); index++)
			{
				columnNames.add(meta.getColumnName(index));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return columnNames;
	}
	
	
	public ArrayList<ArrayList<String>> getResults(
			String table, 
			ArrayList<String> columns, 
			String whereClause,
			int removeColumnAtIndex){
		
		/**
		 * This function takes a valid table name, an ArrayList of the columns
		 * to select and a whereClause (which can be left as a blank string) as
		 * input and returns the corresponding output rows.
		 * 
		 * The removeColumnAtIndex parameter will remove a column at the 
		 * specified index. (This feature is specifically designed for when
		 * additional columns are created outside of a database environment). If
		 * you do not wish to remove any columns, simply enter in -1.
		 */
		
		ArrayList<ArrayList<String>> results = 
				new ArrayList<ArrayList<String>>();
		
		if (removeColumnAtIndex > -1) {
			columns.remove(removeColumnAtIndex);
		}
		
		String columnString = new String();
		for (String column: columns) {
			columnString += column;
			if (columns.indexOf(column) < columns.size() - 1) {
				columnString += ", ";
			}
		}
		try {
			ResultSet resultSet = 
					statement.executeQuery(
									"SELECT " + 
									columnString + 
									" FROM " + 
									table + 
									" " + 
									whereClause);
			while(resultSet.next()) {
				ArrayList<String> row = new ArrayList<String>();
				for (int index = 0; index < columns.size(); index++) {
					row.add(resultSet.getString(columns.get(index)).trim());
				}
				results.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}
}
