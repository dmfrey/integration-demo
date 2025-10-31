package com.broadcom.springconsulting.integrationdemo;

import com.broadcom.springconsulting.integrationdemo.sftp.EmbeddedSftpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.inbound.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.util.Map;

@TestConfiguration( proxyBeanMethods = false )
public class TestcontainersConfiguration {

    private static final Logger log = LoggerFactory.getLogger( TestcontainersConfiguration.class );

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {

        return new PostgreSQLContainer<>( DockerImageName.parse( "postgres:latest" ) );
    }

    @Bean( initMethod = "start", destroyMethod = "stop" )
    EmbeddedSftpServer port62281() throws Exception {

        var directory = System.getProperty( "user.dir" ) + File.separator + "sftp_server";

        var sftpServer = new EmbeddedSftpServer();
        sftpServer.setName( "Port62281" );
        sftpServer.setPort( 62281 );
        sftpServer.setDirectory( directory );
        sftpServer.afterPropertiesSet();

        return sftpServer;
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
        adapter.setOutputChannelName( "port1234OutputChannel.input" );

        return adapter;
    }

//    @Bean
//    IntegrationFlow port1234() {
//
//        return f -> f
//                .handle(
//                        Tcp.inboundGateway(
//                                Tcp.nioServer(1234 )
//                                        .deserializer( TcpCodecs.lengthHeader1() )
//                                        .serializer( TcpCodecs.lengthHeader1() )
//                                        .backlog( 30 )
//                        )
//                                .errorChannel( "tcpIn.errorChannel" )
//                                .id( "tcpIn" )
//                )
//                .log( LoggingHandler.Level.INFO )
//                .channel( "port1234OutputChannel.input" );
//    }

    @Bean
    IntegrationFlow port1234OutputChannel() {

        final var base = System.getProperty( "user.dir" ) + File.separator + "build" + File.separator + "tcp" + File.separator;

        return f -> f
                .enrichHeaders( Map.of( "server-name", "port1234" ) )
                .handle(
                        Files.outboundAdapter( m -> base + m.getHeaders().get( "server-name" ) )
                                .autoCreateDirectory( true )
                                .fileNameExpression( "headers['file_name']" )
                );
    }

}
