var authController 	= require('./auth');
var config      	= require('../config');
var bcrypt			= require('bcrypt');
var mysql 			= require('mysql');
var hat 			= require('hat');
var rack 			= hat.rack();

exports.postReauth = function (req, res) {
	var password = req.body.password;
	var username = req.body.username;
	var salt = bcrypt.genSaltSync(10);
	var secret = rack();
	var connection = mysql.createConnection(config.db);
	connection.connect()
	connection.query("SELECT id, password_hash FROM user WHERE username='" + username + "';", function (err, result) {
		if (err) {
			console.log(err)
			return res.status(500).send({'errors':[{'message': 'Invalid password', 'code': 500}]});  
		} else {
			var password_hash = result[0].password_hash
			var user = result[0].id
			if (bcrypt.compareSync(password, password_hash)){
				connection.query("UPDATE user SET secret='" + secret + "' where username='" + username + "';", function (err, result) {
					if (err) {
						console.log(err);
						connection.rollback(function (argument) {});
						return res.status(500).send({'errors':[{'message': 'Internal Server Error', 'code': 500}]}); 
					} else {
						console.log(result);
						var numberChanged = result.changedRows
						if (numberChanged === 1){
							var token = authController.createToken({id: user, secret: secret});
							return res.json({token: token});
						} else {
							return res.status(500).send({'errors':[{'message': 'Internal Server Error', 'code': 500}]}); 
						}
					}
					connection.end();
				});
			} else {
				console.log("Mismatch");
				connection.end();
				return res.json({'INVALID':'INVALID'});  
			}
		}
	});
}