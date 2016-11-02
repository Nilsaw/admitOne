'use strict';
//    Angular app definition

var parent_app = angular.module('admit-one', ['ui.router', 'ui.bootstrap', 'smart-table']);



//    Defining the routing

parent_app.config(function ($stateProvider, $urlRouterProvider, $httpProvider) {

    $urlRouterProvider.otherwise("/login");
    $stateProvider
        .state('results', {
            url: '/results',
            views: {
                '': {
                    controller: 'main_header_ctrl',
                    templateUrl: 'partials/main.html'
                },
                'body@results': {
                    controller: 'table_ctrl',
                    templateUrl: 'partials/tableBody.html'
                }
            }
        })
        .state('search', {
            url: '/search',
            views: {
                '': {
                    controller: 'main_header_ctrl',
                    templateUrl: 'partials/main.html'
                },
                'body@search': {
                    controller: 'search_ctrl',
                    templateUrl: 'partials/searchForm.html'
                }
            }
        })
        .state('login', {
            url: '/login',
            views: {
                '': {
                    templateUrl: 'partials/loginPage.html'
                },
                'body@login': {
                    controller: 'login_ctrl',
                    templateUrl: 'partials/loginBox.html'
                }
            }
        });
});

//      Controller used by the login popup

parent_app.controller('login_ctrl', ['$scope', '$rootScope', '$location', function ($scope, $rootScope, $location) {
    $scope.isError = false;
    $scope.submitForm = function (isValid, user) {
        console.log(user);
        if (isValid) {
            if (user == undefined || !user.hasOwnProperty('username') || user.username != 'userOne' || !user.hasOwnProperty('password') || user.password != 'userOne')
                $scope.isError = true;
            else {
                window.localStorage.setItem('USER', user.username);
                $scope.isError = false;
                $location.path('search');
            }
        }else
        	$scope.isError = true;
    };
}]);

//   Controller used by the assign to me popup

parent_app.controller('search_ctrl', ['$scope', '$rootScope', '$location', '$http', function ($scope, $rootScope, $location, $http) {
    if (window.localStorage['USER'] != 'userOne') {
        console.log(window.localStorage['USER']);
        $location.path('login');
    }
    $scope.isError = false;
    $scope.data = {};
    $scope.submitForm = function (isValid) {
        console.log($scope.data);
        if (isValid) {
            if ($scope.data==undefined || !$scope.data.hasOwnProperty('startId') || !$scope.data.hasOwnProperty('endId') || isNaN($scope.data.startId) || isNaN($scope.data.endId) || $scope.data.startId > 99 || $scope.data.endId > 99 || $scope.data.startId > $scope.data.endId)
                $scope.isError = true;
            else {
                $scope.isError = false;
//                setTimeout(function(){
//                    $rootScope.$broadcast('searchParams', $scope.data);
//                }, 1000)
                
                $location.path('results').search({
                    startId:$scope.data.startId, endId:$scope.data.endId
                });

            }
        }else
        	$scope.isError = true;
    };
}]);


//   Controller used by the main header for logging out

parent_app.controller('main_header_ctrl', ['$scope', '$filter', function ($scope, $filter) {
    $scope.user = window.localStorage['USER'];
    $scope.logout = function () {
        console.log('logging out');
        window.localStorage.removeItem('USER');
    }
}]);

//Controller used by the search table

parent_app.controller('table_ctrl', ['$scope', '$rootScope', '$location', '$http', function ($scope, $rootScope, $location, $http) {
    
    $scope.rowCollection;
    $scope.rowCollection_backup;
//    $rootScope.$on('searchParams', function (events, args){
//        console.log(args);
//        $scope.loadBookings(args.startId, args.endId);
//    });
    
    $scope.loadBookings = function (startId, endId) {
        console.log('Calling the service method');
        $http({
            method: 'GET',
            url: '/booking_data?event_id_start='+startId+'&event_id_end='+endId
        }).then(function successCallback(response) {
            console.log(response['data']);
            $scope.rowCollection_backup = response['data'];
            $scope.rowCollection = $scope.rowCollection_backup;
            console.log($scope.rowCollection);
        }, function errorCallback(response) {
            console.log(response);
        });
    };
    var params = $location.search();
    $scope.loadBookings(params.startId, params.endId);

}]);