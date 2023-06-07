package com.example.oop_cw_st79404;

import java.sql.Connection;
import java.sql.DriverManager;

public class databaseConnection {
    public Connection databaseLink;

    public Connection getConnection() {
        String url = "jdbc:sqlserver://for-csma.c0e8wpsra8zd.eu-west-2.rds.amazonaws.com:1433;DatabaseName=CSMA_Database;encrypt=true;trustServerCertificate=true;username=CSMAUsername;password=passwordCSMA";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            databaseLink = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }

        return databaseLink;
    }
}
