/**
 * Define the contestant view
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	// define endpoint
	var ENDPOINT = 'backend/buzzer';
	var SSE_ENDOINT = ENDPOINT + '/register';
	var SELECTION_ENDPOINT = ENDPOINT + '/selection';
	var QUESTION_CONFIRMATION = ENDPOINT + '/confirm';
	
	// sse events
	var BUZZER_ACTIVE_CONTESTANT_EVENT = "sse.buzzer.contestant.active";
	var BUZZER_CLEAR_EVENT = "sse.buzzer.clear";
	
	triviaApp.factory('contestantService', ['$http', '$rootScope', function($http, $rootScope) {
		
		// register for sse
		var sse = new EventSource(SSE_ENDPOINT);
		sse.addEventListener(BUZZER_CLEAR_EVENT, function(event) {
			$rootScope.$broadcast(BUZZER_CLEAR_EVENT);
		});
		sse.addEventListener(BUZZER_ACTIVE_CONTESTANT_EVENT, function(event) {
			var activeContestant = JSON.parse(event.data);
			$rootScope.$broadcast(BUZZER_ACTIVE_CONTESTANT_EVENT, activeContestant);
		});
		
		// declare the service
		var service = {};
		
		service.buzzeIn = function() {
			return $http.get(ENDPOINT);
		};
		
		service.selectionQuestion = function(categoryId, questionId) {
			var payload = {
				'categoryId': categoryId,
				'questionId': questionId
			};
			return $http.post(SELECTION_ENDPOINT, payload);
		};
		
		service.questionConfirmation = function() {
			return $http.get(QUESTION_CONFIRMATION);
		};
		
		
		return service;
	}]);
})(angular.module('triviaApp'));