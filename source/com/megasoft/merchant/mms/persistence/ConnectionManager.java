package com.megasoft.merchant.mms.persistence;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager {
    private final boolean useMerchantDatabase;

    public ConnectionManager(boolean useMerchantDatabase) {
        this.useMerchantDatabase = useMerchantDatabase;
    }

    public Connection getConnection() throws SQLException {
        return useMerchantDatabase
                ? DataSourceConfig.getMerchantConnection()
                : DataSourceConfig.getHistoryConnection();
    }
}