package com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.out;

import com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.model.MachineInterface;

import java.util.List;

public interface FindAllOutboundMachineInterfacesPort {

    List<MachineInterface> findAllOutbound();

}
