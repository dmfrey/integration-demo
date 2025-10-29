package com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.out;

import com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.model.MachineInterface;

import java.util.Optional;

public interface UpdateMachineInterfacePort {

    Optional<MachineInterface> update( MachineInterface machineInterface );

}
