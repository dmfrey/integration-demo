package com.broadcom.springconsulting.integrationdemo.machine.adapter.out.persistence;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;

@Table( name = "gateway" )
record GatewayEntity(

        @Id
        Long id,

        @Column( "name" )
        @NotEmpty
        @Size( max = 200 )
        String name,

        @Column( "connection_direction" )
        @NotNull
        ConnectionDirection connectionDirection,

        @Column( "connection_type" )
        @NotNull
        ConnectionType connectionType,

        @Column( "server_hostname" )
        @NotEmpty
        @Size( max = 200 )
        String serverHostname,

        @Column( "server_port" )
        @NotNull
        Integer port,

        @Column( "server_username" )
        @NotEmpty
        @Size( max = 100 )
        String username,

        @Column( "server_password" )
        @NotEmpty
        @Size( max = 100 )
        String password,

        @Column( "remote_directory" )
        @NotEmpty
        @Size( max = 100 )
        String remoteDirectory

) { }
