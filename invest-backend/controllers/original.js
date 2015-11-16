var config 			= require('../config');
var mysql 			= require('mysql');

exports.postOriginal = function (req, res) {
	var username = req.body.username;
	var connection = mysql.createConnection(config.db);
	connection.connect();
	connection.query("SELECT username FROM user WHERE username='" + username + "';", function (err, result) {
		console.log(result);
		if (result.length === 1){
			res.json({valid: false})	
		} else {
			res.json({valid: true})
		}
		
	});
	connection.end();
}