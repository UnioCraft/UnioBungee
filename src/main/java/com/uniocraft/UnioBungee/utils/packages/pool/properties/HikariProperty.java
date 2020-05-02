package com.uniocraft.UnioBungee.utils.packages.pool.properties;

import com.zaxxer.hikari.HikariConfig;

public interface HikariProperty {

    void applyTo(HikariConfig config);

}
