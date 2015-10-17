var authController 	= require('./auth');
var mysql 			= require('mysql');
var bcrypt			= require('bcrypt');
var hat 			= require('hat');
var rack 			= hat.rack();
var config 			= require('../config');
var salt 			= config.salt;
var connection = mysql.createConnection(config.db);

// Examples of how to use mysql at https://github.com/felixge/node-mysql
connection.connect();

// Create endpoint /api/login for POST
exports.postLogin = function (req, res) {
	var username = req.body.username;
	var password = req.body.password;
	var secret = rack();

	// Examples of how to use bcrypt at https://github.com/ncb000gt/node.bcrypt.js/
	var salt = bcrypt.genSaltSync(10);
	var password_hash = bcrypt.hashSync(password, salt);
	
	connection.query('INSERT INTO users SET ?', {username: username, password_hash: password_hash, secret: secret}, function (err, result) {
		if (err) {
			connection.rollback(function (argument) {});
			return res.status(500).send({'errors':[{'message': 'Internal Server Error', 'code': 500}]}); 
		} else {
			var id = result.insertId;
			var token = authController.createToken({id: id, secret: secret});
			return res.json({token : token});
		}
	});
}