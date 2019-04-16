/**
 * 
 */
(function(angular) {
	'use strict';
	var triviaApp = angular.module('triviaApp', []);
	console.log(triviaApp);
	
	triviaApp.factory('authenticationService', ['$http', '$window', function($http, $window) {
		// authentication endpoint
		var AUTHENTICATION_ENDPOINT = '/backend/me';
		
		// define the exposed service object
		var service = {};
		
		service.authenticate = function(email, pwd) {
			var authCred = email + ':' + pwd;
			var auth = $window.btoa(authCred);
			//console.log(auth);	
			var headers = {"Authorization": "Basic " + auth};
			return $http.get(AUTHENTICATION_ENDPOINT, {headers: headers});
		}
		
		return service;
	}]);
	
	triviaApp.controller('authenticationController', ['authenticationService', '$scope', function(authenticationService, $scope) {
		$scope.login = function() {
			var email = $scope.email;
			var pwd = $scope.pwd;
			authenticationService.authenticate(email, pwd).then(function(response) {
				var user = response.data;
				$scope.$rootScope.authenticatedUser = user;
			}, function(errorObj) {
				
			});
		};
	}]);
})(angular);