package com.broadcom.springconsulting.integrationdemo;

import com.broadcom.springconsulting.integrationdemo.sftp.EmbeddedSftpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.connection.TcpMessageMapper;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;
import org.springframework.messaging.MessageChannel;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;

@TestConfiguration( proxyBeanMethods = false )
public class TestcontainersConfiguration {

    private static final Logger log = LoggerFactory.getLogger( TestcontainersConfiguration.class );

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {

        return new PostgreSQLContainer<>( DockerImageName.parse( "postgres:latest" ) );
    }

    @Bean( initMethod = "start", destroyMethod = "stop" )
    EmbeddedSftpServer port62282() throws Exception {

        var sftpServer = new EmbeddedSftpServer();
        sftpServer.setName( "Port62282" );
        sftpServer.setPort( 62282 );
        sftpServer.afterPropertiesSet();

        return sftpServer;
    }

    @Bean( initMethod = "start", destroyMethod = "stop" )
    EmbeddedSftpServer port62283() throws Exception {

        var sftpServer = new EmbeddedSftpServer();
        sftpServer.setName( "Port62283" );
        sftpServer.setPort( 62283 );
        sftpServer.afterPropertiesSet();

        return sftpServer;
    }

    @Bean
    TcpNetServerConnectionFactory tcpNetServerConnectionFactory() {

        var cf = new TcpNetServerConnectionFactory(1234 );
        cf.setDeserializer( new ByteArrayLengthHeaderSerializer( ByteArrayLengthHeaderSerializer.HEADER_SIZE_UNSIGNED_SHORT ) );

        return cf;
    }

    @Bean
    TcpReceivingChannelAdapter port1234( TcpNetServerConnectionFactory tcpNetServerConnectionFactory ) {

        var adapter = new TcpReceivingChannelAdapter();
        adapter.setConnectionFactory( tcpNetServerConnectionFactory );
        adapter.setOutputChannelName( "outputChannel.input" );

        return adapter;
    }

    @Bean
    IntegrationFlow outputChannel() {

        final var base = System.getProperty( "user.dir" ) + File.separator + "build" + File.separator + "tcp" + File.separator;

        return f -> f
                .handle(
                        Files.outboundAdapter( m -> base + m.getHeaders().get( "name" ) )
                                .autoCreateDirectory( true )
                );
    }

}
