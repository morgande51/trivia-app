/**
 * 
 */
(function(angular) {
	'use strict';
	
	// add some util stuff
	String.prototype.capitalize = function() {
		return this.charAt(0).toUpperCase() + this.slice(1);
	};
	
	var triviaApp = angular.module('triviaApp', ['ui.bootstrap']);	
	
})(angular);