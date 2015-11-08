var mysql 	= require('mysql');
var config 	= require('../config');

var connection = mysql.createConnection(config.db);

connection.connect();
exports.getStock = function (req, res) {
	var connection = mysql.createConnection(config.db);
	connection.connect();
	res.render('../stock', {data: []});
};

