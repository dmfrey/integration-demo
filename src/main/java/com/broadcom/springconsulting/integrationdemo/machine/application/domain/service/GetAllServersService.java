package com.broadcom.springconsulting.integrationdemo.machine.application.domain.service;

import com.broadcom.springconsulting.integrationdemo.machine.application.domain.model.Gateway;
import com.broadcom.springconsulting.integrationdemo.machine.application.port.in.GetAllServersUseCase;
import com.broadcom.springconsulting.integrationdemo.machine.application.port.out.GatewayPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class GetAllServersService implements GetAllServersUseCase {

    private final GatewayPort gatewayPort;

    GetAllServersService( GatewayPort gatewayPort ) {

        this.gatewayPort = gatewayPort;

    }

    @Override
    public List<Gateway> execute( GetAllServersCommand command ) {

        return this.gatewayPort.findAllServers();
    }

}
