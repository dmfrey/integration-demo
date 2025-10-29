package com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.service;

import com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.model.MachineInterface;
import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.in.FindMachineInterfaceByPortUseCase;
import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.out.FindByPortNumberMachineInterfacePort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class FindByPortService implements FindMachineInterfaceByPortUseCase {

    private final FindByPortNumberMachineInterfacePort port;

    FindByPortService(FindByPortNumberMachineInterfacePort port ) {

        this.port = port;

    }

    @Override
    public Optional<MachineInterface> execute( FindByPortCommand query ) {

        return this.port.findByPort( query.port() );
    }

}
