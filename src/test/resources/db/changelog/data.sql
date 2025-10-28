insert into gateway (name, connection_type, server_hostname, server_port, server_username, server_password)
    values
        ('Port62281', 'SFTP', 'localhost', 62281, 'admin', 'admin'),
        ('Port62282', 'SFTP', 'localhost', 62282, 'admin', 'admin'),
        ('Port62283', 'SFTP', 'localhost', 62283, 'admin', 'admin'),
        ('Port62284', 'SFTP', 'localhost', 62284, 'admin', 'admin'),
        ('Port62285', 'SFTP', 'localhost', 62285, 'admin', 'admin'),
        ('Port62286', 'SFTP', 'localhost', 62286, 'admin', 'admin'),
        ('Port1234', 'TCP', 'localhost', 1234, '', '');

insert into gateway (name, connection_type, server_hostname, server_port, server_username, server_password, remote_directory)
values
    ('Port59342', 'SMB', 'localhost', 59342, 'foo', 'bar', '/');
