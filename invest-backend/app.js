// Get the packages we need
var express             = require('express');
var passport            = require('passport');
var TokenStrategy       = require('passport-accesstoken').Strategy;
var bodyParser          = require('body-parser');
var loginController     = require('./controllers/login');
var authController      = require('./controllers/auth');
var favoriteController  = require('./controllers/favorite');
var stockController     = require('./controllers/stock');
var newsController      = require('./controllers/news');
var accountController      = require('./controllers/account');

// Create our Express application
var app = express();
app.use(bodyParser.urlencoded({extended: true}));
app.use(bodyParser.json());
app.use(passport.initialize());
app.use(passport.session());

passport.serializeUser(function(user, done) {
  done(null, user);
});

passport.deserializeUser(function (user, done) {
  done(err, user);
});

// Use environment defined port or 80
var port = process.env.PORT || 3000;

// Create our Express router
var router = express.Router();

router.route('/login')
  .post(loginController.postLogin);

router.route('/favorite')
  .get(authController.isAuthenticated, favoriteController.getFavorite)
  .post(authController.isAuthenticated, favoriteController.postFavorite)
  .delete(authController.isAuthenticated, favoriteController.deleteFavorite);

router.route('/stock/:ticker_symbol')
  .get(stockController.getStock);

router.route('/news')
  .get(newsController.getNews);

router.route('/account')
  .post(authController.isAuthenticated, accountController.updateAccount)
  .get(authController.isAuthenticated, accountController.getAccount)
  .delete(authController.isAuthenticated, accountController.deleteAccount);


// Register all our routes with /api
app.use('/api', router);

// Start the server
app.listen(port);
console.log('Magic happens on port ' + port);