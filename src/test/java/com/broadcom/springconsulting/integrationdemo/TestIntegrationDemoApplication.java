package com.broadcom.springconsulting.integrationdemo;

import org.springframework.boot.SpringApplication;

public class TestIntegrationDemoApplication {

    public static void main( String[] args ) {

        SpringApplication.from( IntegrationDemoApplication::main )
                .with( TestcontainersConfiguration.class )
                .withAdditionalProfiles( "test" )
                .run( args );

    }

}
