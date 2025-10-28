package com.broadcom.springconsulting.integrationdemo.machine.application.domain.service;


import com.broadcom.springconsulting.integrationdemo.machine.application.domain.model.Gateway;
import com.broadcom.springconsulting.integrationdemo.machine.application.port.in.UpdateGatewayUseCase;
import com.broadcom.springconsulting.integrationdemo.machine.application.port.out.GatewayPort;
import org.springframework.stereotype.Component;

@Component
class UpdateGatewayService implements UpdateGatewayUseCase {
    private final GatewayPort gatewayPort;

    UpdateGatewayService(GatewayPort gatewayPort) {
        this.gatewayPort = gatewayPort;
    }

    @Override
    public Gateway execute(UpdateGatewayUseCase.UpdateGatewayRecord record) {

        var gateway = new Gateway(
                record.id(),
                record.name(),
                record.connectionType(),
                record.hostname(),
                record.port(),
                record.username(),
                record.password(),
                record.remoteDirectory()
        );

        return this.gatewayPort.updateGateway(gateway);
    }
}
