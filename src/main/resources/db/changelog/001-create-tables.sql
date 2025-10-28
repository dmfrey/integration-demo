create table gateway (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(200) not null,
    connection_direction VARCHAR(10) not null,
    connection_type VARCHAR(10) not null,
    server_hostname VARCHAR(200) not null,
    server_port INT not null,
    server_username VARCHAR(100),
    server_password VARCHAR(100),
    remote_directory VARCHAR(100) not null default '/'
);