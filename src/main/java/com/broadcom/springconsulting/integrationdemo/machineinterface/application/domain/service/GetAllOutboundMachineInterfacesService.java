package com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.service;

import com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.model.MachineInterface;
import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.in.GetAllOutboundMachineInterfacesUseCase;
import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.out.FindAllOutboundMachineInterfacesPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class GetAllOutboundMachineInterfacesService implements GetAllOutboundMachineInterfacesUseCase {

    private final FindAllOutboundMachineInterfacesPort port;

    GetAllOutboundMachineInterfacesService( FindAllOutboundMachineInterfacesPort port ) {

        this.port = port;

    }

    @Override
    public List<MachineInterface> execute( GetAllOutboundMachineInterfacesCommand command ) {

        return this.port.findAllOutbound();
    }

}
