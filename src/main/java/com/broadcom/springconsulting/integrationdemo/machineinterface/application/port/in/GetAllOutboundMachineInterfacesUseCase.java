package com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.in;

import com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.model.MachineInterface;

import java.util.List;

public interface GetAllOutboundMachineInterfacesUseCase {

    List<MachineInterface> execute( GetAllOutboundMachineInterfacesCommand command );

    record GetAllOutboundMachineInterfacesCommand() {}

}
