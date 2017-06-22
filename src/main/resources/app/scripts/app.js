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
        })

        .when('/error', {
            templateUrl: 'pages/error.html'
        });
});


app.controller('homeController', function($scope, $http, $location) {

    $http.get('api/articles/frontpage').then(function success(response) {
        $scope.articles = response.data;
    }, function error(response){
        $location.path('/error');
        $scope.errCode = response.status;
        $scope.errMessage = response.data.message;
    });

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

});

app.controller('bookmarkedController', function($scope, $http) {

    $http.get('api/articles/bookmarked').then(function(response) {
        $scope.articles = response.data;
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