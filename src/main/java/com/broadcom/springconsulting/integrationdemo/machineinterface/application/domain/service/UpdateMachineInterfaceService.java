package com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.service;


import com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.model.MachineInterface;
import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.in.UpdateMachineInterfaceUseCase;
import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.out.UpdateMachineInterfacePort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class UpdateMachineInterfaceService implements UpdateMachineInterfaceUseCase {

    private final UpdateMachineInterfacePort port;

    UpdateMachineInterfaceService( UpdateMachineInterfacePort port ) {

        this.port = port;

    }

    @Override
    public Optional<MachineInterface> execute( UpdateMachineInterfaceCommand command ) {

        return this.port.update( command.renderGateway() );
    }

}
