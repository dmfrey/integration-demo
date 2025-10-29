package com.broadcom.springconsulting.integrationdemo.movit.application.domain.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

@Configuration( proxyBeanMethods = false )
public class MovitIntegrationConfiguration {

    // Client initiates call to request to move any payload to any gateway
    @Bean
    IntegrationFlow retrievit() {

        return f -> f
                .routeToRecipients(r -> r
                                .recipient( "retrievesftp.input", "headers['retrieve-channel'] == 'SFTP'" )
//                            .recipient( "retrievesmb.input", "headers['retrieve-channel'] == 'SMB'" )
                );
    }

    // Send Payload to any Gateway
    @Bean
    IntegrationFlow movit() {

        return f -> f
                .routeToRecipients(r -> r
                        .recipient( "sendsftp.input", "headers['send-channel'] == 'SFTP'" )
                        .recipient( "sendsmb.input", "headers['send-channel'] == 'SMB'" )
                        .recipient( "sendtcp.input", "headers['send-channel'] == 'TCP'" )
                );
    }

}
