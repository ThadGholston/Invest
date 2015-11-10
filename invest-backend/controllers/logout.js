var mysql 			= require('mysql');
var config 			= require('../config');
var authController 	= require('./auth');

exports.getLogout = function (req, res) {
	var user = req.user
	var connection = mysql.createConnection(config.db);
	connection.connect()
	connection.query("UPDATE user SET secret='' where id=" + user, function (err, result) {
		if (err) {
			console.log(err);
			connection.rollback(function (argument) {});
			return res.status(500).send({'errors':[{'message': 'Internal Server Error', 'code': 500}]}); 
		} else {
			return res.status(200);
		}
	});
	connection.end()
}