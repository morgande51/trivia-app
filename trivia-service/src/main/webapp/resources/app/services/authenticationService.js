/**
 * Define the authentication Service
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	triviaApp.factory('authenticationService', ['$http', '$window', '$rootScope', function($http, $window, $rootScope) {
		// authentication endpoint
		var AUTHENTICATION_ENDPOINT = 'backend/me';
		var AUTHORIZATION_ENDPOINT = AUTHENTICATION_ENDPOINT + '/roles';
		
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
		
		service.authorization = function() {
			return $http.get(AUTHORIZATION_ENDPOINT, $rootScope.authentication);
		}
		
		service.logout = function() {
			return $http.delete(AUTHENTICATION_ENDPOINT, {'params': params});
		}
		
		return service;
	}]);
	
})(angular.module('triviaApp'));