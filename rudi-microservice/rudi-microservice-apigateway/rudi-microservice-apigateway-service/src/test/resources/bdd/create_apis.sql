INSERT INTO apigateway_data.throttling
	(uuid, code, label, opening_date, order_, burst_capacity, rate)
VALUES 
    ('a2bd7012-d27e-4dfb-bff2-03f5bda61494', 'BRONZE', 'Bronze', to_date('01/01/2024','DD/MM/YYYY'), 5, 5000, 1000),
	('88e2439a-9bfc-4802-aab0-6c7b88fde6cc', 'SILVER', 'Silver', to_date('01/01/2024','DD/MM/YYYY'), 10, 10000, 5000),
	('6ff84e13-ec09-45c4-8155-441094de1aac', 'GOLD', 'Gold', to_date('01/01/2024','DD/MM/YYYY'), 15, 20000, 10000),
	('fd5ea082-1747-44e8-86eb-196b011d811f', 'UNLIMITED', 'Unlimited', to_date('01/01/2024','DD/MM/YYYY'), 20, -1, -1);
	
INSERT INTO apigateway_data.api
	(
	uuid, contract, 
		global_id, media_id, 
		node_provider_id, producer_id, provider_id, 
		url
	)
VALUES 
	(
	'8f1fe6ae-fb98-4f39-a21d-cbc93d084fa8', 'dwnl', 
		'ece3d9d3-cbc6-4c74-8614-1d2c8ba82998', 'e64c749d-7298-4dfd-abf1-7c84dc885695', 
		'67264815-4d63-4970-9b59-c5544373b0ee', '847b1c29-df67-440b-967a-69716d85a4a9', 'a9664f5a-ddd4-496e-8878-b1acf3ef787f',
		'https://www.google.fr');
		
INSERT INTO apigateway_data.api_method
	(api_fk, methods)
VALUES
	((select id from apigateway_data.api where uuid = '8f1fe6ae-fb98-4f39-a21d-cbc93d084fa8'), 'GET');
	
INSERT INTO apigateway_data.api_throttling
	(api_fk, throttling_fk)
VALUES
	((select id from apigateway_data.api where uuid = '8f1fe6ae-fb98-4f39-a21d-cbc93d084fa8'),(select id from apigateway_data.throttling where code = 'BRONZE'))	;
	
INSERT INTO apigateway_data.apiparameter_entity
	(uuid, name, value_, api_fk)
VALUES ('558215f1-89d4-4073-9d97-e3f48090bdf0', 'crypted', 'true',(select id from apigateway_data.api where uuid = '8f1fe6ae-fb98-4f39-a21d-cbc93d084fa8') );
	