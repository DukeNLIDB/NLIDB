package com.dukenlidb.nlidb.service;

import org.springframework.stereotype.Service;
import com.dukenlidb.nlidb.model.DBConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class DBConnectionService {

    public Connection getConnection(DBConnectionConfig config) throws SQLException {
        return DriverManager.getConnection(config.getUrl(), config.getProperties());
    }

}
