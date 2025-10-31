package com.broadcom.springconsulting.integrationdemo.movit.adapter.in.sftp;

import com.broadcom.springconsulting.integrationdemo.movit.application.domain.model.ServerHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.file.remote.gateway.AbstractRemoteFileOutboundGateway;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.integration.sftp.dsl.Sftp;
import org.springframework.integration.sftp.outbound.SftpOutboundGateway;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Configuration( proxyBeanMethods = false )
class SftpServerIntegrationConfiguration {

    @Bean
    IntegrationFlow retrievesftp() {

        return f -> f.route( new SftpServerMessageRouter() );
    }

    static class SftpServerMessageRouter extends AbstractMessageRouter {

        private static final Logger log = LoggerFactory.getLogger( SftpServerIntegrationConfiguration.class );

        private static final String TMP = System.getProperty( "user.dir" ) + File.separator + "build" + File.separator + "tmp";

        @Autowired
        IntegrationFlow movit;

        @Autowired
        private IntegrationFlowContext flowContext;

        @Override
        protected Collection<MessageChannel> determineTargetChannels( Message<?> message ) {

            var hostPortFlow = message.getHeaders().get( ServerHeaders.HOST, String.class ) + message.getHeaders().get( ServerHeaders.PORT ) + ".flow";

            if( this.flowContext.getRegistry().containsKey( hostPortFlow ) ) {
                log.debug( "Retrieving existing SFTP Server channel: [{}]", hostPortFlow );

                var channel = this.flowContext.getRegistry().get( hostPortFlow ).getInputChannel();
                return Collections.singleton( channel );

            } else {
                log.debug( "Registering SFTP Server channel: [{}]", hostPortFlow );

                var channel = createNewSubflow( message ).getInputChannel();
                return Collections.singleton( channel );
            }

        }

        private IntegrationFlowContext.IntegrationFlowRegistration createNewSubflow( Message<?> message ) {

            var name = (String) message.getHeaders().get( ServerHeaders.NAME );
            var host = (String) message.getHeaders().get( ServerHeaders.HOST );
            var port = (Integer) message.getHeaders().get( ServerHeaders.PORT );
            var user = (String) message.getHeaders().get( ServerHeaders.USER );
            var password = (String) message.getHeaders().get( ServerHeaders.PASSWORD );
            var remoteDirectory = (String) message.getHeaders().get( ServerHeaders.REMOTE_DIRECTORY );

            Assert.state(host != null && port != null && user != null && password != null && remoteDirectory != null, "sft connection details missing" );

            var hostPortFlow = host + port  + ".flow";

            var sftpSessionFactory = new DefaultSftpSessionFactory();
            sftpSessionFactory.setHost( host );
            sftpSessionFactory.setPort( port );
            sftpSessionFactory.setUser( user );
            sftpSessionFactory.setPassword( password );
            sftpSessionFactory.setAllowUnknownKeys( true );

            IntegrationFlow flow = f -> f
                    .handle( Sftp.outboundGateway( sftpSessionFactory, SftpOutboundGateway.Command.GET, "payload" )
                            .options( AbstractRemoteFileOutboundGateway.Option.STREAM )
//                                    .remoteDirectoryFunction( message1 -> remoteDirectory )
//                                    .localDirectory( new File( TMP ) )
//                                    .autoCreateLocalDirectory( true )
                    )
                    .channel( Objects.requireNonNull( movit.getInputChannel() ) );

            return this.flowContext.registration( flow )
                    .addBean( sftpSessionFactory )
                    .id( hostPortFlow )
                    .register();
        }

    }

}
