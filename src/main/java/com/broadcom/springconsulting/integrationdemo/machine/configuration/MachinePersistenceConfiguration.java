package com.broadcom.springconsulting.integrationdemo.machine.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@Configuration
@EnableJdbcRepositories(
        basePackages = {
                "com.broadcom.springconsulting.integrationdemo.machine.adapter.out.persistence"
        }
)
class MachinePersistenceConfiguration {
}
