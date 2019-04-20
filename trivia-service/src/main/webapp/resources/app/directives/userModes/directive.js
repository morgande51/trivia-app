/**
 * Define the user modes directive
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	var CONTESTANT_MODE_INFO = 'Participate in the Triva Game as a contestant.  You will have access to a buzzer, and the ability to select new questions from the available categories.';
	var HOST_MODE_INFO = 'Participate in the Triva Game as the host.  You will moderate the game, have the ability to recognize the active contestant and their answers.';
	var ADMIN_MODE_INFO = 'Admin Users Only!';
	var MODE_INFO_MAP = {
		contestant: CONTESTANT_MODE_INFO,
		host: HOST_MODE_INFO,
		admin: ADMIN_MODE_INFO
	};
	
	var LETS_PLAY_BTN_TXT = "Let's Play!";
	var LETS_GO_BNT_TXT = "Let's Go!";
	var CONTINUE_BTN_TXT = 'Continue';
	var BTN_TXT_MAP = {
		contestant: LETS_PLAY_BTN_TXT,
		host: LETS_GO_BNT_TXT,
		admin: CONTINUE_BTN_TXT
	};
	
	var SUCCESS_BTN_TYPE = 'btn-success';
	var PRIMARY_BTN_TYPE = 'btn-primary';
	var DEFAULT_BTN_TYPE = 'btn-default';
	var BTN_TYPE_MAP = {
		contestant: SUCCESS_BTN_TYPE,
		host: PRIMARY_BTN_TYPE,
		admin: DEFAULT_BTN_TYPE
	};
	
	triviaApp.directive('userModes', [function() {
		
		var controller = ['$rootScope', function($rootScope) {
			var vm = this;
			
			function init() {
				vm.modes = [];
				
				$rootScope.$watch('authenticatedUser', function (user) {
					if (user) {
						if (user.userRoles.length == 1) {
							_setSelectedMode(user.userRoles[0]);
						}
						else {
							for (var i in user.userRoles) {
								var name = user.userRoles[i];
								var info = MODE_INFO_MAP[name];
								var btnTxt = BTN_TXT_MAP[name];
								var btnType = BTN_TYPE_MAP[name];
								var mode = {
									'name': name,
									'info': info,
									'btnTxt': btnTxt,
									'btnType': btnType
								};
								vm.modes.push(mode);
							}
						}
					}
				});				
			};
			init();
			
			function _setSelectedMode(modeName) {
				$rootScope.selectedMode = modeName;
				console.log($rootScope.selectedMode);
			}
			
			vm.setSelectedMode = function(mode) {
				_setSelectedMode(mode.name);
			};
		}];
		
		return {
			restrict: 'E',
			replace: true,
			templateUrl: 'resources/app/directives/userModes/template.html',
			controller: controller,
			controllerAs: 'vm',
			bindToController: true
		}
	}]);
})(angular.module('triviaApp'));