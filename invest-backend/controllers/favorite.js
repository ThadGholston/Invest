var mysql = require('mysql');
var config = require('../config');

var connection = mysql.createConnection(config.db);

connection.connect();

exports.postFavorite = function (req, res) {
	res.json({});
};

exports.deleteFavorite = function (req, res) {
	res.json({});
};

exports.getFavorite = function (req, res) {
	res.json({msg: 'copasetic'});
};