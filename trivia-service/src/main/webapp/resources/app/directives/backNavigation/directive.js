/**
 * Define the backspace navigation directive
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	triviaApp.directive('preventBackspace', ['$window', function($window) {
		
		function preventBackspack() {
			$window.onbeforeunload = function() { 
				return "Are you sure you want to leave?"; 
			}
		}
		
		return {
			restrict: 'A',
			link: preventBackspack
		}
	}]);
})(angular.module('triviaApp'));