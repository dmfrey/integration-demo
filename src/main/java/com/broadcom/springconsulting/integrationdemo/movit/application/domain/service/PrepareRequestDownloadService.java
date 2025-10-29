package com.broadcom.springconsulting.integrationdemo.movit.application.domain.service;

import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.in.GetAllOutboundMachineInterfacesUseCase;
import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.in.GetAllInboundMachineInterfacesUseCase;
import com.broadcom.springconsulting.integrationdemo.movit.application.domain.model.MachineInterface;
import com.broadcom.springconsulting.integrationdemo.movit.application.domain.model.Server;
import com.broadcom.springconsulting.integrationdemo.movit.application.port.in.PrepareRequestDownloadUseCase;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
class PrepareRequestDownloadService implements PrepareRequestDownloadUseCase {

    private final GetAllInboundMachineInterfacesUseCase getAllInboundMachineInterfacesUseCase;
    private final GetAllOutboundMachineInterfacesUseCase getAllOutboundMachineInterfacesUseCase;

    PrepareRequestDownloadService(GetAllInboundMachineInterfacesUseCase getAllInboundMachineInterfacesUseCase, GetAllOutboundMachineInterfacesUseCase getAllOutboundMachineInterfacesUseCase) {

        this.getAllInboundMachineInterfacesUseCase = getAllInboundMachineInterfacesUseCase;
        this.getAllOutboundMachineInterfacesUseCase = getAllOutboundMachineInterfacesUseCase;

    }

    @Override
    public Map<String, Object> execute(PrepareRequestDownloadCommand command) {

        var servers = this.getAllInboundMachineInterfacesUseCase.execute( new GetAllInboundMachineInterfacesUseCase.GetAllInboundMachineInterfacesCommand() ).stream()
                .map( mi -> new Server( mi.name(), mi.port() ) );
        var gateways = this.getAllOutboundMachineInterfacesUseCase.execute( new GetAllOutboundMachineInterfacesUseCase.GetAllOutboundMachineInterfacesCommand() ).stream()
                .map( mi -> new MachineInterface( mi.name(), mi.port() ) );

        return Map.of( "servers", servers, "gateways", gateways );
    }

}
