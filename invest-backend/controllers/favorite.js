var mysql = require('mysql');
var config = require('../config');
var util = require('util')

exports.postFavorite = function (req, res) {
	var connection = mysql.createConnection(config.db);
	connection.connect();
	var symbol = req.body.symbol
	var insertTable = "user_" + req.body.type
	var userID = req.user
	var type = req.body.type
	var symbolID = type + "_id"
	if (type === 'index'){
		type = '`index`'
	}
	var fromTable = type
	var stmt = util.format("INSERT INTO %s(user_id, %s) VALUES (%d, (SELECT id from %s where symbol = '%s' limit 1))", insertTable, symbolID, userID, fromTable, symbol)
	console.log(stmt)
	connection.query( stmt, function (err, result) {
		if (err) {
			connection.rollback(function (argument) {});
			console.log(err);
			if (err.message.indexOf('Duplicate entry') > -1){
				return res.status(300).send({'errors':[{'message': 'Duplicate', 'code': 300}]}); 
			} else {
				return res.status(500).send({'errors':[{'message': 'Internal Server Error', 'code': 500}]}); 
			}
		} else {
			return res.json({msg: 'copasetic'});
		}
	});
	connection.end();
};


exports.deleteFavorite = function (req, res) {
	var connection = mysql.createConnection(config.db);
	connection.connect();
	var symbol = req.body.symbol
	var insertTable = 'user_' + req.body.type
	var insertTableTypeID = req.body.type + '_id'
	var fromTable = req.body.type
	if (fromTable === 'index'){
		fromTable = '`index`'
	}
	console.log(req)
	var user = req.user
	var stmt = util.format("DELETE FROM %s WHERE user_id=%d AND %s=(SELECT id from %s where symbol = '%s');", insertTable, user, insertTableTypeID, fromTable, symbol);
	connection.query(stmt, function (err, result) {
  		if (err) {
  			console.log(err);
			connection.rollback(function (argument) {});
			return res.status(500).send({'errors':[{'message': 'Internal Server Error', 'code': 500}]}); 
		} else {
			return res.json({msg: 'copasetic'});
		}
	});
	connection.end();
};

exports.getFavorite = function (req, res) {
	var connection = mysql.createConnection(config.db);
	connection.connect();
	var user = req.user
	var stmt = "(SELECT name, 'stock' as type, symbol FROM stock join user_stock on stock.id = user_stock.stock_id where user_id = " + user + ") UNION (SELECT name, 'index' as type, symbol FROM `index` join user_index on `index`.id = user_index.index_id where user_id = " + user + ") UNION (SELECT name, 'mutual_fund' as insertTableTypeID, symbol FROM mutual_fund join user_mutual_fund on mutual_fund.id = user_mutual_fund.mutual_fund_id where user_id = " + user + ");"
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
