package com.broadcom.springconsulting.integrationdemo.movit.adapter.out.tcp;

import com.broadcom.springconsulting.integrationdemo.movit.application.domain.model.MachineInterfaceHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.tcp.serializer.TcpCodecs;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;

@Configuration( proxyBeanMethods = false )
class TcpIntegrationConfiguration {

    private static final Logger log = LoggerFactory.getLogger( TcpIntegrationConfiguration.class );

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

            var host = (String) message.getHeaders().get( MachineInterfaceHeaders.HOST );
            var port = (Integer) message.getHeaders().get( MachineInterfaceHeaders.PORT );

            Assert.state(host != null && port != null, "tcp host and port are required!" );

            var hostPortFlow = host + port  + ".flow";

//            var tcpConnectionFactory = new TcpNetClientConnectionFactory( host, port );
//            tcpConnectionFactory.setSerializer( new ByteArrayLengthHeaderSerializer( ByteArrayLengthHeaderSerializer.HEADER_SIZE_UNSIGNED_SHORT ) );
//
//            var tcpSendingMessageHandler = new TcpSendingMessageHandler();
//            tcpSendingMessageHandler.setConnectionFactory( tcpConnectionFactory );
//
//            IntegrationFlow flow = f -> f.handle( tcpSendingMessageHandler );
            IntegrationFlow flow = f -> f
                    .handle(
                            Tcp.outboundGateway(
                                    Tcp.nioClient( host, port )
                                            .deserializer( TcpCodecs.lengthHeader1() )
                                            .serializer( TcpCodecs.lengthHeader1() )
                            )
                    );

            return this.flowContext.registration( flow )
//                    .addBean( tcpConnectionFactory )
                    .id( hostPortFlow )
                    .register();
        }

    }

}
