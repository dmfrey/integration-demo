package com.broadcom.springconsulting.integrationdemo.movit.application.domain.service;

import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.in.FindMachineInterfaceByPortUseCase;
import com.broadcom.springconsulting.integrationdemo.movit.application.port.in.SendProgramUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class SendProgramService implements SendProgramUseCase {

    private static final Logger log = LoggerFactory.getLogger( SendProgramService.class );

    private final FindMachineInterfaceByPortUseCase usecase;
    private final IntegrationFlow retrievit;

    SendProgramService( FindMachineInterfaceByPortUseCase usecase, IntegrationFlow retrievit ) {
        this.usecase = usecase;
        this.retrievit = retrievit;
    }

    @Override
    public void execute( SendProgramCommand command ) {

        var serverGateway = this.usecase.execute( new FindMachineInterfaceByPortUseCase.FindByPortCommand( 62281 ) ).get();

        this.usecase.execute( new FindMachineInterfaceByPortUseCase.FindByPortCommand( command.port() ) )
                .ifPresentOrElse( gateway -> {

                        var message =
                                MessageBuilder
                                        .withPayload( serverGateway.remoteDirectory() + command.filename() )
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

                },
                        () -> { throw new RuntimeException( "Gateway not found!" ); }
                );

    }

}
