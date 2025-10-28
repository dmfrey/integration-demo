package com.broadcom.springconsulting.integrationdemo;

import jcifs.DialectVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.remote.gateway.AbstractRemoteFileOutboundGateway;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.ip.tcp.outbound.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.integration.sftp.dsl.Sftp;
import org.springframework.integration.sftp.outbound.SftpOutboundGateway;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.smb.dsl.Smb;
import org.springframework.integration.smb.session.SmbSessionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Configuration( proxyBeanMethods = false )
@EnableIntegration
public class IntegrationConfiguration {

    private static final Logger log = LoggerFactory.getLogger( IntegrationConfiguration.class );

    @Configuration( proxyBeanMethods = false )
    static class MovitIntegrationConfiguration {

        @Bean
        IntegrationFlow retrievit() {

            return f -> f
                    .routeToRecipients(r -> r
                            .recipient( "retrievesftp.input", "headers['retrieve-channel'] == 'SFTP'" )
//                            .recipient( "retrievesmb.input", "headers['retrieve-channel'] == 'SMB'" )
                    );
        }

        // Client initiates call to request to move any payload to any gateway
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

    @Configuration( proxyBeanMethods = false )
    static class SftpServerIntegrationConfiguration {

        @Bean
        IntegrationFlow retrievesftp() {

            return f -> f.route( new SftpServerMessageRouter() );
        }

        static class SftpServerMessageRouter extends AbstractMessageRouter {

            @Autowired
            IntegrationFlow movit;

            @Autowired
            private IntegrationFlowContext flowContext;

            @Override
            protected Collection<MessageChannel> determineTargetChannels( Message<?> message ) {

                var hostPortFlow = message.getHeaders().get("server_host", String.class ) + message.getHeaders().get( "server_port" ) + ".flow";

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

                var name = (String) message.getHeaders().get( "server_name" );
                var host = (String) message.getHeaders().get( "server_host" );
                var port = (Integer) message.getHeaders().get( "server_port" );
                var user = (String) message.getHeaders().get( "server_user" );
                var password = (String) message.getHeaders().get( "server_password" );
                var remoteDirectory = (String) message.getHeaders().get( "server_remoteDirectory" );

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
                        )
                        .channel( Objects.requireNonNull( movit.getInputChannel() ) );

                return this.flowContext.registration( flow )
                        .addBean( ( name + "SessionFactory" ), sftpSessionFactory )
                        .id( hostPortFlow )
                        .register();
            }

        }

    }

    @Configuration( proxyBeanMethods = false )
    static class SftpIntegrationConfiguration {

        @Bean
        IntegrationFlow sendsftp() {

            return f -> f.route( new SftpMessageRouter() );
        }

        static class SftpMessageRouter extends AbstractMessageRouter {

            @Autowired
            private IntegrationFlowContext flowContext;

            @Override
            protected Collection<MessageChannel> determineTargetChannels( Message<?> message ) {

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

            private IntegrationFlowContext.IntegrationFlowRegistration createNewSubflow(Message<?> message ) {

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

    @Configuration( proxyBeanMethods = false )
    static class SmbIntegrationConfiguration {

        private BeanFactory beanFactory;

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

                var hostPortFlow = message.getHeaders().get("host", String.class ) + message.getHeaders().get( "port" ) + ".flow";

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

                var name = (String) message.getHeaders().get( "name" );
                var host = (String) message.getHeaders().get( "host" );
                var port = (Integer) message.getHeaders().get( "port" );
                var user = (String) message.getHeaders().get( "user" );
                var password = (String) message.getHeaders().get( "password" );
                var remoteDirectory = (String) message.getHeaders().get( "remoteDirectory" );

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

    @Configuration( proxyBeanMethods = false )
    static class TcpIntegrationConfiguration {

        private BeanFactory beanFactory;

        @Bean
        IntegrationFlow sendtcp() {

            return f -> f
                    .route( new TcpMessageRouter() );
        }

        static class TcpMessageRouter extends AbstractMessageRouter {

            @Autowired
            private IntegrationFlowContext flowContext;

            @Override
            protected Collection<MessageChannel> determineTargetChannels( Message<?> message ) {

                var hostPortFlow = message.getHeaders().get("host", String.class ) + message.getHeaders().get( "port" ) + ".flow";

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

                var host = (String) message.getHeaders().get( "host" );
                var port = (Integer) message.getHeaders().get( "port" );

                Assert.state(host != null && port != null, "tcp host and port are required!" );

                var hostPortFlow = host + port  + ".flow";

                var tcpConnectionFactory = new TcpNetClientConnectionFactory( host, port );
                tcpConnectionFactory.setSerializer( new ByteArrayLengthHeaderSerializer( ByteArrayLengthHeaderSerializer.HEADER_SIZE_UNSIGNED_SHORT ) );

                var tcpSendingMessageHandler = new TcpSendingMessageHandler();
                tcpSendingMessageHandler.setConnectionFactory( tcpConnectionFactory );

                IntegrationFlow flow = f -> f.handle( tcpSendingMessageHandler );

                return this.flowContext.registration( flow )
                        .addBean( tcpConnectionFactory )
                        .id( hostPortFlow )
                        .register();
            }

        }

    }

}
