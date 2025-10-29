package com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.in;

import com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.model.MachineInterface;

import java.util.List;

public interface GetAllInboundMachineInterfacesUseCase {

    List<MachineInterface> execute(GetAllInboundMachineInterfacesCommand command );

    record GetAllInboundMachineInterfacesCommand() {}

}
