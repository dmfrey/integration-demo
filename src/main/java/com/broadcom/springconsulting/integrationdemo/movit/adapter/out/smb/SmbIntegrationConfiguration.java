package com.broadcom.springconsulting.integrationdemo.movit.adapter.out.smb;

import com.broadcom.springconsulting.integrationdemo.movit.application.domain.model.MachineInterfaceHeaders;
import jcifs.DialectVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.integration.smb.dsl.Smb;
import org.springframework.integration.smb.session.SmbSessionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;

@Configuration( proxyBeanMethods = false )
class SmbIntegrationConfiguration {

    private static final Logger log = LoggerFactory.getLogger( SmbIntegrationConfiguration.class );

    @Bean
    IntegrationFlow sendsmb() {

        return f -> f
                .route( new SmbMessageRouter() );
    }

    static class SmbMessageRouter extends AbstractMessageRouter {

        @Autowired
        private IntegrationFlowContext flowContext;

        @Override
        protected Collection<MessageChannel> determineTargetChannels( Message<?> message ) {

            var hostPortFlow = message.getHeaders().get(MachineInterfaceHeaders.HOST, String.class ) + message.getHeaders().get( MachineInterfaceHeaders.PORT ) + ".flow";

            if( this.flowContext.getRegistry().containsKey( hostPortFlow ) ) {
                log.debug( "Retrieving existing TCP channel: [{}]", hostPortFlow );

                var channel = this.flowContext.getRegistry().get( hostPortFlow ).getInputChannel();
                return Collections.singleton( channel );

            } else {
                log.debug( "Registering TCP channel: [{}]", hostPortFlow );

                var channel = createNewSubflow( message ).getInputChannel();
                return Collections.singleton( channel );
            }

        }

        private IntegrationFlowContext.IntegrationFlowRegistration createNewSubflow( Message<?> message ) {

            var name = (String) message.getHeaders().get( MachineInterfaceHeaders.NAME );
            var host = (String) message.getHeaders().get( MachineInterfaceHeaders.HOST );
            var port = (Integer) message.getHeaders().get( MachineInterfaceHeaders.PORT );
            var user = (String) message.getHeaders().get( MachineInterfaceHeaders.USER );
            var password = (String) message.getHeaders().get( MachineInterfaceHeaders.PASSWORD );
            var remoteDirectory = (String) message.getHeaders().get( MachineInterfaceHeaders.REMOTE_DIRECTORY );

            Assert.state(host != null && port != null && user != null && password != null && remoteDirectory != null, "smb connection details missing" );

            var hostPortFlow = host + port  + ".flow";

            var smbSessionFactory = new SmbSessionFactory();
            smbSessionFactory.setHost( host );
            smbSessionFactory.setPort( port );
            smbSessionFactory.setDomain( "foo" );
            smbSessionFactory.setUsername( user );
            smbSessionFactory.setPassword( password );
            smbSessionFactory.setShareAndDir( "share" );
            smbSessionFactory.setSmbMinVersion( DialectVersion.SMB1 );
            smbSessionFactory.setSmbMaxVersion( DialectVersion.SMB210 );

            IntegrationFlow flow = f -> f
                    .handle( Smb.outboundAdapter( smbSessionFactory, FileExistsMode.REPLACE )
                            .fileNameExpression("headers['" + FileHeaders.FILENAME + "']")
                            .remoteDirectory( name ) // remoteDirectory )
                    );

            return this.flowContext.registration( flow )
                    .addBean( smbSessionFactory )
                    .id( hostPortFlow )
                    .register();
        }

    }

}
