package com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.out;

import com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.model.MachineInterface;

import java.util.Optional;

public interface FindByPortNumberMachineInterfacePort {

    Optional<MachineInterface> findByPort( Integer port );

}
