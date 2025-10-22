package com.broadcom.springconsulting.integrationdemo.machine.adapter.out.persistence;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface GatewayRepository extends CrudRepository<GatewayEntity, Long> {

    @Query( "select * from gateway" )
    List<GatewayEntity> findAllServers();

    @Query( "select * from gateway where server_port = :port" )
    Optional<GatewayEntity> findByPort( Integer port );

}
