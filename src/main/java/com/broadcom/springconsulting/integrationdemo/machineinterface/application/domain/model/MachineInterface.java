package com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.model;

public record MachineInterface(

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
