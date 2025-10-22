package com.broadcom.springconsulting.integrationdemo;

import org.aopalliance.aop.Advice;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.handler.advice.ExpressionEvaluatingRequestHandlerAdvice;
import org.springframework.messaging.MessageChannel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

//@Configuration
//@EnableIntegration
public class BackupIntegrationConfiguration {

    String build = System.getProperty( "user.dir" ) + File.separator + "build";

    @Bean
    IntegrationFlow fileMover() {

        return f -> f
                .routeToRecipients(r -> r
                        .recipient( "serverOne.input", "headers['target'] == 'server1'" )
                        .recipient( "serverTwo.input", "headers['target'] == 'server2'" )
                        .defaultOutputToParentFlow()
                );
    }

    @Bean
    IntegrationFlow serverOne() {

        var output = Paths.get( build, "output/server1" );
        try {
            java.nio.file.Files.createDirectories( output );
        } catch( IOException e ) {
            //  ignore if already exists
        }
        var directory = new File( output.toUri() );

        return f -> f
                .handle(
                        Files.outboundAdapter( directory )
                                .autoCreateDirectory( true )
                                .fileExistsMode( FileExistsMode.REPLACE )
                                .fileNameExpression( "headers['file_name']" ),
                        c -> c.advice( expressionAdvice() )
                );
    }

    @Bean
    IntegrationFlow serverTwo() {

        var output = Paths.get( build, "output/server2" );
        try {
            java.nio.file.Files.createDirectories( output );
        } catch( IOException e ) {
            //  ignore if already exists
        }
        var directory = new File( output.toUri() );

        return f -> f
                .handle(
                        Files.outboundAdapter( directory )
                                .autoCreateDirectory( true )
                                .fileExistsMode( FileExistsMode.REPLACE )
                                .fileNameExpression( "headers['file_name']" ),
                        c -> c.advice( expressionAdvice() )
                );
    }

    @Bean
    Advice expressionAdvice() {

        var advice = new ExpressionEvaluatingRequestHandlerAdvice();
        advice.setSuccessChannel( success() );
        advice.setOnSuccessExpressionString( "headers['file_name'] + ' sent successfully!'" );
        advice.setFailureChannelName( "failure.input" );
        advice.setOnFailureExpressionString( "headers['file_name'] + ' failed, with reason: ' + #exception.cause.message" );

        return advice;
    }

    @Bean
    MessageChannel success() {

        return MessageChannels.queue().getObject();
    }

    @Bean
    IntegrationFlow failure() {

        return f -> f.handle( System.out::println );
    }

}
