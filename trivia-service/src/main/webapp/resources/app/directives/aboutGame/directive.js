/**
 * Define the aboutGame directive
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	triviaApp.directive('aboutGame', [function() {
		
		var controller = ['$rootScope', function ($rootScope) {
			var vm = this;
			
			vm.watchGame = function() {
				$rootScope.selectedMode = 'game';
			};
		}];
		
		return {
			restrict: 'E',
			replace: true,
			templateUrl: 'resources/app/directives/aboutGame/template.html',
			scope: {},
			controller: controller,
			controllerAs: 'vm',
			bindToController: true
		}
	}]);
	
})(angular.module('triviaApp'));