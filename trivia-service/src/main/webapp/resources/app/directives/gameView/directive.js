/**
 * Define the game view
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	var CORRECT_ANSWER_TYPE = 'CORRECT';
	var INCORRECT_ANSWER_TYPE = 'INCORRECT';
	var NO_ANSWER_ANSWER_TYPE = 'NO_ANSWER';
	
	triviaApp.directive('gameView', [function() {
		
		var controller = ['$rootScope', '$scope', 'gameService', 'notificationService', function ($rootScope, $scope, gameService, notificationService) {
			var vm = this;
			
			var _questionKeys;
			
			function init() {
				// set vm properties
				_questionKeys = new Set();
				vm.activeQuestion = null;
				
				gameService.getContestants().then(function (results) {
					_setContestants(results.data);
				}).then(function() {
					gameService.getActiveGame().then(function (response) {
						var gameState = response.data;
						var round = gameState.activeRound;
						_setActiveRound(round);
						_setActiveRoundCategories(gameState.activeQuestion);					
						var activeContestant = gameState.activeContestant;
						_setActiveContestant(activeContestant);
					}, function(error) {
						console.log('No active game');
					})
				});
				
				// handle active contestant event
				$scope.$on(notificationService.getBuzzerActiveEventType(), function(event, activeContestant) {
					_setActiveContestant(activeContestant, true);
				});
				
				// handle active question event
				$scope.$on(notificationService.getActiveQuestionEventType(), function(event, selectedQuestion) {
					var question = _locateCategoryQuestion(selectedQuestion);
					_setActiveQuestion(question);
					_clearContestantsAnswerTypes();
				});
				
				// handle buzzer clear notification
				$scope.$on(notificationService.getBuzzerClearEventType(), function(event, answerPayload) {
					if (vm.activeContestant && vm.buzzerEvent) {
						vm.activeContestant.answerType = answerPayload.answerType;
					}
					_setActiveQuestion(null);
					vm.buzzerEvent = false;
				});
			}
			
			init();
			
			function _setActiveRoundCategories(selectedQuestion) {
				gameService.getActiveRoundCategories().then(function (results) {
					var categories = results.data;
					vm.activeCategories = categories;
					var promise;
					categories.forEach(function(category) {
						promise = _loadCategoryQuestions(category);
					});
					if (selectedQuestion) {
						promise.then(function() {
							var question = _locateCategoryQuestion(selectedQuestion);
							_setActiveQuestion(question);
						});
					}
				});
			}
			
			function _loadCategoryQuestions(category) {
				var promise = null;
				promise = gameService.getActiveRoundCategoryQuestions(category.id).then(function(response) {
					var questions = response.data;
					
					var questionValueMap = {};
					var questionKeys = new Array();
					questions.forEach(function(question, i) {
						var qv = question.value;
						questionValueMap[qv] = question;
						questionKeys.push(qv);
					});
					
					// sort the list of questionKeys
					questionKeys.sort(function(a, b){return a - b});
					questionKeys.forEach(function(key) {
						_questionKeys.add(key);
					});
//					vm.questionKeys.add(questionKeys);
					category.questions = questionValueMap;
					
//					console.log(_questionKeys);
				});
				return promise;
			}
			
			function _setActiveRound(round) {
				vm.activeRound = round;
			}
			
			function _clearTimer() {
				console.log('timer clearing???');
				clearInterval(_questionTimer);
				vm.buzzerEvent = false;
				vm.contestantRecognized = false;
			}
			
			function _setActiveContestant(c, notificationEvent) {
				var contestant;
				if (c) {
					contestant = _findContestant(c);
					console.log('New Active constestant' + contestant.fullName);
					console.log('buzzer clicked: ' + notificationEvent);
					if (notificationEvent && vm.activeQuestion) {
						vm.buzzerEvent = notificationEvent;
					}
				}
				else {
					contestant = null;
				}				
				vm.activeContestant = contestant;
			}
			
			function _findContestant(c) {
				var foundContestant = null;
				for (var i in vm.contestants) {
					var contestant = vm.contestants[i];
					if (c.id === contestant.id) {
						foundContestant = contestant;
						break;
					}
				}
				return foundContestant;
			}
			
			function _setContestants(contestants) {
				vm.contestants = contestants;
				_clearContestantsWrongAnswers();
			}
			
			function _clearContestantsAnswerTypes() {
				vm.contestants.forEach(function(c, index) {
					c.answerType = null;
				});
			}
			
			function _findContestant(c) {
				var foundContestant = null;
				for (var i in vm.contestants) {
					var contestant = vm.contestants[i];
					if (c.id === contestant.id) {
						foundContestant = contestant;
						break;
					}
				}
				return foundContestant;
			}
			
			function _setActiveQuestion(question) {
				if (question) {
					question.selected = true;
				}
				vm.activeQuestion = question;
				// clear any answered state for contestants
				if (vm.contestants) {
					vm.contestants.forEach(function (c) {
						c.hasAnswered = false;
					});
				}
			}
			
			function _locateCategoryQuestion(selectedQuestion) {
				var target;
				console.log('check the amount of categories we got...');
				console.log(vm.activeCategories);
				for (var index in vm.activeCategories) {
					var category = vm.activeCategories[index];
					var selectedCategory = selectedQuestion.category;
					if (category.id === selectedCategory.id) {
						var key = selectedQuestion.value;
						target = category.questions[key];
						break;
					}
				}
				return target;
			}
			
			function _locateCategoryQuestion(selectedQuestion) {
				var target;
				console.log('check the amount of categories we got...');
				console.log(vm.activeCategories);
				for (var index in vm.activeCategories) {
					var category = vm.activeCategories[index];
					var selectedCategory = selectedQuestion.category;
					if (category.id === selectedCategory.id) {
						var key = selectedQuestion.value;
						target = category.questions[key];
						break;
					}
				}
				return target;
			}
			
			function _clearContestantsWrongAnswers() {
				vm.contestants.forEach(function(c, index) {
					c.wrongAnswered = false;
				});
			}
			
			vm.getQuestionKeys = function() {
				var keys = Array.from(_questionKeys);
				return keys;
			};
		}];
		
		return {
			restrict: 'E',
			replace: true,
			templateUrl: 'resources/app/directives/gameView/template.html',
			controller: controller,
			controllerAs: 'vm',
			scope: {},
			bindToController: true
		}
	}]);
})(angular.module('triviaApp'));