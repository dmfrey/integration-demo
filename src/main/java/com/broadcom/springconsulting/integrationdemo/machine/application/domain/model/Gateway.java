package com.broadcom.springconsulting.integrationdemo.machine.application.domain.model;

public record Gateway(

        Long id,
        String name,
        String connectionDirection,
        String connectionType,
        String hostname,
        Integer port,
        String username,
        String password,
        String remoteDirectory

) { }
