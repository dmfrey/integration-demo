package com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.in;

import com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.model.MachineInterface;

import java.util.Optional;

public interface UpdateMachineInterfaceUseCase {

    Optional<MachineInterface> execute( UpdateMachineInterfaceCommand command );

    record UpdateMachineInterfaceCommand(
            Long id,
            String name,
            String connectionDirection,
            String connectionType,
            String hostname,
            Integer port,
            String username,
            String password,
            String remoteDirectory
    ) {

        public MachineInterface renderGateway() {

            return new MachineInterface(
                    id,
                    name,
                    connectionDirection,
                    connectionType,
                    hostname,
                    port,
                    username,
                    password,
                    remoteDirectory
            );
        }

    }
}
