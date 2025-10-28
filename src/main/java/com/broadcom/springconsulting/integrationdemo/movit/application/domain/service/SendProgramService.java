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
    private final IntegrationFlow retrievit;

    SendProgramService( GetServerUseCase getServer, IntegrationFlow retrievit ) {
        this.getServer = getServer;
        this.retrievit = retrievit;
    }

    @Override
    public void execute( SendProgramCommand command ) {

        var serverGateway = this.getServer.execute( new GetServerUseCase.GetServerQuery( 62281 ) ).get();

        this.getServer.execute( new GetServerUseCase.GetServerQuery( command.port() ) )
                .ifPresentOrElse( gateway -> {

//                    var file = new ClassPathResource( "sample/bbb_sunflower_2160p_60fps_normal.mp4" );
//                    var file = new ClassPathResource( "sample/test-data.csv" );

//                    try {

//                        var path = Paths.get( file.getFile().getAbsolutePath() );
//                        var bytes = Files.readAllBytes( path );

                        var message =
                                MessageBuilder
                                        .withPayload( serverGateway.remoteDirectory() + command.filename() )
//                                        .setHeader( "file_name", file.getFilename() )
                                        .setHeader( "file_name", command.filename() )
                                        .setHeader( "send-channel", gateway.connectionType() )
                                        .setHeader( "name", gateway.name() )
                                        .setHeader( "host", gateway.hostname() )
                                        .setHeader( "port", gateway.port() )
                                        .setHeader( "user", gateway.username() )
                                        .setHeader( "password", gateway.password() )
                                        .setHeader( "remoteDirectory", gateway.remoteDirectory() )
                                        .setHeader( "retrieve-channel", serverGateway.connectionType() )
                                        .setHeader( "server_name", serverGateway.name() )
                                        .setHeader( "server_host", serverGateway.hostname() )
                                        .setHeader( "server_port", serverGateway.port() )
                                        .setHeader( "server_user", serverGateway.username() )
                                        .setHeader( "server_password", serverGateway.password() )
                                        .setHeader( "server_remoteDirectory", serverGateway.remoteDirectory() )
                                        .build();

                        Objects.requireNonNull( this.retrievit.getInputChannel() ).send( message );

//                    } catch( IOException e ) {
//                        log.error( "Error reading file", e );
//
//                        throw new RuntimeException( e );
//                    }

                },
                        () -> { throw new RuntimeException( "Gateway not found!" ); }
                );

    }

}
