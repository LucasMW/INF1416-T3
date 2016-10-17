-- create a new database
--begin transation;

-------------------------------------------------------------------------------
create table users (
	id            integer primary key autoincrement,
	login         char(20)        not null,
	name          char(50)        not null,
	description   char(50)        not null,

	cert          char(1024),

	password      char(127)       not null,
	tanList       char(127)       not null,

	isAdmin       boolean         not null,
	totalAccesses integer,
	blockedUntil  char(32) -- problems with java select, store as string
);

-- md5(BACADA000000000) = a3024da4c60f087128a545957cb40c5d
insert into users values (
	0,
	"admin",
	"joe, the admin",
	"default admin account",

	'
Certificate:
    Data:
        Version: 3 (0x2)
        Serial Number: 0 (0x0)
    Signature Algorithm: sha1WithRSAEncryption
        Issuer: C=BR, ST=RJ, L=Rio, O=PUC, OU=DI, CN=INF1416 AC/emailAddress=ca@grad.inf.puc-rio.br
        Validity
            Not Before: Sep 14 20:54:18 2015 GMT
            Not After : Sep 13 20:54:18 2016 GMT
        Subject: C=BR, ST=RJ, O=PUC, OU=DI, CN=Anderson Oliveira da Silva/emailAddress=oliveira@grad.inf.puc-rio.br
        Subject Public Key Info:
            Public Key Algorithm: rsaEncryption
                Public-Key: (1024 bit)
                Modulus:
                    00:c1:30:df:c0:2b:8a:82:8b:52:62:40:81:9e:88:
                    6c:da:02:29:b8:25:23:56:81:e4:4e:9f:13:33:e3:
                    3c:e7:76:5b:6f:36:ab:17:bb:65:a9:1e:64:a7:d1:
                    bf:1b:8f:92:22:ef:fd:c9:ba:c4:54:37:60:53:d7:
                    ce:43:f0:e8:b8:ba:43:23:ba:fc:3f:0b:34:58:2b:
                    a8:77:05:b0:da:31:00:07:04:7d:85:4d:58:82:9f:
                    23:7d:d6:7e:9c:3d:d1:46:bc:dc:49:0d:f9:f7:ce:
                    27:25:4e:ef:f6:11:39:6a:e0:30:58:e8:c2:68:ef:
                    b5:73:9c:de:90:9d:6b:fd:cd
                Exponent: 65537 (0x10001)
        X509v3 extensions:
            X509v3 Basic Constraints: 
                CA:FALSE
            Netscape Comment: 
                OpenSSL Generated Certificate
            X509v3 Subject Key Identifier: 
                22:6F:9C:E6:65:8F:4E:CA:E8:18:DF:B6:C7:EC:F7:B9:3A:9D:79:52
            X509v3 Authority Key Identifier: 
                keyid:40:B4:AB:6C:E0:95:72:0A:0B:1D:5F:BA:FE:FC:A6:0B:0C:29:8E:FB

    Signature Algorithm: sha1WithRSAEncryption
         25:35:70:37:3f:f5:04:8a:19:c8:43:ef:a9:96:c0:c6:8e:08:
         ec:f5:84:2a:17:32:e3:44:b7:c0:26:7f:68:28:5d:77:d1:63:
         c7:4b:da:60:29:50:18:15:0c:aa:ff:33:0a:76:51:8d:08:6e:
         e5:53:00:6f:66:3d:1b:b5:99:af:c3:83:0e:77:d3:94:94:85:
         29:e9:2f:b6:87:27:29:d6:cf:3d:88:0c:c7:ec:a1:d3:f8:2c:
         3f:fa:da:47:2b:4c:8c:e0:63:4a:c3:40:aa:f9:68:31:64:24:
         dd:26:d1:c9:99:39:99:9f:59:a3:6d:91:bf:1b:53:fe:f0:00:
         8b:03
-----BEGIN CERTIFICATE-----
MIIC+DCCAmGgAwIBAgIBADANBgkqhkiG9w0BAQUFADB/MQswCQYDVQQGEwJCUjEL
MAkGA1UECAwCUkoxDDAKBgNVBAcMA1JpbzEMMAoGA1UECgwDUFVDMQswCQYDVQQL
DAJESTETMBEGA1UEAwwKSU5GMTQxNiBBQzElMCMGCSqGSIb3DQEJARYWY2FAZ3Jh
ZC5pbmYucHVjLXJpby5icjAeFw0xNTA5MTQyMDU0MThaFw0xNjA5MTMyMDU0MTha
MIGHMQswCQYDVQQGEwJCUjELMAkGA1UECAwCUkoxDDAKBgNVBAoMA1BVQzELMAkG
A1UECwwCREkxIzAhBgNVBAMMGkFuZGVyc29uIE9saXZlaXJhIGRhIFNpbHZhMSsw
KQYJKoZIhvcNAQkBFhxvbGl2ZWlyYUBncmFkLmluZi5wdWMtcmlvLmJyMIGfMA0G
CSqGSIb3DQEBAQUAA4GNADCBiQKBgQDBMN/AK4qCi1JiQIGeiGzaAim4JSNWgeRO
nxMz4zzndltvNqsXu2WpHmSn0b8bj5Ii7/3JusRUN2BT185D8Oi4ukMjuvw/CzRY
K6h3BbDaMQAHBH2FTViCnyN91n6cPdFGvNxJDfn3ziclTu/2ETlq4DBY6MJo77Vz
nN6QnWv9zQIDAQABo3sweTAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVu
U1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQUIm+c5mWPTsroGN+2
x+z3uTqdeVIwHwYDVR0jBBgwFoAUQLSrbOCVcgoLHV+6/vymCwwpjvswDQYJKoZI
hvcNAQEFBQADgYEAJTVwNz/1BIoZyEPvqZbAxo4I7PWEKhcy40S3wCZ/aChdd9Fj
x0vaYClQGBUMqv8zCnZRjQhu5VMAb2Y9G7WZr8ODDnfTlJSFKekvtocnKdbPPYgM
x+yh0/gsP/raRytMjOBjSsNAqvloMWQk3SbRyZk5mZ9Zo22RvxtT/vAAiwM=
-----END CERTIFICATE-----
	',
	--"./data/Keys/userpriv-pkcs8-pem-des.key",

	"a3024da4c60f087128a545957cb40c5d|000000000",
	"I2RR|AMSG|WTPU|0BHQ|TUY4|LI28|OCLE|UI16|F3PG|00MF|#8|6|0|3|9|4|5|1|2|7|",

	1,
	0,
	null
);

