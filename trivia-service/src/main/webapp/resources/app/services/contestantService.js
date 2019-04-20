/**
 * Define the contestant view
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	// define contestant endpoints
	var ENDPOINT = 'backend/contestant';
	var NOTIFICATIONS = ENDPOINT + '/notifications';
	var CONTESTANT_ACTIVE = ENDPOINT + '/activate';
	var CONTESTANT_SELECTION = ENDPOINT + '/selection';
	
	// sse events
	var BUZZER_ACTIVE_CONTESTANT_EVENT = "sse.contestant.active";
	var BUZZER_CLEAR_EVENT = "sse.contestant.buzzer.clear";
	
	triviaApp.factory('contestantService', ['$http', '$rootScope', function($http, $rootScope) {
		
		// register for sse
		var sse = new EventSource(NOTIFICATIONS);
		sse.addEventListener(BUZZER_CLEAR_EVENT, function(event) {
			$rootScope.$broadcast(BUZZER_CLEAR_EVENT);
		});
		sse.addEventListener(BUZZER_ACTIVE_CONTESTANT_EVENT, function(event) {
			var activeContestant = JSON.parse(event.data);
			$rootScope.$broadcast(BUZZER_ACTIVE_CONTESTANT_EVENT, activeContestant);
		});
		
		// declare the service
		var service = {};
		
		service.buzzIn = function() {
			return $http.get(ENDPOINT);
		};
		
		service.selectionQuestion = function(categoryId, questionId) {
			var payload = {
				'categoryId': categoryId,
				'questionId': questionId
			};
			return $http.post(CONTESTANT_SELECTION, payload);
		};
		
		service.whenSelected = function() {
			return $http.get(CONTESTANT_ACTIVE);
		};		
		
		return service;
	}]);
})(angular.module('triviaApp'));