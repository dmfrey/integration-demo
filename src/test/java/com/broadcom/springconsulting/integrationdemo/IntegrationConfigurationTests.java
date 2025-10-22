package com.broadcom.springconsulting.integrationdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(BackupIntegrationConfiguration.class)
@SpringIntegrationTest
public class IntegrationConfigurationTests {

    @Autowired
    @Qualifier( "fileMover.input" )
    DirectChannel fileMover;

    @Autowired
    @Qualifier( "success" )
    QueueChannel output;

    @Test
    void test() throws IOException {

        var sampleData = new ClassPathResource( "sample/test-data.csv" );
        var fakeMessage =
                MessageBuilder
                        .withPayload( sampleData.getInputStream() )
                        .setHeader( FileHeaders.FILENAME, sampleData.getFilename() )
                        .setHeader( "target", "server1" )
                        .build();
        this.fileMover.send( fakeMessage );

        var message = (Message<?>) this.output.receive( 10_000L );
        assertThat( message ).isNotNull();
        assertThat( message.getPayload().toString() ).isEqualTo( sampleData.getFilename() + " sent successfully!" );

    }

}
