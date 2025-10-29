package com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.service;

import com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.model.MachineInterface;
import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.in.GetAllInboundMachineInterfacesUseCase;
import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.out.FindAllInboundMachineInterfacesPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class GetAllInboundMachineInterfacesService implements GetAllInboundMachineInterfacesUseCase {

    private final FindAllInboundMachineInterfacesPort findAllInboundMachineInterfacesPort;

    GetAllInboundMachineInterfacesService(FindAllInboundMachineInterfacesPort findAllInboundMachineInterfacesPort) {

        this.findAllInboundMachineInterfacesPort = findAllInboundMachineInterfacesPort;

    }

    @Override
    public List<MachineInterface> execute(GetAllInboundMachineInterfacesCommand command ) {

        return this.findAllInboundMachineInterfacesPort.findAllInbound();
    }

}
