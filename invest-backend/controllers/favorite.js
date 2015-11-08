var mysql = require('mysql');
var config = require('../config');

exports.postFavorite = function (req, res) {
	var connection = mysql.createConnection(config.db);
	connection.connect();
	var symbol = req.body.symbol
	var table = "user_" + req.body.type
	var user = req.user
	var stmt = "INSERT INTO " + table + " SET ?"
	var tableId = req.body.type + "_id"
	connection.query( stmt, {user_id : user, tableId : id}, function (err, result) {
		if (err) {
			connection.rollback(function (argument) {});
			return res.status(500).send({'errors':[{'message': 'Internal Server Error', 'code': 500}]}); 
		} else {
			return res.json({msg: 'copasetic'});
		}
	});
	connection.end();
};

exports.deleteFavorite = function (req, res) {
	var connection = mysql.createConnection(config.db);
	connection.connect();
	var id = req.body.id
	var type = req.body.type
	var user = req.user
	var stmt = "DELETE FROM user_" + type + " WHERE user_id=" + user + " AND " + type + "_id=" + id + ";";
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
	var stmt = "(SELECT 'stock' as fin_type, symbol FROM stock join user_stock on stock.id = user_stock.stock_id where user_id = " + user + ") UNION (SELECT 'index' as fin_type, symbol FROM `index` join user_index on `index`.id = user_index.index_id where user_id = " + user + ") UNION (SELECT 'mutual_fund' as fin_type, name as symbol FROM mutual_fund join user_mutual_fund on mutual_fund.id = user_mutual_fund.mutual_fund_id where user_id = " + user + ");"
	connection.query(stmt, function(err, rows, fields){
		if (err) {
			console.log(err);
			return res.status(500).send({'errors':[{'message': 'Internal Server Error', 'code': 500}]}); 
		} else {
			console.log(rows);
			return res.json({favorites: rows});
		}
	});
	connection.end();
};
