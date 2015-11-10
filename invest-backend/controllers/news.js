var request 		= require("request");
var config 			= require('../config');
var parseString 	= require('xml2js').parseString;

var newsJSON 		= []

exports.getNews = function (req, res) {
	var urls = ["http://articlefeeds.nasdaq.com/nasdaq/categories?category=Stocks", 
					"http://articlefeeds.nasdaq.com/nasdaq/categories?category=Bonds", 
					"http://articlefeeds.nasdaq.com/nasdaq/categories?category=Commodities",
					"http://articlefeeds.nasdaq.com/nasdaq/categories?category=ETFs",
					"http://articlefeeds.nasdaq.com/nasdaq/categories?category=Forex+and+Currencies",
					"http://articlefeeds.nasdaq.com/nasdaq/categories?category=Futures",
					"http://articlefeeds.nasdaq.com/nasdaq/categories?category=International",
					"http://articlefeeds.nasdaq.com/nasdaq/categories?category=Investing+Ideas",
					"http://articlefeeds.nasdaq.com/nasdaq/categories?category=Mutual+Funds",
					"http://articlefeeds.nasdaq.com/nasdaq/categories?category=Options"];

	var count = urls.length
	for (var url in urls){
		getRSSFeedFromURL(urls[url], function (err, body) {
			if (err){
				// ignore error and attempt to process other
				console.log(err);
			} else {
				var items = convertBodyToJSON(body, function (items){
					newsJSON = newsJSON.concat(items);
					count -= 1;
					if (count === 0) {
						newsJSON.sort(compare);
						return res.json(newsJSON);
					}
				});
			}
		});
	}
};


function getRSSFeedFromURL (url, cb) {
	request(url, function (err, response, body) {
		if (err) {
			console.log(err);
			 cb(err); 
		} else {
			cb(null, body);
		}
	})
}

function convertBodyToJSON(body, cb){
	var retArr = [];
	parseString(body, function (err, result) {
	    var items = result.rss.channel[0].item;
	    for (i in items) {
		    var item = items[i];
		    var prettyItem = {pubDate: item.pubDate[0], desc: item.description[0], title: item.title[0], link: item.link[0]};
		    retArr.push(prettyItem)
		}
		cb(retArr);
	});
}
function compare(a, b){
	var dateA = convertPubDateToDateObj(a.pubDate);
	var dateB = convertPubDateToDateObj(b.pubDate);
	if (dateA < dateB){
		return 1;
	} else if (dateA === dateB) {
		return 0
	} else {
		return -1;
	}
}

function convertPubDateToDateObj(pubDate){
	//Fri, 06 Nov 2015 10:04:51 -0500
	//YYYY-MM-DDTHH:MM:SS
	var parts = pubDate.split(" ");
	var month = getMonthFromString(parts[2]);
	if (month < 10) 
		month = "0" + month
	var dateString = parts[3] + "-" + month + "-" + parts[1] + "T" + parts[4]
	function getMonthFromString(mon){
   		return new Date(Date.parse(mon +" 1, 2012")).getMonth()+1
	}

	return new Date(dateString)
}