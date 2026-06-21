package com.eds.db;

/**
 * 数据库连接：单例模式
 */
public class DBConnection {
    private static volatile DBConnection instance;

    private DBConnection() {}

    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }
}
