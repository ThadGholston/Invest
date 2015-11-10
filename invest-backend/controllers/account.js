var authController 	= require('./auth');
var mysql 			= require('mysql');
var config 			= require('../config');
var mysql 			= require('mysql');
var bcrypt			= require('bcrypt');
var config 			= require('../config');
var hat 			= require('hat');
var rack 			= hat.rack();


exports.updateAccount  = function (req, res) {
	var user = req.user;
	var old_password = req.body.old_password;
	var new_password = req.body.new_password;
	var secret = rack();
	var connection = mysql.createConnection(config.db);
	connection.connect()
	connection.query("SELECT password_hash FROM user WHERE id=" + user, function (err, result) {
		var password_hash = result[0].password_hash
		if (bcrypt.compareSync(old_password, password_hash)){
			var new_password_hash = bcrypt.hashSync(new_password, 10);
			connection.query("UPDATE user SET secret='" + secret + "', password_hash='" + new_password_hash +"' where id=" + user + ";", function (err, result) {
				if (err) {
					console.log(err);
					connection.rollback(function (argument) {});
					return res.status(500).send({'errors':[{'message': 'Internal Server Error', 'code': 500}]}); 
				} else {
					var numberChanged = result.changedRows
					if (numberChanged === 1){
						var token = authController.createToken({id: user, secret: secret});
						return res.json({token: token});
					} else {
						return res.status(500).send({'errors':[{'message': 'Internal Server Error', 'code': 500}]}); 
					}
				}
				connection.end()
			});
		} else {
			return res.status(500).send({'errors':[{'message': 'Invalid password', 'code': 500}]});  
		}
	});
	
}

exports.getAccount  = function (req, res) {
	var user = req.user;
	var connection = mysql.createConnection(config.db);
	connection.connect()
	connection.query('SELECT username, first_name, last_name FROM user WHERE id=' + user, function (err, result) {
		if (err) {
			console.log(err);
			connection.rollback(function (argument) {});
			return res.status(500).send({'errors':[{'message': 'Internal Server Error', 'code': 500}]}); 
		} else {
			console.log(result);
			res.json(result[0]);
		}
	});
	connection.end()
}
