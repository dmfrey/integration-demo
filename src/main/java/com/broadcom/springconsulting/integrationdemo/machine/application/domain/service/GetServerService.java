package com.broadcom.springconsulting.integrationdemo.machine.application.domain.service;

import com.broadcom.springconsulting.integrationdemo.machine.application.domain.model.Gateway;
import com.broadcom.springconsulting.integrationdemo.machine.application.port.in.GetServerUseCase;
import com.broadcom.springconsulting.integrationdemo.machine.application.port.out.GatewayPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class GetServerService implements GetServerUseCase {

    private final GatewayPort gatewayPort;

    GetServerService( GatewayPort gatewayPort ) {

        this.gatewayPort = gatewayPort;

    }

    @Override
    public Optional<Gateway> execute( GetServerQuery query ) {

        return Optional.ofNullable( this.gatewayPort.findByPort( query.port() ) );
    }

}
