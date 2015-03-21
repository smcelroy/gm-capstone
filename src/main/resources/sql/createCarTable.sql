USE testGM;

DROP TABLE IF EXISTS filterquerymakes;
DROP TABLE IF EXISTS filterquerymodels;
DROP TABLE IF EXISTS filterqueryalternates;

CREATE TABLE filterquerymakes(
	filterid INT NOT NULL,
	name varchar(128),
    models varchar(128),
	PRIMARY KEY(filterid)
);

CREATE TABLE filterquerymodels(
	make varchar(128) NOT NULL,
	models varchar(2048),
	PRIMARY KEY(make)
);

CREATE TABLE filterqueryalternates(
	make varchar(128) NOT NULL,
	alternates varchar(2048),
	PRIMARY KEY(make)	
);

insert into filterquerymakes values(0, 'generalmotors,chevrolet,gmc,buick,cadillac');

insert into filterquerymodels values('chevrolet', 'cruze,sonic,spark,impala,malibu,camaro,corvette,ss,captiva,equinox,trax,traverse,tahoe,suburban,express,savana,avalanche,silverado,colorado');
insert into filterquerymodels values('buick', 'verano,lacrosse,regal,encore,enclave');
insert into filterquerymodels values('cadillac', 'cts,xts,ats,cts-v,srx,escalade');
insert into filterquerymodels values('gmc', 'terrain,acadia,yukon,sierra');

insert into filterqueryalternates values ('chevrolet', 'vette,chevy');
