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
				
				// make API calls to backend GameService
				gameService.getAllRounds().then(function(results) {
					_setAllRounds(results.data);
				});
				gameService.getActiveRound().then(function (results) {
					var round = results.data;
					_setActiveRound(round);
					_getActiveRoundCategories();
				}, function(error) {
					console.log('status');
					console.log(error.status);
					if (error.status != 404) {
						throw error;
					}
				});
				gameService.getContestants().then(function (results) {
					_setContestants(results.data);
				});
				
				// handle active contestant event
				$rootScope.$on(notificationService.getBuzzerActiveEventType(), function(event, activeContestant) {
					console.log('active contestant...');
					console.log(activeContestant);
					_setActiveContestant(activeContestant, true);
				});
				
				// handle active question event
				$rootScope.$on(notificationService.getActiveQuestionEventType(), function(event, selectedQuestion) {
					console.log('pulse check...');
					console.log(vm);
					console.log(vm.activeCategories);
					for (var index in vm.activeCategories) {
						var category = vm.activeCategories[index];
						var selectedCategory = selectedQuestion.category;
						if (category.id === selectedCategory.id) {
							var key = selectedQuestion.value;
							var question = category.questions[key];
							question.selected = true;
							vm.activeQuestion = question;
							break;
						}
					}
					_clearContestantsWrongAnswers();
				});
			}
			
			init();
			
			function _setAllRounds(rounds) {
				vm.rounds = rounds;
			}
			
			function _setActiveRoundCategories(categories) {
				vm.activeCategories = categories;
				categories.forEach(_loadCategoryQuestions);
			}
			
			function _getActiveRoundCategories() {
				gameService.getActiveRoundCategories().then(function (results) {
					var categories = results.data;
					_setActiveRoundCategories(categories);
				});
			}
			
			function _loadCategoryQuestions(category) {
				gameService.getActiveCategoryQuestions(category.id).then(function(response) {
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
			
			function _setActiveContestant(c, buzzerEvent) {
				var contestant = _findContestant(c);
				console.log('New Active constestant' + contestant.fullName);
				vm.activeContestant = contestant;
				vm.buzzerEvent = buzzerEvent;
				console.log('buzzer clicked: ' + buzzerEvent);
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
				var foundContestant;
				for (var i in vm.contestants) {
					var contestant = vm.contestants[i];
					if (c.id === contestant.id) {
						foundContestant = contestant;
						break;
					}
				}
				return foundContestant;
			}
			
			vm.getQuestionKeys = function() {
				var keys = Array.from(_questionKeys);
				return keys;
			};
			
			vm.recognizeContestant = function() {
				vm.contestantRecognized = true;
				vm.activeQuestionTimerValue = 100;
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
				switch (answer) {
					case 'CORRECT':
						vm.activeQuestion = null;
						_clearContestantsWrongAnswers();
						break;
						
					case 'INCORRECT':
						vm.activeContestant.wrongAnswered = true;
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