package com.broadcom.springconsulting.integrationdemo.machine.application.port.in;

import com.broadcom.springconsulting.integrationdemo.machine.application.domain.model.Gateway;

import java.util.Optional;

public interface GetServerUseCase {

    Optional<Gateway> execute(GetServerQuery query );

    record GetServerQuery( Integer port ) { }

}
