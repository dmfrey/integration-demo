package com.broadcom.springconsulting.integrationdemo.machine.application.domain.service;

import com.broadcom.springconsulting.integrationdemo.machine.application.domain.model.Gateway;
import com.broadcom.springconsulting.integrationdemo.machine.application.port.in.GetAllGatewayUseCase;
import com.broadcom.springconsulting.integrationdemo.machine.application.port.out.GatewayPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class GetAllGatewayService implements GetAllGatewayUseCase {

    private final GatewayPort gatewayPort;

    GetAllGatewayService( GatewayPort gatewayPort ) {

        this.gatewayPort = gatewayPort;

    }

    @Override
    public List<Gateway> execute( GetAllGatewaysCommand command ) {

        return this.gatewayPort.findAllOutboundGateways();
    }

}
