package com.broadcom.springconsulting.integrationdemo.sftp;

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.config.keys.AuthorizedKeysAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

public class EmbeddedSftpServer implements InitializingBean, SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger( EmbeddedSftpServer.class );

    private static final String BUILD = System.getProperty( "user.dir" ) + File.separator + "build" + File.separator + "sftp";

    /**
     * Let OS obtain the proper port
     */
    public static final int PORT = 0;

    private final SshServer server = SshServer.setUpDefaultServer();

    private volatile String name;

    private volatile int port;

    private volatile String directory = BUILD;

    private volatile boolean running;

    public void setName( String name ) {

        this.name = name;

    }

    public void setPort( int port ) {

        this.port = port;

    }

    public void setDirectory( String directory ) {

        this.directory = directory;

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        this.server.setPublickeyAuthenticator( getPublickeyAuthenticator() );
        this.server.setPasswordAuthenticator( new AcceptAllUsersPasswordAuthenticator() );
        this.server.setPort( this.port );
        this.server.setKeyPairProvider( new SimpleGeneratorHostKeyProvider( new File( "hostkey.ser" ).toPath() ) );
        this.server.setSubsystemFactories( Collections.singletonList( new SftpSubsystemFactory() ) );
        final String pathname = directory + File.separator + name;
        new File( pathname ).mkdirs();
        this.server.setFileSystemFactory( new VirtualFileSystemFactory( Paths.get( pathname ) ) );

    }

    private PublickeyAuthenticator getPublickeyAuthenticator() throws Exception {

        var path = new ClassPathResource( "META-INF/keys/sftp_known_hosts" ).getFile().toPath();

        return new AuthorizedKeysAuthenticator( path );
    }

    @Override
    public boolean isAutoStartup() {

        return PORT == this.port;
    }

    @Override
    public int getPhase() {

        return Integer.MAX_VALUE;
    }

    @Override
    public void start() {

        try {

            this.server.start();
            this.running = true;

            log.info( "SFTP server '{}' started on port [{}]", name,  this.server.getPort() );

        } catch( IOException e ) {

            throw new IllegalStateException( e );
        }

    }

    @Override
    public void stop( Runnable callback ) {

        stop();
        callback.run();

        log.info( "SFTP server '{}' stopped", name );

    }

    @Override
    public void stop() {

        if( this.running) {

            try {

                server.stop( true );

            } catch( Exception e ) {

                throw new IllegalStateException( e );
            } finally {

                this.running = false;

            }

        }

    }

    @Override
    public boolean isRunning() {

        return this.running;
    }

    class AcceptAllUsersPasswordAuthenticator implements PasswordAuthenticator {

        @Override
        public boolean authenticate( String s, String s1, ServerSession serverSession ) throws PasswordChangeRequiredException, AsyncAuthException {

            return true;
        }

        @Override
        public boolean handleClientPasswordChangeRequest( ServerSession session, String username, String oldPassword, String newPassword ) {

            return true;
        }

    }

}
