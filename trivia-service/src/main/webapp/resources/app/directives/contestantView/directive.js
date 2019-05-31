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
	
	var APPLICATION_RESET = 'APPLICATION_RESET';
	
	triviaApp.directive('contestantView', [function() {
		
		var controller = ['$scope', '$rootScope', 'contestantService', 'gameService', 'notificationService', function ($scope, $rootScope, contestantService, gameService, notificationService) {
			var vm = this;

			function init() {
				_cleanup();
				
				// get the current status
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
				}, function(error) {
					console.log('No known active game');
				});
				
				// handle new activeRound
				$scope.$on(notificationService.getActiveRoundEventType(), function (event, round) {
					console.log('$scope.$on(ACTIVE_ROUND_EVENT)');
					_loadActiveRoundCategories();
					vm.waitingForHost = false;
				});
				
				// handle question selected notification
				$scope.$on(notificationService.getActiveQuestionEventType(), function(event, question) {
					console.log('$scope.$on(ACTIVE_QUESTION_EVENT)');
					_setActiveQuestion(question);
					vm.waitingForHost = false;
				});
				
				// handle question clear notification
				$scope.$on(notificationService.getActiveQuestionClearEventType(), function (event) {
					console.log('$scope.$on(ACTIVE_QUESTION_CLEAR_EVENT)');
					_setHasBuzzedIn(false);
					
					var status;
					switch (vm.contestantStatus) {
						case ACTIVE_STATUS:
						case RECOGNIZED_STATUS:
							status = ACTIVE_STATUS;
							break;
							
						default:
							status = null;
					}
					_setContestantStatus(status);
						
					_setActiveQuestionAnswer(null);
					console.log('$scope.$on(ACTIVE_QUESTION_CLEAR_EVENT) is calling _setAcitveQuestion to null');
					_setActiveQuestion(null);
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
							console.log('...waitingForHost value changed from ' + ov + ' to ' + nv);
							if (!nv) {
								console.log('...we should now have a value from the host, lets clear the buzzer');
								_clearBuzzer(answerPayload.answerType);
								clearWatcher();
								vm.waitingForHost = false;
							}
						});
					}
					else {
						console.log('Is the race condition happening here???');
						_clearBuzzer(answerPayload.answerType);
					}
				});
				
				// handle end activeRound
				$scope.$on(notificationService.getRoundEndEventType(), function (event) {
					console.log('$scope.$on(ROUND_END_EVENT)');
					_cleanup();
				});
				
				// handle active notification
				$scope.$on(notificationService.getBuzzerActiveEventType(), function(event, activeContestant) {
					var status;
					console.log('$scope.$on(BUZZER_ACTIVE_EVENT)');
					console.log('active contestant: ' + activeContestant.fullName);
					console.log('current status: ' + vm.contestantStatus);
					var listenForAnswer = (vm.contestantStatus == PENDING_STATUS);
					if (!listenForAnswer && vm.contestantStatus != INCORRECT_ANSWER_STATUS) {
						if (_verifyContestant(activeContestant)) {
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
					}
					else if (listenForAnswer){
						// we might need to listen for the answer if we are pending
						console.log('$scope.$on(BUZZER_ACTIVE_EVENT) is calling _handleHostResponse()');
						_setContestantStatus(RECOGNIZED_STATUS);
						_handleHostResponse();
					}
				});
			}
			
			init();
			$scope.$on(APPLICATION_RESET, function (event) {
				console.log('we are resetting the application!');
				init();
			});
			
			function _loadActiveRoundCategories() {
				gameService.getActiveRoundCategories().then(function (results) {
					var categories = results.data;
					_setCategories(categories);
				});
			}
			
			function _setContestantStatus(status) {
				console.log('status being set to: ' + status);
				vm.contestantStatus = status;
			}
			
			function _setActiveQuestion(question) {
				console.log('the activeQuestion is being set to: ' + question);
				vm.activeQuestion = question;
				vm.selectedCategory = null;
				vm.selectedQuestionValue = null;
			}
			
			function _setActiveQuestionAnswer(answer) {
				console.log('setting the active question answer to: ' + answer);
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
						_setActiveQuestionAnswer(answer.answerType);
						switch (answer.answerType) {
							case CORRECT_ANSWER_TYPE:
								_setContestantStatus(ACTIVE_STATUS);
								break;
								
							case INCORRECT_ANSWER_TYPE:
								_setContestantStatus(INCORRECT_ANSWER_STATUS);
								break;
								
							default:
								var errorMsg = 'This is an error condition, answer must be correct or inncorect'; 
								console.log(errorMsg);
								throw errorMsg;
								
						}
					}
					else {
						console.log('contestant buzzed in, but another contestant answered correctly');
						_setContestantStatus(null);
					}
					vm.waitingForHost = false;
				}, function (error) {
					console.log('An error occured while getting the host answer');
					console.log(error);
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
				console.log('our buzzer is clearing..');
				if (vm.contestantStatus != ACTIVE_STATUS || answerType == NO_ANSWER_ANSWER_TYPE) {
					console.log('The current status[' + vm.contestantStatus + '] will be cleared');
					_setContestantStatus(null);
				}
				_setHasBuzzedIn(false);
				_setActiveQuestionAnswer(answerType);
				console.log('clearBuzzer() is setting activeQuestion to null');
				_setActiveQuestion(null);
			}
			
			function _cleanup() {
				// declar/clear initial state
				vm.contestantStatus = null;
				vm.hasBuzzedIn = false;
				vm.categories = null;
				vm.selectedCategory = null;
				vm.selectedQuestionValue = null;
				vm.waitingForHost = false;
				vm.activeQuestion = null;
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
					console.log('updateActiveQuestion is calling _setActiveQuestion');
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