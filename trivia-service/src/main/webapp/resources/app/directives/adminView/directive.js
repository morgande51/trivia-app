/**
 * Define the admin view
 * 
 * @Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	var CORRECT_ANSWER_TYPE = 'CORRECT';
	
	triviaApp.directive('adminView', [function() {
			
		var controller = ['$rootScope', '$scope', 'gameService', 'contestantService', 'notificationService', function($rootScope, $scope, gameService, conestantService, notificationService) {
			var vm = this;
			
			function init() {
				gameService.getActiveGame().then(function (response) {
					var gameState = response.data;
					_setActiveRound(gameState.activeRound);
					_setActiveQuestion(gameState.activeQuestion);
					_setActiveContestant(gameState.activeContestant);
				}, function(error) {
					console.log('No active game');
				});
				
				// handle active contestant event
				$scope.$on(notificationService.getBuzzerActiveEventType(), function(event, activeContestant) {
					_setActiveContestant(activeContestant);
				});
				
				// handle active question event
				$scope.$on(notificationService.getActiveQuestionEventType(), function(event, selectedQuestion) {
					_setActiveQuestion(selectedQuestion);
				});
				
				// handle buzzer clear notification
				$scope.$on(notificationService.getBuzzerClearEventType(), function(event, answerPayload) {
					_setActiveQuestion(null);
					var answerType = answerPayload.answerType;
					if (answerType != CORRECT_ANSWER_TYPE) {
						_setActiveContestant(null);
					}
				});
				
				// handle new activeRound
				$scope.$on(notificationService.getActiveRoundEventType(), function (event, round) {
					_setActiveRound(round);
				});
				
				// handle new activeRound
				$scope.$on(notificationService.getRoundEndEventType(), function (event) {
					_setActiveRound(null);
					_setActiveQuestion(null);
					_setActiveContestant(null);
				});
			}
			init();
			
			function _setActiveRound(round) {
				vm.activeRound = round;
			}
			
			function _setActiveQuestion(question) {
				vm.activeQuestion = question;
			}
			
			function _setActiveContestant(contestant) {
				vm.activeContestant = contestant;
			}
			
			vm.clearBuzzer = function() {
				conestantService.clearBuzzer().catch(function(error) {
					console.log(error);
				});
			};
			
			vm.clearActiveQuestion = function() {
				gameService.clearActiveQuestion().catch(function(error) {
					console.log(error);
				});
			};
			
			vm.endRound = function() {
				gameService.endActiveRound().catch(function(error) {
					console.log(error);
				});
			};
		}];
		
		return {
			restrict: 'E',
			replace: true,
			templateUrl: 'resources/app/directives/adminView/template.html',
			controller: controller,
			controllerAs: 'vm',
			scope: {},
			bindToController: true
		}
	}]);
	
})(angular.module('triviaApp'));