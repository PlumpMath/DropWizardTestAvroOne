package com.awcoleman.DropWizardTestAvroOne.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * Class to output some resultset metadata from a Hive query to examine raw values.
 *
 */
public class InvestigateResultSetHiveJDBC {

	public InvestigateResultSetHiveJDBC() throws SQLException {

		String driverName = "org.apache.hive.jdbc.HiveDriver";
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/DropWizardTestAvroOne", "username", "");
		Statement stmt = con.createStatement();

		String sql = null;
		ResultSet res = null;
		
		String tableName = "TestRecOne";
/*
		// show tables
		sql = "show tables '" + tableName + "'";
		System.out.println("Running: " + sql);
		res = stmt.executeQuery(sql);
		if (res.next()) {
			System.out.println(res.getString(1));
		}
*/		
		
		// describe table
		sql = "describe " + tableName;
		System.out.println("Running: " + sql);
		res = stmt.executeQuery(sql);
		while (res.next()) {
			System.out.println(res.getString(1) + "\t" + res.getString(2));
		}

		//set session parameters
		sql = "set hive.resultset.use.unique.column.names=false";
		System.out.println("Running: " + sql);
		boolean stmtRet = stmt.execute(sql);
		
		
		// select * query
		sql = "select * from " + tableName;
		System.out.println("Running: " + sql);
		res = stmt.executeQuery(sql);
		
		
		ResultSetMetaData rsmd = res.getMetaData();
        for (int j = 1; j <= res.getMetaData().getColumnCount(); j++) {
        	System.out.print("ColumnName: "+rsmd.getColumnName(j)+" ColumnLabel: "+rsmd.getColumnLabel(j)+" ColumnType: "+rsmd.getColumnType(j)+ "\t");
        }
        System.out.println();
        System.out.println();

		
		while (res.next()) {
            for (int j = 1; j <= res.getMetaData().getColumnCount(); j++) {
                System.out.print(res.getObject(j) + "\t");
            }
            System.out.println();
        }
		
/*		
		while (res.next()) {
			System.out.println(String.valueOf(res.getInt(1)) + "\t" + res.getString(2));
		}
		
*/		
/*
		// regular hive query
		sql = "select count(1) from " + tableName;
		System.out.println("Running: " + sql);
		res = stmt.executeQuery(sql);
		while (res.next()) {
			System.out.println(res.getString(1));
		}

*/

	}

	public static void main(String[] args) throws SQLException {
		@SuppressWarnings("unused")
		InvestigateResultSetHiveJDBC mainobj = new InvestigateResultSetHiveJDBC();
	}

}
