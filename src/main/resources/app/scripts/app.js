var app = angular.module('home', ['ngRoute', 'myLoginCheck']);

var login = angular.module('myLoginCheck', [])
    .factory('$logincheck', function() {
        return function(userid) {
            // Perform logical user logging. Check either
            // by looking at cookies or make a call to server.
            if (userid > 0) return true;
            return false;
        };
    });

app.config(function($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'pages/articles.html',
            controller: 'resourceController',
            resource : 'api/articles/frontpage'
        })
        .when('/bookmarked', {
            templateUrl: 'pages/articles.html',
            controller: 'resourceController',
            resource : 'api/articles/bookmarked'
        })
        .when('/frontpage', {
            templateUrl: 'pages/articles.html',
            controller: 'resourceController',
            resource : 'api/articles/frontpage'
        })
        .when('/all', {
            templateUrl: 'pages/articles.html',
            controller: 'resourceController',
            resource : 'api/articles/all'
        })
        .when('/tagged/:param', {
            templateUrl: 'pages/articles.html',
            controller: 'taggedController'
        })
        .when('/add', {
            templateUrl: 'pages/addFeeds.html',
            controller: 'addFeedsController'
        })
        .when('/about', {
            templateUrl: 'pages/about.html'
        })
        .when('/settings', {
            templateUrl: 'pages/settings.html'
        })
        .when('/login', {
            templateUrl: 'pages/login.html'
        })
        .when('/error', {
            templateUrl: 'pages/error.html'
        });
    });

 app.controller('resourceController', ['$scope', '$route', '$http', '$location', '$logincheck', function ($scope, $route, $http, $location, $logincheck) {
     var route = $route.current.$$route.resource;
     if (!$logincheck(0)) {
         $scope.loggedin = false;
         route = route.replace("articles", "public")
     }
     
    $http.get(route).then(function success(response) {
        $scope.loggedin = false;
        $scope.articles = response.data;
    }, function error(response) {
        $location.path('/error');
        $scope.errCode = response.status;
        $scope.errMessage = response.data.message;
    });
 }]);

app.controller('homeController', ['$scope', '$route', '$http', '$location', '$logincheck', function ($scope, $route, $http, $location, $logincheck) {
    if (!$logincheck(0)) {
        $http.get('api/public/tagStubs').then(function(response) {
            $scope.tags = response.data;
        });
    } else {
        $http.get('api/articles/tagStubs').then(function(response) {
            $scope.tags = response.data;
        });

        $scope.bookmark = function(article) {
            if (article.bookmark) {
                $http.post('api/articles/removeBookmark', article).then(function success(response){
                    article.bookmark = false;
                }, function error(response) {
                    $location.path('/error');
                    $scope.errCode = response.status;
                    $scope.errMessage = response.data.message;
                });
            } else {
                $http.post('api/articles/bookmark', article).then(function(response) {
                    article.bookmark = true;
                }, function error(response) {
                    $location.path('/error');
                    $scope.errCode = response.status;
                    $scope.errMessage = response.data.message;
                });
            }
        }
    }
 }]);

app.controller('taggedController', function($scope, $http, $routeParams) {
    var route = 'api/articles/tagged?tag=';
    if (!$logincheck(0)) {
        $scope.loggedin = false;
        route = route.replace("articles", "public")
    }
    $http.get(route + $routeParams.param).then(function(response) {
        $scope.articles = response.data;
    });
});

app.controller('addFeedsController', function($scope, $http) {
    $scope.add = function(feed) {
        var feedArray = feed.split(" ");
        $http.post('api/articles/new', feedArray).then(function(response) {
            console.log(response);
        })
    };
});