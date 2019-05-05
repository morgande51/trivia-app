/**
 * 
 */
(function(angular) {
	'use strict';
	
	// add some util stuff
	String.prototype.capitalize = function() {
		return this.charAt(0).toUpperCase() + this.slice(1);
	};
	
	var triviaApp = angular.module('triviaApp', ['ui.bootstrap']);
	
	triviaApp.factory('authenticationService', ['$http', '$window', '$rootScope', function($http, $window, $rootScope) {
		// authentication endpoint
		var AUTHENTICATION_ENDPOINT = 'backend/me';
		
		// define the exposed service object
		var service = {};
		
		service.authenticate = function(email, pwd) {
			var authCred = email + ':' + pwd;
			var auth = $window.btoa(authCred);
			//console.log(auth);	
			var headers = {"Authorization": "Basic " + auth};
			var headersAuthentication = {'headers': headers};
			$rootScope.authentication = headersAuthentication;
			return $http.get(AUTHENTICATION_ENDPOINT, headersAuthentication);
		}
		
		service.logout = function() {
			var params = {
				'action': 'logout'
			};
			return $http.get(AUTHENTICATION_ENDPOINT, {'params': params});
		}
		
		return service;
	}]);
	
	triviaApp.controller('authenticationController', ['authenticationService', '$scope', '$rootScope', function(authenticationService, $scope, $rootScope) {
		$scope.login = function() {
			var email = $scope.email;
			var pwd = $scope.pwd;
			authenticationService.authenticate(email, pwd).then(function(response) {
				var user = response.data;
				$rootScope.authenticatedUser = user;
				console.log(user);
			}, function(errorObj) {
				console.log(errorObj);
			});
		};
		
		$scope.logout = function() {
			authenticationService.logout().then(function(response) {
				$rootScope.authenticatedUser = null;
			}, function(errorObj) {
				
			});
		}
	}]);
	
	
})(angular);