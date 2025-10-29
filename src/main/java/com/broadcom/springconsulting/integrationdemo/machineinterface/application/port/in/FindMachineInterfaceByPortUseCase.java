package com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.in;

import com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.model.MachineInterface;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

import static com.broadcom.springconsulting.integrationdemo.common.validation.Validation.validate;

public interface FindMachineInterfaceByPortUseCase {

    Optional<MachineInterface> execute( FindByPortCommand command );

    record FindByPortCommand( @NotNull Integer port ) {

        public FindByPortCommand( Integer port ) {
            this.port = port;

            validate( this );

        }

    }

}
