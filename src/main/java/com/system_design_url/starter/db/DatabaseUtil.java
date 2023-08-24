package com.system_design_url.starter.db;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    Logger log = LoggerFactory.getLogger(DatabaseUtil.class);
    private static DatabaseUtil instance = null;
    private static Connection conn = null;

    private DatabaseUtil() {


        if(conn == null) {

            try{
                conn = DriverManager.getConnection("jdbc:sqlite:urls.db");
                log.info("Success DB connection \n");
            } catch (Exception e){ log.error(e); };

        }

    }

    private static void createInstance() {
        if(instance == null) instance = new DatabaseUtil();
    }

    public static void initializeDbSchema() throws SQLException {
        // Save shortUrl, longUrl in DB
        String createTable = "CREATE TABLE IF NOT EXISTS Urls ( \n"
                + "   long_url text PRIMARY KEY,\n"
                + "   short_url text\n"
                + ")";

        Statement smt = conn.createStatement();
        smt.executeUpdate(createTable);
    }

    public static Connection getConnection() throws SQLException {
        createInstance();
        initializeDbSchema();
        return conn;
    }
}
