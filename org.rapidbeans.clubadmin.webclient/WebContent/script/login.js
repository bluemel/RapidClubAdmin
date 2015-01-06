angular.module('rcaLogin', [])

.controller('loginBranchCtrl', function($http, $scope) {

	$scope.user = {};
	$scope.loginData = {};

	$scope.login = function() {
		$scope.error = false;
		$http({
			method : 'POST',
			url : 'server.php?action=login',
			data : $.param($scope.loginData),
			headers : {
				'Content-Type' : 'application/x-www-form-urlencoded'
			}
		}).success(function(user) {
			if (user && user.username) {
				$scope.user = user;
				$scope.loginTarget = 'trainingslist';
			} else {
				$scope.error = true;
			}
		});
		$scope.loginData.password = '';
	};

	$scope.logout = function() {
		$scope.user = {};
		$http.get('server.php?action=logout').success(function() {
			$scope.loginTarget = 'login';
		});
	};

	$http.get('server.php?action=getuser').success(function(user) {
		$scope.user = user;
		if ($scope.user.username) {
			$scope.loginTarget = 'trainingslist';
		} else {
			$scope.loginTarget = 'login';
		}
	});

});
