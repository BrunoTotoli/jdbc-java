package application;

import db.DB;
import db.DbException;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Program {

    Connection conn = null;
    Statement statement = null;
    ResultSet resultSet = null;
    PreparedStatement preparedStatement = null;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");


    @Test
    void searchInDataBase() {
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

    @Test
    void insertInDataBase() {
        try {
            conn = DB.getConnection();
            preparedStatement = conn.prepareStatement("INSERT INTO seller " +
                    "(Name,Email,BirthDate,BaseSalary,DepartmentId) " +
                    "VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, "Moacir Oliveira");
            preparedStatement.setString(2, "Moacir@gmail.com");
            preparedStatement.setDate(3, new Date(sdf.parse("22/04/1948").getTime()));
            preparedStatement.setDouble(4, 3000.00);
            preparedStatement.setInt(5, 4);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = preparedStatement.getGeneratedKeys();
                while (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("Id: " + id);
                }
            } else {
                System.out.println("No rown affected!");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            DB.closeStatement(preparedStatement);
            DB.closeConnection();
        }
    }

    @Test
    void updateInDataBase() {
        try {
            conn = DB.getConnection();
            preparedStatement = conn.prepareStatement("UPDATE seller " +
                    "SET BaseSalary + ? " +
                    "WHERE (DepartmentId = ? )");
            preparedStatement.setDouble(1, 200);
            preparedStatement.setInt(2, 2);
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Rows Affected: " + rowsAffected);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(preparedStatement);
            DB.closeConnection();
        }

    }

    @Test
    void deleteInDataBase() {
        try {
            conn = DB.getConnection();
            preparedStatement = conn.prepareStatement("DELETE FROM department WHERE id = ? ");
            preparedStatement.setInt(1, 2);
            int rowsAffected = preparedStatement.executeUpdate();

            System.out.println("Rows Affected: " + rowsAffected);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(preparedStatement);
            DB.closeConnection();
        }
    }

    @Test
    void commit() {
        try {
            conn = DB.getConnection();
            statement = conn.createStatement();
            conn.setAutoCommit(false);
            int rows1 = statement.executeUpdate("UPDATE seller SET BaseSalary = 2090 WHERE DepartmentId = 1 ");
//            if (true)
//                throw new SQLException("Fake Exception");
            int rows2 = statement.executeUpdate("UPDATE seller SET BaseSalary = 2090 WHERE DepartmentId = 2");
            conn.commit();
            System.out.println("rows 1: " + rows1);
            System.out.println("rows 2: " + rows2);


        } catch (SQLException e) {
            try {
                conn.rollback();
                throw new DbException("Transaction rolled back! caused by: " + e.getMessage());
            } catch (SQLException ex) {
                throw new DbException("Error trying to rollback! caused by: " + ex.getMessage());
            }
        }
    }
}
