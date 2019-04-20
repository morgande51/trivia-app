/**
 * Define the contestant view
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	// sse events
	var BUZZER_ACTIVE_CONTESTANT_EVENT = 'sse.buzzer.contestant.active';
	var BUZZER_CLEAR_EVENT = 'sse.buzzer.clear';
	
	var BUZZER_BTN_SUCCESS = 'btn-success';
	var BUZZER_BTN_WARN = 'btn-warning';
	var BUZZER_BTN_PRIMARY = 'btn-primary';
	var BUZZER_BTN_ERROR = 'btn-danger';
	var BUZZER_BTN_DEFAULT = 'btn-default';
	
	var CORRECT_ANSWER_TYPE = "CORRECT";
	var INCORRECT_ANSWER_TYPE = 'INCORRECT';
	var NO_ANSWER_ANSWER_TYPE = 'NO_ANSWER';
	
	triviaApp.directive('contestant', [function() {
		
		var controller = ['$rootScope', 'contestantService', function ($rootScope, contestantService) {
			var vm = this;
			
			function init() {
				_setBuzzerActive(false);
				_setAnsweredCorrectly(false);
				_setActiveContestantIndicator(false);
				_setBuzzerButtonStatus(BUZZER_BTN_DEFAULT);
				
				// handle buzzer clear event
				$rootScope.$on(BUZZER_CLEAR_EVENT, function() {
					_setBuzzerActive(false);
					_setActiveContestantIndicator(false);
					_setBuzzerButtonStatus(BUZZER_BTN_DEFAULT);
				});
				$rootScope.$on(BUZZER_ACTIVE_CONTESTANT_EVENT, function(activeContestant) {
					if (_isThisYou(activeContestant.userEmail)) {
						_promptContestant();
					}
				});
			}
			
			function _setBuzzerActive(active) {
				vm.buzzerActive = active;
			}
			
			function _setActiveContestantIndicator(indicator) {
				vm.activeContestant = indicator;
			}
			
			function _setAnsweredCorrectly(indicator) {
				vm.answeredCorrectly = indicator;
			}
			
			function _setBuzzerButtonStatus(status) {
				vm.buzzerStatus = status;
			}
			
			function _isThisYou(email) {
				return ($rootScope.authenticatedUser.userEmail === email);
			}
			
			function _promptContestant() {
				_setActiveContestantIndicator(true);
				_setBuzzerButtonStatus(BUZZER_BTN_SUCCESS);
				contestantService.whenSelected().then(function (confirm) {
					var confirmData = confirm.data;
					console.log(confirmData);
					var isUser = _isThisYou(confirmData.answeringContestantEmail);
					
					// verify this confirmation matches the authenticated user
					if (!isUser) {
						console.log('odd, this contestant doesnt match the host!!!');
						return;
					}
					
					var correctAnswer = (confirmData.answerType === CORRECT_ANSWER_TYPE);
					_setAnsweredCorrectly(correctAnswer);
					_setActiveContestantIndicator(correctAnswer);
					if (correctAnswer) {
						_setBuzzerButtonStatus(BUZZER_BTN_DEFAULT);
					}
					else {
						_setBuzzerButtonStatus(BUZZER_BTN_ERROR);
					}
				});
			}
			
			init();
			
			vm.buzzIn = function() {
				if (!vm.buzzerActive) {
					_setBuzzerActive(true);
					console.log('btn is locked...now what?');
					contestantService.buzzIn().then(function (response) {
						var data = response.data;
						console.log('we have buzzed in...lets look at payload');
						console.log(data);
						if (data.first) {
							_promptContestant();
						}
						else {
							console.log('your not the first!!!  now what?');
							_setBuzzerButtonStatus(BUZZER_BTN_WARN);
						}
					}, function (error) {
						console.log('oh noooo!');
						console.log(error);
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