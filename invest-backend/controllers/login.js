var authController 	= require('./auth');
var mysql 			= require('mysql');
var bcrypt			= require('bcrypt');
var hat 			= require('hat');
var rack 			= hat.rack();
var config 			= require('../config');
var salt 			= config.salt;

// Create endpoint /api/login for POST
exports.postLogin = function (req, res) {
	var username = req.body.username;
	var password = req.body.password;
	var first_name = req.body.first_name;
	var last_name = req.body.last_name;
	var secret = rack();

	// Examples of how to use bcrypt at https://github.com/ncb000gt/node.bcrypt.js/
	var password_hash = bcrypt.hashSync(password, 10);
	var connection = mysql.createConnection(config.db);
	connection.connect();
	console.log(username);
	console.log(first_name);
	console.log(last_name);
	console.log(secret);
	connection.query('INSERT INTO user SET ?', {username: username, password_hash: password_hash, secret: secret, first_name: first_name, last_name: last_name}, function (err, result) {
		if (err) {
			console.log(err);
			connection.rollback(function (argument) {});
			return res.status(500).send({'errors':[{'message': 'Internal Server Error', 'code': 500}]}); 
		} else {
			var id = result.insertId;
			var token = authController.createToken({id: id, secret: secret});
			return res.json({token : token});
		}
	});
	connection.end()
}

