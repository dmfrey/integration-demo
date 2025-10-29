package com.broadcom.springconsulting.integrationdemo.machineinterface.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@Configuration
@EnableJdbcRepositories(
        basePackages = {
                "com.broadcom.springconsulting.integrationdemo.machineinterface.adapter.out.persistence"
        }
)
class MachinePersistenceConfiguration {
}
