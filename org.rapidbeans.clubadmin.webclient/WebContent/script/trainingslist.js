angular.module('rcaTrainingsList', ['rcaFilters', 'rcaUtils'])

.filter('resolveTrainer', function($filter, $rcaTrainingsData){
	return function(id) {
		var resolve = $filter('resolveUserByMap');
		return resolve(id, $rcaTrainingsData.getTrainersMap());
	};
})

.filter('resolveUser', function($filter, $rcaTrainingsData){
	return function(id) {
		var resolve = $filter('resolveUserByMap');
		return resolve(id, $rcaTrainingsData.getUsersMap());
	};
})

.factory('$rcaTrainingsData', function(ensureArray) {
	var usersById = {};
	var trainersById = {};
	var trainings = [];
	
	var buildUserMap = function(users) {
		var result = {};
		users = ensureArray(users);
		for (var i = 0; users && i < users.length; i++) {
			result[users[i].id] = users[i];
		}
		return result;
	};
	
    return {
    	getUsersMap: function () {
    		return usersById;
    	},
    	
    	getTrainersMap: function () {
    		return trainersById;
    	},
    	
		getTrainings: function() {
			return trainings;
		},

		setData : function(dataRoot) {
			usersById = buildUserMap(dataRoot.user);
			trainersById = buildUserMap(dataRoot.trainer);

			trainings = [];

			var trainingdates = ensureArray(dataRoot.club.department.trainingdate);
			for ( var i = 0; i < trainingdates.length; i++) {
				var trainingdate = trainingdates[i];
				for ( var j = 0; j < trainingdate.training.length; j++) {
					var training = trainingdate.training[j];
					training.heldbytrainer = ensureArray(training.heldbytrainer);
					
					trainings.push({
						training : training,
						trainingdate : trainingdate,
						sortKey : training.date + '-' + trainingdate.timestart
					});
				}
			}
		}
    };
})
  
.controller('trainingsListCtrl', function($scope, $http, $rcaTrainingsData) {

	// TODO (BH): Limit depending on user.
	$scope.availableDepartments = ['Aikido', 'Chanbara', 'Grundschule', 'Haidong Gumdo', 'Judo', 'Tang Soo Do'];

    $scope.trainings = [];
    $scope.selectedTraining = {};
    
    // search the first training not yet closed, cancelled or checked
    var resetSelectedTraining = function() {
    	var bestTraining = null;
    	var ignoreStates = {
    		closed: 1,
    		cancelled : 1,
    		checked : 1
    	};
		for ( var i = 0; i < $scope.trainings.length; i++) {
			var training = $scope.trainings[i];
			if (!ignoreStates[training.training.state]
					&& (!bestTraining || training.sortKey < bestTraining.sortKey)) {
				bestTraining = training;
			}
		}
        $scope.selectedTraining = bestTraining;
    };

    $scope.loadTrainingsList = function (department) {
    	// file URL for local test
        // $http.get('data/trainingslist' + department + '.json').success(function(data) {
        $http.get('server.php?action=getlist&department=' + department).success(function(data) {
        	 $scope.selectedDepartment = department;
        	 $rcaTrainingsData.setData(data);
        	 $scope.trainings = $rcaTrainingsData.getTrainings();
        	 resetSelectedTraining ();
        });
    };
    
    $scope.loadTrainingsList($scope.availableDepartments[0]);
    
    $scope.setSelectedTraining = function(training) {
    	$scope.selectedTraining = training;
    };
})
  
;
