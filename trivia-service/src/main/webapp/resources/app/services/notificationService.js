/**
 * Define the Notification Service
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	// sse events constants
	var NOTIFICATIONS = 'backend/game/notifications';
	
	var BUZZER_ACTIVE_EVENT = 'sse.buzzer.contestant.active';
	var BUZZER_CLEAR_EVENT = 'sse.buzzer.clear';
	var ROUND_END_EVENT = 'sse.host.round.end';
	var ACTIVE_ROUND_EVENT = 'sse.host.round.active';
	var ACTIVE_QUESTION_EVENT = 'sse.contestant.question.active';
	
	triviaApp.service('notificationService', ['$rootScope', function($rootScope) {
		
		// register for sse
		var sse = new EventSource(NOTIFICATIONS);
		sse.addEventListener(BUZZER_CLEAR_EVENT, function(event) {
			var answerPayload = JSON.parse(event.data);
			$rootScope.$apply(function() {
				$rootScope.$broadcast(BUZZER_CLEAR_EVENT, answerPayload);
			});
		});
		sse.addEventListener(BUZZER_ACTIVE_EVENT, function(event) {
			var activeContestant = JSON.parse(event.data);
			$rootScope.$apply(function() {
				$rootScope.$broadcast(BUZZER_ACTIVE_EVENT, activeContestant);
			});
		});
		sse.addEventListener(ROUND_END_EVENT, function (event) {
			$rootScope.$apply(function() {
				$rootScope.$broadcast(ROUND_END_EVENT);
			});
		});
		sse.addEventListener(ACTIVE_ROUND_EVENT, function (event) {
			var round = JSON.parse(event.data);
			$rootScope.$apply(function() {
				$rootScope.$broadcast(ACTIVE_ROUND_EVENT, round);
			});
		});
		sse.addEventListener(ACTIVE_QUESTION_EVENT, function (event) {
			var question = JSON.parse(event.data);
			console.log('activeQuestion');
			console.log(question);
			console.log(question.value);
			console.log(question.category.name);
			$rootScope.$apply(function() {
				$rootScope.$broadcast(ACTIVE_QUESTION_EVENT, question);
			});
		});
		
		this.getBuzzerActiveEventType = function() {
			return BUZZER_ACTIVE_EVENT;
		};
		
		this.getBuzzerClearEventType = function() {
			return BUZZER_CLEAR_EVENT;
		};
		
		this.getActiveRoundEventType = function() {
			return ACTIVE_ROUND_EVENT;
		};
		
		this.getRoundEndEventType = function() {
			return ROUND_END_EVENT;
		};
		
		this.getActiveQuestionEventType = function() {
			return ACTIVE_QUESTION_EVENT;
		}
	}]);
})(angular.module('triviaApp'));	