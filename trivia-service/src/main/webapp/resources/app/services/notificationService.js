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
	var ACTIVE_QUESTION_CLEAR_EVENT = 'sse.contestant.question.clear';

	triviaApp.service('notificationService', ['$rootScope', function($rootScope) {
		
		// register for sse
		var sse = new EventSource(NOTIFICATIONS);
		
		// listen for buzzer clear event
		sse.addEventListener(BUZZER_CLEAR_EVENT, function(event) {
			var answerPayload = JSON.parse(event.data);
			$rootScope.$apply(function() {
				$rootScope.$broadcast(BUZZER_CLEAR_EVENT, answerPayload);
			});
		});
		
		// listen for active contestant event
		sse.addEventListener(BUZZER_ACTIVE_EVENT, function(event) {
			var activeContestant = JSON.parse(event.data);
			$rootScope.$apply(function() {
				$rootScope.$broadcast(BUZZER_ACTIVE_EVENT, activeContestant);
			});
		});
		
		// listen for round end event
		sse.addEventListener(ROUND_END_EVENT, function (event) {
			console.log('notificaiton service just got the ROUND_END_EVENT');
			$rootScope.$apply(function() {
				$rootScope.$broadcast(ROUND_END_EVENT);
			});
		});
		
		// listen for round active event
		sse.addEventListener(ACTIVE_ROUND_EVENT, function (event) {
			var round = JSON.parse(event.data);
			$rootScope.$apply(function() {
				$rootScope.$broadcast(ACTIVE_ROUND_EVENT, round);
			});
		});
		
		// listen for active question event
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
		
		// listen for question clear event
		sse.addEventListener(ACTIVE_QUESTION_CLEAR_EVENT, function(event) {
			$rootScope.$apply(function() {
				$rootScope.$broadcast(ACTIVE_QUESTION_CLEAR_EVENT);
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
		};
		
		this.getActiveQuestionClearEventType = function() {
			return ACTIVE_QUESTION_CLEAR_EVENT;
		};
	}]);
})(angular.module('triviaApp'));	