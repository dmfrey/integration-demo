package com.broadcom.springconsulting.integrationdemo.movit.application.domain.service;

import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.in.FindMachineInterfaceByPortUseCase;
import com.broadcom.springconsulting.integrationdemo.movit.application.domain.model.DownloadComplete;
import com.broadcom.springconsulting.integrationdemo.movit.application.domain.model.MachineInterfaceHeaders;
import com.broadcom.springconsulting.integrationdemo.movit.application.domain.model.ServerHeaders;
import com.broadcom.springconsulting.integrationdemo.movit.application.port.in.SendProgramUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.FileHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
    public DownloadComplete execute(SendProgramCommand command ) {

        var replyChannel = new QueueChannel();
        var future = CompletableFuture.supplyAsync(() -> replyChannel.receive( 10000 ) );

        var serverGateway = this.usecase.execute( new FindMachineInterfaceByPortUseCase.FindByPortCommand( 62281 ) ).get();

        this.usecase.execute( new FindMachineInterfaceByPortUseCase.FindByPortCommand( command.port() ) )
                .ifPresentOrElse( gateway -> {

                    var message =
                            MessageBuilder
                                    .withPayload( serverGateway.remoteDirectory() + command.filename() )
                                    .setHeader( FileHeaders.FILENAME, command.filename() )
                                    .setHeader( "send-channel", gateway.connectionType() )
                                    .setHeader( MachineInterfaceHeaders.NAME, gateway.name() )
                                    .setHeader( MachineInterfaceHeaders.HOST, gateway.hostname() )
                                    .setHeader( MachineInterfaceHeaders.PORT, gateway.port() )
                                    .setHeader( MachineInterfaceHeaders.USER, gateway.username() )
                                    .setHeader( MachineInterfaceHeaders.PASSWORD, gateway.password() )
                                    .setHeader( MachineInterfaceHeaders.REMOTE_DIRECTORY, gateway.remoteDirectory() )
                                    .setHeader( "retrieve-channel", serverGateway.connectionType() )
                                    .setHeader( ServerHeaders.NAME, serverGateway.name() )
                                    .setHeader( ServerHeaders.HOST, serverGateway.hostname() )
                                    .setHeader( ServerHeaders.PORT, serverGateway.port() )
                                    .setHeader( ServerHeaders.USER, serverGateway.username() )
                                    .setHeader( ServerHeaders.PASSWORD, serverGateway.password() )
                                    .setHeader( ServerHeaders.REMOTE_DIRECTORY, serverGateway.remoteDirectory() )
                                    .setReplyChannel( replyChannel )
                                    .build();

                    Objects.requireNonNull( this.retrievit.getInputChannel() ).send( message );

                }, () -> { throw new RuntimeException( "An error occurred sending the program to the selected machine interface." ); }
                );

        try {

            var reply = future.get();
            log.info( "Message Reply: {}", reply );

            var name = reply.getHeaders().get( MachineInterfaceHeaders.NAME, String.class );
            var host = reply.getHeaders().get( MachineInterfaceHeaders.HOST, String.class );
            var port = reply.getHeaders().get( MachineInterfaceHeaders.PORT, Integer.class );
            return new DownloadComplete(
                    name,
                    port,
                    "File Download complete @" + name + "[" + host + ":" + port + "] at location: " + reply.getPayload()
            );

        } catch( InterruptedException | ExecutionException e ) {

            throw new RuntimeException( e );
        }

    }

}
