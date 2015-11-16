var mysql = require('mysql');
var config = require('../config');
var util = require('util')

exports.getSearch = function (req, res) {
	var connection = mysql.createConnection(config.db);
	connection.connect();
	var user = req.user
	var query = decodeURIComponent(req.params.query).toUpperCase()
	var stmt = "SELECT * FROM ((SELECT 'stock' as type, symbol, name FROM stock where name like '" + query + "%') UNION (SELECT 'index' as type, symbol, name FROM `index` where name like '" + query + "%') UNION (SELECT 'mutual_fund' as type, symbol, name FROM mutual_fund where name like '" + query + "%')) as tmp ORDER BY symbol ASC LIMIT 25;"
	console.log(stmt)
	connection.query(stmt, function(err, rows, fields){
		if (err) {
			console.log(err);
			return res.status(500).send({'errors':[{'message': 'Internal Server Error', 'code': 500}]}); 
		} else {
			console.log(rows);
			return res.json(rows);
		}
	});
	connection.end();
};