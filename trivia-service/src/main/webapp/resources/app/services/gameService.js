/**
 * Define the contestant view
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	// define game endpoints
	var ENDPOINT = 'backend/game';
	var ROUND = ENDPOINT + '/active/round';
	var ALL_ROUNDS  = ENDPOINT + '/rounds';
	var CATEGORIES = ENDPOINT + '/active/round/categories';
	var CONTESTANTS = ENDPOINT + '/contestants';
	var CONTESTANT = ENDPOINT + '/active/round/contestant';
	var CATEGORY_ID_PLACEHOLDER = '{categoryId}';
	var QUESTIONS_FMT = ENDPOINT + '/active/round/categories/{categoryId}/questions';
	
	triviaApp.factory('gameService', ['$http', '$rootScope', function($http, $rootScope) {
		
		console.log('making sure the game service can init...');
		// define the service
		var service = {};
		
		service.getAllRounds = function() {
			return $http.get(ALL_ROUNDS);
		};
		
		service.getActiveRoundCategories = function() {
			return $http.get(CATEGORIES);
		};
		
		service.getActiveCategoryQuestions = function(categoryId) {
			var url = QUESTIONS_FMT.replace(CATEGORY_ID_PLACEHOLDER, categoryId);
			console.log('checking authetnication creds...');
			console.log($rootScope.authentication)
			return $http.get(url, $rootScope.authentication);
		};
		
		service.getActiveRound = function() {
			return $http.get(ROUND);
		};
		
		service.setSelectedRound = function(roundId) {
			var payload = {'roundId': roundId};
			return $http.post(ROUND, payload, $rootScope.authentication);
		};
		
		service.selectionQuestion = function(categoryId, questionId) {
			var url = QUESTIONS_FMT.replace(CATEGORY_ID_PLACEHOLDER, categoryId);
			var payload = {
				'questionId': questionId
			};
			return $http.post(url, payload);
		};
		
		service.endActiveRound = function() {
			return $http.delete(ROUND, $rootScope.authentication);
		};
		
		service.getContestants = function() {
			return $http.get(CONTESTANTS);
		};
		
		service.setActiveContestant = function(contestant) {
			return $http.post(CONTESTANT, contestant, $rootScope.authentication);
		};
		
		return service;
	}]);
	
})(angular.module('triviaApp'));