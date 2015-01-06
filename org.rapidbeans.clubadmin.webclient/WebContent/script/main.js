angular.module('RapidClubAdminWebClient', [ 'rcaTrainingsList', 'rcaLogin' ])

.config(function($httpProvider) {

	$httpProvider.interceptors.push(function($q) {
		return {
			responseError : function(rejection) {
				// TODO (BH): Make error handling nicer
				alert('Error ' + rejection.status + ' ('
						+ rejection.statusText + '): '
						+ rejection.data);
				return $q.reject(rejection);
			}
		};
	});

});
