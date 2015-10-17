var mysql = require('mysql');
var config = require('../config');

var connection = mysql.createConnection(config.db);

connection.connect();

exports.updateAccount = function updateAccount () {
	// body...
}

exports.getAccount = function getAccount () {
	// body...
}

exports.deleteAccount = function deleteAccount () {
	// body...
}