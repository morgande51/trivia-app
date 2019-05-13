/**
 * Define the contestant view
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	// define contestant endpoints
	var ENDPOINT = 'backend/contestant';
	var BUZZER = ENDPOINT + '/buzzer';
	var CONTESTANT_ACTIVE = 'backend/host/answer';
	
	triviaApp.factory('contestantService', ['$http', '$rootScope', function($http, $rootScope) {
		
		// declare the service
		var service = {};
		
		service.buzzIn = function() {
			return $http.get(BUZZER, $rootScope.authentication);
		};
		
		service.withAnswer = function() {
			return $http.get(CONTESTANT_ACTIVE);
		};		
		
		return service;
	}]);
})(angular.module('triviaApp'));