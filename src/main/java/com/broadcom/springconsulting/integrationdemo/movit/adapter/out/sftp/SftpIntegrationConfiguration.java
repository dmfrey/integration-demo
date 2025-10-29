package com.broadcom.springconsulting.integrationdemo.movit.adapter.out.sftp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.integration.sftp.dsl.Sftp;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;

@Configuration( proxyBeanMethods = false )
class SftpIntegrationConfiguration {

    private static final Logger log = LoggerFactory.getLogger( SftpIntegrationConfiguration.class );

    @Bean
    IntegrationFlow sendsftp() {

        return f -> f.route( new SftpMessageRouter() );
    }

    static class SftpMessageRouter extends AbstractMessageRouter {

        @Autowired
        private IntegrationFlowContext flowContext;

        @Override
        protected Collection<MessageChannel> determineTargetChannels(Message<?> message ) {

            var hostPortFlow = message.getHeaders().get("host", String.class ) + message.getHeaders().get( "port" ) + ".flow";

            if( this.flowContext.getRegistry().containsKey( hostPortFlow ) ) {
                log.debug( "Retrieving existing SFTP channel: [{}]", hostPortFlow );

                var channel = this.flowContext.getRegistry().get( hostPortFlow ).getInputChannel();
                return Collections.singleton( channel );

            } else {
                log.debug( "Registering SFTP channel: [{}]", hostPortFlow );

                var channel = createNewSubflow( message ).getInputChannel();
                return Collections.singleton( channel );
            }

        }

        private IntegrationFlowContext.IntegrationFlowRegistration createNewSubflow( Message<?> message ) {

            var host = (String) message.getHeaders().get( "host" );
            var port = (Integer) message.getHeaders().get( "port" );
            var user = (String) message.getHeaders().get( "user" );
            var password = (String) message.getHeaders().get( "password" );
            var remoteDirectory = (String) message.getHeaders().get( "remoteDirectory" );

            Assert.state(host != null && port != null && user != null && password != null && remoteDirectory != null, "sft connection details missing" );

            var hostPortFlow = host + port  + ".flow";

            var sftpSessionFactory = new DefaultSftpSessionFactory();
            sftpSessionFactory.setHost( host );
            sftpSessionFactory.setPort( port );
            sftpSessionFactory.setUser( user );
            sftpSessionFactory.setPassword( password );
            sftpSessionFactory.setAllowUnknownKeys( true );

            IntegrationFlow flow = f -> f.handle( Sftp.outboundAdapter( sftpSessionFactory, FileExistsMode.REPLACE ).remoteDirectory( remoteDirectory ) );

            return this.flowContext.registration( flow )
                    .addBean( sftpSessionFactory )
                    .id( hostPortFlow )
                    .register();
        }

    }

}

