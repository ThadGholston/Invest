var passport        = require('passport');
var TokenStrategy   = require('passport-accesstoken').Strategy;
var mysql 			= require('mysql');
var bcrypt 			= require('bcrypt');
var hat 			= require('hat');
var CryptoJS 		= require("crypto-js");
var config 			= require('../config');

var connection = mysql.createConnection(config.db);
var secret = config.secret;

connection.connect();

var strategyOptions = {
	tokenHeader: 'x-auth-token',
	tokenField: 'nothingnothingnothingyoudidntseethissaynothingtonooneiwasneverhurtheworldismyplayground'
}

passport.use(new TokenStrategy(strategyOptions, 
	function (token, done) {
		if (token === "undefined") return done('Token undefined');
		if (token === undefined) return done('Token undefined');
		if (token === null) return done('Token null');
		decryptToken(token, function (err, userid) {
			if (err){
				return done(err);
			}
			return done(null, userid);
		});
	}
));

function decryptToken (token, cb) {
	var str = new Buffer(token, 'base64').toString('ascii');
	var pieces = str.split('.');
	var userid = pieces[0];
	var encrypted = pieces[1];
	var bytes = CryptoJS.AES.decrypt(encrypted.toString(), secret);
	var decryptedData = JSON.parse(bytes.toString(CryptoJS.enc.Utf8));
	console.log(decryptedData);
	if (userid == decryptedData.id){
		connection.query('SELECT secret, id FROM users WHERE id=' + userid, function (err, rows, fields) {
			if (err) throw err;
			console.log(decryptedData.secret);
			console.log(rows[0].secret);
			if (decryptedData.secret === rows[0].secret){
				console.log("TEST");
				cb(null, rows[0].id);
			} else return cb(false);
		});
	} else return cb(false);
};

exports.decryptToken = decryptToken;

exports.createToken = function createToken (info) {
	var data = {
		id : info.id,
		secret : info.secret
	};

	var encrypted = CryptoJS.AES.encrypt(JSON.stringify(data), secret);
	var token = new Buffer(info.id + '.' + encrypted).toString('base64');
	return token;
}

exports.isAuthenticated = passport.authenticate('token', { session : false });






