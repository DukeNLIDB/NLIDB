package com.dukenlidb.nlidb.service;

import com.dukenlidb.nlidb.model.DBConnectionConfig;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class SQLExecutionService {

    private DBConnectionService dbConnectionService;

    @Autowired
    public SQLExecutionService(DBConnectionService dbConnectionService) {
        this.dbConnectionService = dbConnectionService;
    }

    public String executeSQL(DBConnectionConfig config, String query)
            throws SQLException {
        try {
            Connection conn = dbConnectionService.getConnection(config);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int numCols = rsmd.getColumnCount();
            StringBuilder sb = new StringBuilder();

            // SQL column index start from 1
            for (int col = 1; col <= numCols; col++) {
                sb.append(rsmd.getColumnName(col)).append("\t");
            }
            sb.append("\n");

            while (rs.next()) {
                for (int col = 1; col <= numCols; col++) {
                    sb.append(rs.getString(col)).append("\t");
                }
                sb.append("\n");
            }

            rs.close();
            stmt.close();
            conn.close();

            return sb.toString();
        } catch (PSQLException e) {
            return e.getMessage();
        }
    }

}
