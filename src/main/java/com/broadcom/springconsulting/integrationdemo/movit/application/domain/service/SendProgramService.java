package com.broadcom.springconsulting.integrationdemo.movit.application.domain.service;

import com.broadcom.springconsulting.integrationdemo.machine.application.port.in.GetServerUseCase;
import com.broadcom.springconsulting.integrationdemo.movit.application.port.in.SendProgramUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Component
class SendProgramService implements SendProgramUseCase {

    private static final Logger log = LoggerFactory.getLogger( SendProgramService.class );

    private final GetServerUseCase getServer;
    private final IntegrationFlow movit;

    SendProgramService( GetServerUseCase getServer, IntegrationFlow movit ) {
        this.getServer = getServer;
        this.movit = movit;
    }

    @Override
    public void execute( SendProgramCommand command ) {

        this.getServer.execute( new GetServerUseCase.GetServerQuery( command.port() ) )
                .ifPresentOrElse( gateway -> {

//                    var file = new ClassPathResource( "sample/bbb_sunflower_2160p_60fps_normal.mp4" );
                    var file = new ClassPathResource( "sample/test-data.csv" );

                    try {

                        var path = Paths.get( file.getFile().getAbsolutePath() );
                        var bytes = Files.readAllBytes( path );

                        var message =
                                MessageBuilder
                                        .withPayload( file.getInputStream() )
                                        .setHeader( "send-channel", gateway.connectionType() )
                                        .setHeader( "host", gateway.hostname() )
                                        .setHeader( "port", gateway.port() )
                                        .setHeader( "user", gateway.username() )
                                        .setHeader( "password", gateway.password() )
                                        .setHeader( "remoteDirectory", gateway.remoteDirectory() )
                                        .build();

                        Objects.requireNonNull( this.movit.getInputChannel() ).send( message );

                    } catch( IOException e ) {
                        log.error( "Error reading file", e );

                        throw new RuntimeException( e );
                    }

                },
                        () -> { throw new RuntimeException( "Gateway not found!" ); }
                );

    }

}
