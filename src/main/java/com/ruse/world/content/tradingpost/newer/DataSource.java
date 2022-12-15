package com.ruse.world.content.tradingpost.newer;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSource {

    public static final HikariConfig config = new HikariConfig();

    static {
      //  config.setJdbcUrl("jdbc:mysql://localhost:3306/simpsons");
      //  config.setUsername("bart");
      //  config.setPassword("51mp50n");
      //  config.addDataSourceProperty("cachePrepStmts", "true");
      //  config.addDataSourceProperty("prepStmtCacheSize", "250");
      //  config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    }

    public static final HikariDataSource ds = new HikariDataSource(config);
}
