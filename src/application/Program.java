package application;

import db.DB;
import db.DbException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Program {

    public static void main(String[] args) {
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            conn = DB.getConnection();
            statement = conn.createStatement();
            resultSet = statement.executeQuery("select * from department");
            while (resultSet.next()) {
                System.out.println("ID" + resultSet.getInt("Id") + " Name:" + resultSet.getString("Name"));
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(statement);
            DB.closeResultSet(resultSet);
            DB.closeConnection();
        }
    }
}
