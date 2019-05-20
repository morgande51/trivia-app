/**
 * Define the backspace menu directive
 * 
 * Author: Darnell Morgan
 */
(function(triviaApp) {
	'use strict';
	
	var APPLICATION_RESET = 'APPLICATION_RESET';
	
	triviaApp.directive('menu', [function() {
		
		var controller = ['authenticationService', '$rootScope', function(authenticationService, $rootScope) {
			var vm = this;
			
			vm.login = function() {
				var email = vm.email;
				var pwd = vm.pwd;
				authenticationService.authenticate(email, pwd).then(function(response) {
					var user = response.data;
					console.log(user);
					authenticationService.authorization().then(function (rolesResponse) {
						var roles = rolesResponse.data;
						console.log(roles);
						user.userRoles = roles;
						$rootScope.authenticatedUser = user;
						vm.authenticatedUser = user;
						vm.email = null;
						vm.pwd = null;
					});
				}, function(errorObj) {
					console.log(errorObj);
				});
			};
			
			vm.logout = function() {
				authenticationService.logout().then(function(response) {
					$rootScope.authenticatedUser = null;
				}, function(errorObj) {
					console.log(errorObj);
				});
			};
			
			vm.reset = function() {
				$rootScope.$broadcast(APPLICATION_RESET);
			};
		}];
		
		return {
			restrict: 'E',
			replace: true,
			templateUrl: 'resources/app/directives/menu/template.html',
			controller: controller,
			controllerAs: 'vm',
			scope: {},
			bindToController: true
		}
	}]);
	
})(angular.module('triviaApp'));