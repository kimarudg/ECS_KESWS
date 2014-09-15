package databaselayer;

import java.sql.Connection;
import java.sql.DriverManager;
import logger.ECSKESWSLogger;
import ECSKESWSService.ConfigMapper;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class DBConnector {

    BoneCP connectionPool = null;
    Connection conn = null;

    public Connection GetECSDBConnector() {
        ConfigMapper configMapper = new ConfigMapper();


        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL = ConfigMapper.getECSDatabaseUrl();
        final String USER = ConfigMapper.getECSDatabaseuser();
        final String PASS = ConfigMapper.getECSDatabasepassword();
        try {

            Class.forName(JDBC_DRIVER);
            BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(DB_URL);
            config.setUsername(USER);
            config.setPassword(PASS); 
            config.setMinConnectionsPerPartition(5);
            config.setMaxConnectionsPerPartition(10);
             config.setPartitionCount(1);
            config.setDetectUnclosedStatements(true);
            config.setMaxConnectionAge(30, TimeUnit.SECONDS);
            config.setCloseOpenStatements(true);
            connectionPool = new BoneCP(config);
            conn = connectionPool.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            ECSKESWSLogger.Log(e.toString(), "SEVERE");
        }
        connectionPool.shutdown();
        return conn;
    }

    public void CloseECSDBConnector() {
        
        try {
            conn.close();
            
        } catch (SQLException ex) {
            ECSKESWSLogger.Log(ex.toString(), "SEVERE");
        }
    }
}
