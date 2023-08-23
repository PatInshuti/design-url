package com.system_design_url.starter.db;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseUtil extends AbstractVerticle{
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

    public static Connection getConnection() {
        createInstance();
        return conn;
    }
}
