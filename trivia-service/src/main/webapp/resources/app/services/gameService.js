/**
 * Define the contestant view
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	// define game endpoints
	var ENDPOINT = 'backend/game';
	var CATEGORIES = ENDPOINT + '/active/categories';
	var CATEGORY_ID_PLACEHOLDER = '{categoryId}';
	var QUESTIONS_FMT = ENDPOINT + '/active/categories/{categoryId}/questions';
	
	triviaApp.factory('triviaService', '$http', '$rootScope', [function($http, $rootScope) {
		
		// define the service
		var service = {};
		
		service.getActiveRoundCategories = function() {
			return $http.get(CATEGORIES);
		}
		
		service.getActiveCategoryQuestions = function(categoryId) {
			var url = QUESTIONS_FMT.replace(CATEGORY_ID_PLACEHOLDER, categoryId);
			return $http.get(url);
		}
		
		return service;
	}]);
	
})(angular.module('triviaApp'));