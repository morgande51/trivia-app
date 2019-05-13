/**
 * Define the host view
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';

	triviaApp.directive('hostView', [function() {
		
		var controller = ['$rootScope', '$scope', 'gameService', 'notificationService', 'hostService', function ($rootScope, $scope, gameService, notificationService, hostService) {
			var vm = this;
			
			var _questionKeys;
			var _questionTimer;
			
			function init() {
				// set vm properties
				_questionKeys = new Set();
				vm.activeQuestionTimerValue = 0;
				vm.buzzerEvent = false;
				vm.contestantRecognized = false;
				vm.activeQuestion = null;
				vm.wrongContestants = new Array();
				
				gameService.getContestants().then(function (results) {
					_setContestants(results.data);
				}).then(function() {
					gameService.getActiveGame().then(function (response) {
						var gameState = response.data;
						if (gameState) {
							var round = gameState.activeRound;
							if (round) {
								_setActiveRound(round);
								_getActiveRoundCategories(gameState.activeQuestion);
							}
							var activeContestant = gameState.activeContestant;
							if (activeContestant) {
								_setActiveContestant(activeContestant, false);
							}
						}
					}, function(error) {
						console.log('no active game present');
					});
				});
				
				// make API calls to backend GameService
				gameService.getAllRounds().then(function(results) {
					_setAllRounds(results.data);
				});
				
				// handle active contestant event
				$scope.$on(notificationService.getBuzzerActiveEventType(), function(event, activeContestant) {
					console.log('active contestant...');
					console.log(activeContestant);
					_setActiveContestant(activeContestant, true);
				});
				
				// handle active question event
				$scope.$on(notificationService.getActiveQuestionEventType(), function(event, selectedQuestion) {
					console.log('pulse check...');
					console.log(vm);
					console.log(vm.activeCategories);
					var question = _locateCategoryQuestion(selectedQuestion);
					_setActiveQuestion(question);
					_clearContestantsWrongAnswers();
				});
			}
			
			init();
			
			function _setAllRounds(rounds) {
				vm.rounds = rounds;
			}
			
			function _setActiveRoundCategories(categories) {
				vm.activeCategories = categories;
				var promise;
				categories.forEach(function(category) {
					promise = _loadCategoryQuestions(category);
				});
				return promise;
			}
			
			function _getActiveRoundCategories(selectedQuestion) {
				gameService.getActiveRoundCategories().then(function (results) {
					var categories = results.data;
					var promise = _setActiveRoundCategories(categories);
					if (selectedQuestion) {
						promise.then(function() {
							var question = _locateCategoryQuestion(selectedQuestion);
							_setActiveQuestion(question, true);
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
			
			function _setContestants(contestants) {
				vm.contestants = contestants;
				_clearContestantsWrongAnswers();
			}
			
			function _clearContestantsWrongAnswers() {
				vm.contestants.forEach(function(c, index) {
					c.wrongAnswered = false;
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
				question.selected = true;
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
			
			vm.getQuestionKeys = function() {
				var keys = Array.from(_questionKeys);
				return keys;
			};
			
			vm.recognizeContestant = function() {
				vm.contestantRecognized = true;
				vm.activeQuestionTimerValue = 100;
				vm.activeContestant.hasAnswered = true;
				_questionTimer = setInterval(function() {
					$scope.$apply(function() {
					vm.activeQuestionTimerValue -= 10;
						if (vm.activeQuestionTimerValue === 0) {
							vm.sendAnswer('INCORRECT');
						}
					});
				}, 1000);
			};
			
			vm.activateContestant = function(contestant) {
				gameService.setActiveContestant(contestant).then(function() {
					_setActiveContestant(contestant, false);
				});
			};
			
			vm.setActiveRound = function(round) {
				gameService.setSelectedRound(round.id).then(function (results) {
					_setActiveRound(round);
					_getActiveRoundCategories();
				});
			};
			
			vm.endActiveRound = function() {
				gameService.endActiveRound().then(function() {
					_setActiveRound(null);
					_setActiveContestant(null, false);
					vm.activeCategories = null;
				});
			};
			
			vm.sendAnswer = function(answer) {
				console.log('host says the contestant answer is: ' + answer);
				_clearTimer();
				vm.activeQuestion.answerType = answer;
				vm.activeQuestion.selected = false;
				var questionValue = vm.activeQuestion.value;
				switch (answer) {
					case 'CORRECT':
						vm.activeQuestion = null;
						_clearContestantsWrongAnswers();
						vm.activeContestant.totalScore += questionValue;
						break;
						
					case 'INCORRECT':
						vm.activeContestant.wrongAnswered = true;
						vm.activeContestant.totalScore -= questionValue;
						break;
						
					default:
						vm.activeContestant = null;
						vm.activeQuestion = null;
						_clearContestantsWrongAnswers();
				}
			
				hostService.sendActiveQuestionAnswer(answer).catch(function (error) {
					throw error;
				});
			};
		}];
		
		return {
			restrict: 'E',
			replace: true,
			templateUrl: 'resources/app/directives/hostView/template.html',
			controller: controller,
			controllerAs: 'vm',
			scope: {},
			bindToController: true
		}
	}]);
})(angular.module('triviaApp'));