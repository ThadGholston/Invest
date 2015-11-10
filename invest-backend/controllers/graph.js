var mysql 	= require('mysql');
var config 	= require('../config');
var request = require('request');
var fs 		= require('fs')

exports.getGraph = function (req, res) {
	var symbol = req.params.symbol;
	var url = "http://chart.finance.yahoo.com/z?s=" + symbol;
	request(url).pipe(res);
};

