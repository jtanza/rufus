var app = angular.module('home', ['ngRoute']);

app.config(function($routeProvider) {
    $routeProvider

        .when('/', {
            templateUrl: 'pages/articles.html',
            controller: 'homeController'
        })

        .when('/bookmarked', {
            templateUrl: 'pages/articles.html',
            controller: 'bookmarkedController'
        })

        .when('/frontpage', {
            templateUrl: 'pages/articles.html',
            controller: 'frontpageController'
        })

        .when('/all', {
            templateUrl: 'pages/articles.html',
            controller: 'allController'
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
        });
});


app.controller('homeController', function($scope, $http) {

    $http.get('api/articles/frontpage').then(function(response) {
        $scope.articles = response.data;
    });

    $http.get('api/articles/tagStubs').then(function(response) {
        $scope.tags = response.data;
    });

    $scope.bookmark = function(article) {
        //TODO if not successful alert!
        if (article.bookmark) {
           $http.post('api/articles/removeBookmark', article);
           article.bookmark = false;
        } else {
           $http.post('api/articles/bookmark', article);
           article.bookmark = true;
         }
    }

});

app.controller('bookmarkedController', function($scope, $http) {

    $http.get('api/articles/bookmarked').then(function(response) {
        $scope.articles = response.data;
    });

    $scope.bookmark = function(article) {
        //TODO if not successful alert!
        if (article.bookmark) {
           $http.post('api/articles/removeBookmark', article);
           article.bookmark = false;
        } else {
           $http.post('api/articles/bookmark', article);
           article.bookmark = true;
         }
    }
})

app.controller('frontpageController', function($scope, $http) {
    $http.get('api/articles/frontpage').then(function(response) {
        $scope.articles = response.data;
    });
})

app.controller('allController', function($scope, $http) {
    $http.get('api/articles/all').then(function(response) {
        $scope.articles = response.data;
    });
})

app.controller('taggedController', function($scope, $http, $routeParams) {
    $http.get('api/articles/tagged?tag=' + $routeParams.param).then(function(response) {
        $scope.articles = response.data;
    });
})

app.controller('addFeedsController', function($scope, $http) {
    $scope.add = function(feed) {
        var feedArray = feed.split(" ");
        $http.post('api/articles/new', feedArray).then(function(response) {
            console.log(response);
        })
    };
})