var config 	= require('../config');
var request = require('request');

exports.getPrice = function (req, res) {
	var symbol = req.params.symbol;
	var url = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/jsonp?symbol=" + symbol;
	request(url, function (err, response, body) {
		var jsonString = body.replace("(function () { })(", "").replace(")", "");
		var json = JSON.parse(jsonString);
		var latestPrice = json.LastPrice;
		res.json({price: latestPrice});
		
	});
};

