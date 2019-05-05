/**
 * Define the host service
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	// define contestant endpoints
	var ENDPOINT = 'backend/host';
	var ANSWER = ENDPOINT + '/answer';
	
	triviaApp.factory('hostService', ['$http', '$rootScope', function($http, $rootScope) {
		
		// declare the service
		var service = {};
		
		service.sendActiveQuestionAnswer = function(answer) {
			var payload = {
				'answerType': answer
			}
			return $http.post(ANSWER, payload, $rootScope.authentication);
		};		
		
		return service;
	}]);
})(angular.module('triviaApp'));