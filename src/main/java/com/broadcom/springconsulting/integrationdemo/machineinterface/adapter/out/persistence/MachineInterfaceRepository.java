package com.broadcom.springconsulting.integrationdemo.machineinterface.adapter.out.persistence;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface MachineInterfaceRepository extends CrudRepository<MachineInterfaceEntity, Long> {

    @Query( "select * from machine_interface where connection_direction = :connectionDirection" )
    Iterable<MachineInterfaceEntity> findAllByConnectionDirection( ConnectionDirection connectionDirection );

    @Query( "select * from machine_interface where server_port = :port" )
    Optional<MachineInterfaceEntity> findByPort( Integer port );

}
