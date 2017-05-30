/*
angular.module('home', [])
    .controller('Home', function($scope, $http) {
        $http.get('api/articles/frontpage').
        then(function(response) {
            $scope.articles = response.data;
        });
    });*/


var app = angular.module('home', []);

app.controller('Home', function($scope, $http) {
  $http.get('api/articles/frontpage').then(function(response) {
    $scope.articles = response.data;
  });

  $http.get('api/articles/tagStubs').then(function(response) {
    $scope.tags = response.data;
  });

  $scope.get = function(route, data) {
     if (data != null) {
        console.log(data);

        $http.post('api/articles/' + route, data).then(function(response) {
            $scope.articles = response.data;
        });

     } else {

        $http.get('api/articles/' + route).then(function(response) {
            $scope.articles = response.data;
        });

    }
  }

});