-------------------------------------------------------------------------------
create table groups (
	id          integer primary key autoincrement,
	name        char(50)        not null,
	description char(50)        not null
);

insert into groups values (
	0,
	"admin",
	"Access to basic systems + ability to create new users"
);

insert into groups values (
	1,
	"user",
	"Access to basic systems"
);

-------------------------------------------------------------------------------
create table ingroup (
	id          integer primary key autoincrement,
	user_id     integer,
	group_id    integer,

	foreign key(user_id)  references users (id)
	foreign key(group_id) references groups(id)
);

insert into ingroup values (
	0, 0, 0 -- user admin is admin
);

insert into ingroup values (
	1, 0, 1 -- user admin is user
);

-------------------------------------------------------------------------------
create table messages (
	id          integer primary key autoincrement,
	message     char(50)        not null
);

insert into messages values (1001, "Sistema iniciado.");
insert into messages values (1002, "Sistema encerrado.");
insert into messages values (2001, "Autenticação etapa 1 iniciada.");
insert into messages values (2002, "Autenticação etapa 1 encerrada.");
insert into messages values (2003, "Log in name %s identificado com acesso liberado."); -- login_name
insert into messages values (2004, "Login name %s identificado com acesso bloqueado."); -- login_name
insert into messages values (2005, "Login name %s não identificado."); -- login_name
insert into messages values (3001, "Autenticação etapa 2 iniciada para %s."); -- login_name
insert into messages values (3002, "Autenticação etapa 2 encerr ada para %s."); -- login_name
insert into messages values (3003, "Senha pessoal verificada positivamente para %s."); -- login_name
insert into messages values (3004, "Senha pessoal verificada negativamente para %s."); -- login_name
insert into messages values (3005, "Primeiro erro da senha pessoal contabilizado para %s."); -- login_name
insert into messages values (3006, "Segundo erro da senha pessoal contabilizado para %s."); -- login_name
insert into messages values (3007, "Terceiro erro da senha pessoal contabilizado para %s."); -- login_name
insert into messages values (3008, "Acesso do usuario %s bloqueado pela autenticação etapa 2."); -- login_name
insert into messages values (4001, "Autenticação etapa 3 iniciada para %s."); -- login_name
insert into messages values (4002, "Autenticação etap a 3 encerrada para %s."); -- login_name
insert into messages values (4003, "Senha de única vez verificada positivamente para %s."); -- login_name
insert into messages values (4004, "Primeiro erro da senha de única vez contabilizado para %s."); -- login_name
insert into messages values (4005, "Segundo erro da senha de única vez contabilizado para %s."); -- login_name
insert into messages values (4006, "Terceiro erro da senha de única vez contabilizado para %s."); -- login_name
insert into messages values (4009, "Acesso do usuario %s bloqueado pela autenticação etapa 3."); -- login_name
insert into messages values (5001, "Tela principal apresentada para %s."); -- login_name
insert into messages values (5002, "Opção 1 do menu principal selecionada por %s."); -- login_name
insert into messages values (5003, "Opção 2 do menu principal selecionada por %s."); -- login_name
insert into messages values (5004, "Opção 3 do menu principal selecionada por %s."); -- login_name
insert into messages values (5005, "Opção 4 do menu principal selecionada por %s."); -- login_name
insert into messages values (6001, "Tela de cadastro apresentada para %s."); -- login_name
insert into messages values (6002, "Botão ca dastrar pressionado por %s."); -- login_name
insert into messages values (6003, "Caminho do certificado digital inválido fornecido por %s."); -- login_name
insert into messages values (6004, "Confirmação de dados aceita por %s."); -- login_name
insert into messages values (6005, "Confirmação de dados rejeitada por %s."); -- login_name
insert into messages values (6006, "Botão voltar de cadastro para o menu principal pressionado por %s."); -- login_name
insert into messages values (7001, "Tela de carregamento da chave privada apresentada para %s."); -- login_name
insert into messages values (7002, "Caminho da chave privada inválido fornecido por %s."); -- login_name
insert into messages values (7003, "Frase secreta inválida fornecida por %s."); -- login_name
insert into messages values (7004, "Erro de validação da chave privada com o certificado digital de %s."); -- login_name
insert into messages values (7005, "Chave privada validada com sucesso para %s."); -- login_name
insert into messages values (7006, "Botão voltar de carregamento para o menu principal pressionado por %s."); -- login_name
insert into messages values (8001, "Tela de consulta de arquivos secretos apresentada para %s."); -- login_name
insert into messages values (8002, "Botão voltar de consulta para o menu principal pressionado por %s."); -- login_name
insert into messages values (8003, "Botão Listar de consulta pressionado por %s."); -- login_name
insert into messages values (8006, "Caminho de pasta inválido fornecido por %s."); -- login_name
insert into messages values (8007, "Lista de arquivos apresentada para %s."); -- login_name
insert into messages values (8008, "Arquivo <arq_name> selecionado por %s para decriptação."); -- login_name
insert into messages values (8009, "Arquivo <arq_na me> decriptado com sucesso para %s."); -- login_name
insert into messages values (8010, "Arquivo <arq_name> verificado (integridade e autenticidade) com sucesso para %s."); -- login_name
insert into messages values (8011, "Falha na decriptação do arquivo %s para %s."); -- arq_name, login_name
insert into messages values (8012, "Falha na verificação do arquivo %s para %s."); -- arq_name, login_name
insert into messages values (9001, "Tela de saída apresentada para %s."); -- login_name
insert into messages values (9002, "Botão sair pressionado por %s."); -- login_name
insert into messages values (9003, "Botão voltar de sair para o menu principal pressionado por %s."); -- login_name

-------------------------------------------------------------------------------
create table registers (
	id         integer primary key autoincrement,
	user_id    integer,
	msg_id     integer              not null,
	time       date,
	file_name  char(255),

	foreign key(user_id) references users   (id)
	foreign key(msg_id)  references messages(id)
);

--commit;
