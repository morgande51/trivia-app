/**
 * Define the contestant view
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	var CORRECT_ANSWER_TYPE = 'CORRECT';
	var INCORRECT_ANSWER_TYPE = 'INCORRECT';
	var NO_ANSWER_ANSWER_TYPE = 'NO_ANSWER';
	
	var ACTIVE_STATUS = 'ACTIVE';
	var RECOGNIZED_STATUS = 'RECOGNIZED';
	var PENDING_STATUS = 'PENDING';
	var INCORRECT_ANSWER_STATUS = 'INCORRECT';
	
	triviaApp.directive('contestantView', [function() {
		
		var controller = ['$scope', '$rootScope', 'contestantService', 'gameService', 'notificationService', function ($scope, $rootScope, contestantService, gameService, notificationService) {
			var vm = this;
			vm.contestant = $rootScope.authenticatedUser;
			console.log("contestant is...");
			console.log(vm.contestant);
			function init() {
				// get the current status
				_setHasBuzzedIn(false);
				gameService.getActiveGame().then(function (response) {
					var gameState = response.data;
					if (gameState) {
						var round = gameState.activeRound;
						if (round) {
							_loadActiveRoundCategories();
							
							var question = gameState.activeQuestion;
							_setActiveQuestion(question);
							if (question) {
								_setHasBuzzedIn(true);	// lock out the buzzer
							}
							
							var contestant = gameState.activeContestant;
							if (contestant && _verifyContestant(contestant)) {
								_setContestantStatus(ACTIVE_STATUS);
							}
						}
					}
				});
				
				// handle new activeRound
				$scope.$on(notificationService.getActiveRoundEventType(), function (event, round) {
					_loadActiveRoundCategories();
					vm.waitingForHost = false;
				});
				
				// handle question selected notification
				$scope.$on(notificationService.getActiveQuestionEventType(), function(event, question) {
					_setActiveQuestion(question);
					vm.waitingForHost = false;
				});
				
				// handle buzzer clear notification
				$scope.$on(notificationService.getBuzzerClearEventType(), function(event, answerPayload) {
					console.log('$scope.$on(BUZZER_CLEAR_EVENT)');
					console.log(answerPayload);
					if (vm.waitingForHost) {
						var clearWatcher;
						console.log('...attempting to wait for the host to finish before we perform buzzer clear...');
						clearWatcher = $scope.$watch('vm.waitingForHost', function(nv, ov) {
							_clearBuzzer(answerPayload.answerType);
							clearWatcher();
							vm.waitingForHost = false;
						});
					}
					else {
						_clearBuzzer(answerPayload.answerType);
					}
				});
				
				// handle active notification
				$scope.$on(notificationService.getBuzzerActiveEventType(), function(event, activeContestant) {
					var status;
					console.log('$scope.$on(BUZZER_ACTIVE_EVENT)');
					console.log(activeContestant);
					var listenForAnswer = (vm.contestantStatus == PENDING_STATUS);
					if (_verifyContestant(activeContestant)) {
						console.log('current status: ' + vm.contestantStatus);
						// if there is no active question, the contestant is active and can set the activeQuestion
						// if there is an active question, the contestant is recognized and can respond to host
						if (vm.activeQuestion == null) {
							status = ACTIVE_STATUS;
						}
						else {	
							status = RECOGNIZED_STATUS;
						}
//						_handleHostResponse();
					}
					else if (vm.activeQuestion) {
						// there is an active question, but the contestant did not buzz in
						status = PENDING_STATUS;
					}
					_setContestantStatus(status);
					
					// we might need to listen for the answer if we are pending
					if (listenForAnswer) {
						console.log('$scope.$on(BUZZER_ACTIVE_EVENT) is calling _handleHostResponse()');
						_handleHostResponse();
					}
				});
			}
			
			init();
			
			function _loadActiveRoundCategories() {
				gameService.getActiveRoundCategories().then(function (results) {
					var categories = results.data;
					_setCategories(categories);
				});
			}
			
			function _setContestantStatus(status) {
				vm.contestantStatus = status;
			}
			
			function _setActiveQuestion(question) {
				vm.activeQuestion = question;
				vm.selectedCategory = null;
				vm.selectedQuestionValue = null;
			}
			
			function _setActiveQuestionAnswer(answer) {
				if (vm.activeQuestion) {
					var selectedCategory = vm.activeQuestion.category.id;
					var questionId = vm.activeQuestion.id;
					for (var cId in vm.categories) {
						var category = vm.categories[cId];
						if (category.id == selectedCategory) {
							for (var qId in category.questions) {
								var question = category.questions[qId];
								if (question.id == questionId) {
									question.answerType = answer;
									break;
								}
							}
							break;
						}
					}
				}
			}
			
			function _setHasBuzzedIn(flg) {
				vm.hasBuzzedIn = flg;
			}
			
			function _verifyContestant(contestant) {
				return ($rootScope.authenticatedUser.id === contestant.id);
			}
			
			function _handleHostResponse() {
				vm.waitingForHost = true;
				contestantService.withAnswer().then(function (results) {
					var answer = results.data;
					console.log('contestantService.withAnswer() is returning....');
					console.log(answer);
					// if the answering contestant is you
					if (_verifyContestant(answer.contestant)) {
						$rootScope.authenticatedUser.totalScore = answer.contestant.totalScore;
						switch (answer.answerType) {
							case CORRECT_ANSWER_TYPE:
								_setContestantStatus(ACTIVE_STATUS);
								break;
								
							case INCORRECT_ANSWER_TYPE:
								_setContestantStatus(INCORRECT_ANSWER_STATUS);
								break;
								
							default:
								console.log('no noting, none has answered');
						}
					}
					else {
						console.log('contestant buzzed in, but another contestant answered correctly');
						_setContestantStatus(null);
					}
					vm.waitingForHost = false;
				});
			}
			
			function _setCategories(categories) {
				vm.categories = categories;
				categories.forEach(function (category) {
					gameService.getActiveRoundCategoryQuestions(category.id).then(function(response) {
						var questions = response.data;
						
						// sort the list of questionKeys
						questions.sort(function(a, b){return a.value - b.value});
						category.questions = questions;
					});
				});
			}
			
			function _clearBuzzer(answerType) {
				/*
				if (vm.contestantStatus == PENDING_STATUS && _verifyContestant(answerPayload.contestant)) {
					var status
					if (answerPayload.answerType == CORRECT_ANSWER_TYPE) {
						status = ACTIVE_STATUS;
					}
					else {
						status = null;
					}
					_setContestantStatus(status);
				}
				*/
				// default to clearing the status
				var status = null;
				if (answerType == CORRECT_ANSWER_TYPE && vm.contestantStatus == RECOGNIZED_STATUS) {
					status == ACTIVE_STATUS;
				}
				console.log('our buzzer is clearing...');
				_setContestantStatus(status);
				_setHasBuzzedIn(false);
				_setActiveQuestionAnswer(answerType);
				_setActiveQuestion(null);
			}
			
			vm.buzzIn = function() {
				if (vm.activeQuestion && !vm.hasBuzzedIn) {
					_setHasBuzzedIn(true);
					contestantService.buzzIn().then(function (response) {
						var data = response.data;
						console.log('we have buzzed in...lets look at payload');
						console.log(data);
						if (data.first) {
							_setContestantStatus(RECOGNIZED_STATUS);
							console.log('vm.buzzIn is calling _handleHostResponse()');
							_handleHostResponse();
						}
						else {
							_setContestantStatus(PENDING_STATUS);
						}
					});
				}
			};
			
			vm.updateActiveQuestion = function() {
				var categoryId = vm.selectedCategory.id;
				var questionValue = vm.selectedQuestionValue;
				console.log('setting activeQuestion to...');
				console.log(categoryId);
				console.log(questionValue);
				
				gameService.makeQuestionActive(categoryId, questionValue).then(function(results) {
					_setActiveQuestion(results.data);
				}, function (error) {
					console.log(error);
				});
			};
			
		}];
		
		return {
			restrict: 'E',
			replace: true,
			templateUrl: 'resources/app/directives/contestantView/template.html',
			controller: controller,
			controllerAs: 'vm',
			scope: {},
			bindToController: true
		}
	}]);
})(angular.module('triviaApp'));