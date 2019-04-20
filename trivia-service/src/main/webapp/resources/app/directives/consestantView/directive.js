/**
 * Define the contestant view
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	triviaApp.directive('contestantView', [function() {
		
		// sse events
		var BUZZER_ACTIVE_CONTESTANT_EVENT = "sse.buzzer.contestant.active";
		var BUZZER_CLEAR_EVENT = "sse.buzzer.clear";
		
		var controller = ['$rootScope', 'contestantService', function ($rootScope, contestantService) {
			var vm = this;
			
			function init() {
				vm.buzzerActive = false;
				vm.answeredCorrectly = false;
				vm.activeContestant = false;
				
				// handle buzzer clear event
				$rootScope.$on(BUZZER_CLEAR_EVENT, function() {
					_setBuzzerActive(false);
					_setActive(false);
				});
				$rootScope.$on(BUZZER_ACTIVE_CONTESTANT_EVENT, function() {
					_setActive(true);
				})
				
			}
			
			function _setBuzzerActive(active) {
				vm.buzzerActive = active;
			}
			
			function _setActive(indicator) {
				vm.activeContestant = indicator;
			}
			
			function _setAnsweredCorrectly(indicator) {
				vm.answeredCorrectly = indicator;
			}
			
			init();
			
			vm.buzzIn = function() {
				if (!vm.buzzerActive) {
					_setBuzzerActive(true);
					contestantService.buzzIn().then(function (response) {
						var data = response.data;
						if (data.isFirst) {
							_setActive(true);
							contestantService.questionConfirmation().then(function (confirm) {
								var confirmData = confirm.data;
								var answeredIndicator = ($rootScope.authenticatedUser.userEmail === confirmData.userEmail);
								_setAnsweredCorrectly(answeredIndicator);
							});
						}
					});
				}
			};
		}];
		
		return {
			restrict: 'E',
			replace: true,
			templateUrl: 'resources/app/directives/contestantView/template.html',
			controller: controller,
			controllerAs: 'vm',
			bindToController: true
		}
	}]);
})(angular.module('triviaApp'));