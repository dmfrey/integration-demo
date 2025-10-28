package com.broadcom.springconsulting.integrationdemo.movit.application.domain.service;

import com.broadcom.springconsulting.integrationdemo.machine.application.port.in.GetAllGatewayUseCase;
import com.broadcom.springconsulting.integrationdemo.machine.application.port.in.GetAllServersUseCase;
import com.broadcom.springconsulting.integrationdemo.movit.application.domain.model.Gateway;
import com.broadcom.springconsulting.integrationdemo.movit.application.domain.model.Server;
import com.broadcom.springconsulting.integrationdemo.movit.application.port.in.PrepareRequestDownloadUseCase;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
class PrepareRequestDownloadService implements PrepareRequestDownloadUseCase {

    private final GetAllServersUseCase getAllServersUseCase;
    private final GetAllGatewayUseCase getAllGatewayUseCase;

    PrepareRequestDownloadService( GetAllServersUseCase getAllServersUseCase, GetAllGatewayUseCase getAllGatewayUseCase ) {

        this.getAllServersUseCase = getAllServersUseCase;
        this.getAllGatewayUseCase = getAllGatewayUseCase;

    }

    @Override
    public Map<String, Object> execute(PrepareRequestDownloadCommand command) {

        var servers = this.getAllServersUseCase.execute( new GetAllServersUseCase.GetAllServersCommand() ).stream()
                .map( server -> new Server( server.name(), server.port() ) );
        var gateways = this.getAllGatewayUseCase.execute( new GetAllGatewayUseCase.GetAllGatewaysCommand() ).stream()
                .map( gateway -> new Gateway( gateway.name(), gateway.port() ) );

        return Map.of( "servers", servers, "gateways", gateways );
    }

}
