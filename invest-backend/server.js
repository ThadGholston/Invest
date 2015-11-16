// Get the packages we need
var express             = require('express');
var passport            = require('passport');
var TokenStrategy       = require('passport-accesstoken').Strategy;
var bodyParser          = require('body-parser');
var loginController     = require('./controllers/login');
var authController      = require('./controllers/auth');
var favoriteController  = require('./controllers/favorite');
var graphController     = require('./controllers/graph');
var newsController      = require('./controllers/news');
var accountController   = require('./controllers/account');
var priceController     = require('./controllers/price');
var logoutController    = require('./controllers/logout');
var reauthController    = require('./controllers/reauth');
var originalController  = require('./controllers/original');
var searchController    = require('./controllers/search');

// Create our Express application
var app = express();
app.set('view engine', 'jade');
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
var port = 8080;

// Create our Express router
var router = express.Router();

router.route('/login')
  .post(loginController.postLogin);

router.route('/logout')
  .post(authController.isAuthenticated, logoutController.getLogout);

router.route('/favorite')
  .get(authController.isAuthenticated, favoriteController.getFavorite)
  .post(authController.isAuthenticated, favoriteController.postFavorite)
  .put(authController.isAuthenticated, favoriteController.deleteFavorite);

router.route('/graph/:symbol')
  .get(graphController.getGraph);

router.route('/price/:symbol')
  .get(priceController.getPrice);

router.route('/news')
  .get(newsController.getNews);

router.route('/account')
  .post(authController.isAuthenticated, accountController.updateAccount)
  .get(authController.isAuthenticated, accountController.getAccount);

router.route('/reauth')
  .post(reauthController.postReauth);

router.route('/original')
  .post(originalController.postOriginal);

router.route('/search/:query')
  .get(searchController.getSearch);

// Register all our routes with /api
app.use('/api', router);

// Start the server
app.listen(port);
console.log('Magic happens on port ' + port);