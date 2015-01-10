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
	var trainerRoles = [];
	
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
		
		getTrainerRoles: function () {
			return trainerRoles;
		},
		
		getAllTrainerIds: function () {
			var ids = [];
			angular.forEach (trainersById, function(value, key) {
				ids.push(key);
			});
			return ids;
		},

		setData : function(dataRoot) {
			usersById = buildUserMap(dataRoot.user);
			trainersById = buildUserMap(dataRoot.trainer);

			trainerRoles = [];
			for (var i = 0; i < dataRoot.trainerrole.length; ++i) {
				trainerRoles.push (dataRoot.trainerrole[i].id);
			}
			
			trainings = [];

			var trainingdates = ensureArray(dataRoot.club.department.trainingdate);
			for ( var i = 0; i < trainingdates.length; i++) {
				var trainingdate = trainingdates[i];
				for ( var j = 0; j < trainingdate.training.length; j++) {
					var training = trainingdate.training[j];
					trainings.push({
						name : trainingdate.name,
						date: training.date,
						timestart: trainingdate.timestart,
						dayofweek : trainingdate.dayofweek,
						state: training.state,
						location: trainingdate.location,
						participantscount : training.participantscount,
						checkedByUser: training.checkedByUser,
						checkedDate : training.checkedDate,
						heldbytrainer: ensureArray(training.heldbytrainer),
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
    $scope.allTrainerIds = [];
    $scope.trainerRoles = [];
    $scope.selectedTraining = {};
    
    var isCompleted = function (training) {
    	var completeStates = {
        		closed: 1,
        		cancelled : 1,
        		checked : 1
        	};
    	return !!completeStates[training.state];
    };
    
    // search the first training not yet closed, cancelled or checked
    var resetSelectedTraining = function() {
    	var bestTraining = null;
		for ( var i = 0; i < $scope.trainings.length; i++) {
			var training = $scope.trainings[i];
			if (!isCompleted(training)
					&& (!bestTraining || training.sortKey < bestTraining.sortKey)) {
				bestTraining = training;
			}
		}
		$scope.setSelectedTraining (bestTraining);
    };

    $scope.loadTrainingsList = function (department) {
    	// file URL for local test
        // $http.get('data/trainingslist' + department + '.json').success(function(data) {
        $http.get('server.php?action=getlist&department=' + department).success(function(data) {
        	 $scope.selectedDepartment = department;
        	 $rcaTrainingsData.setData(data);
        	 $scope.trainings = $rcaTrainingsData.getTrainings();
        	 
        	 $scope.allTrainerIds = $rcaTrainingsData.getAllTrainerIds();
        	 $scope.trainerRoles = $rcaTrainingsData.getTrainerRoles();
        	 
        	 resetSelectedTraining ();
        });
    };
    
    $scope.loadTrainingsList($scope.availableDepartments[0]);
    
    var mayEditTraining = function (training) {
    	// TODO(BH): respect permissions of the user here
    	return !isCompleted (training);
    };
    
    $scope.setSelectedTraining = function(training) {
    	$scope.selectedTraining = training;
    	if (mayEditTraining (training)) {
    		$scope.editableTraining = angular.copy (training);
    	} else {
    		$scope.editableTraining = null;
    	}
    };
    
    $scope.addTrainer = function () {
    	if ($scope.editableTraining) {
    		$scope.editableTraining.heldbytrainer.push({
    			role: $scope.trainerRoles[0],
    			trainer: $scope.allTrainerIds[0]
    		});
    	}
    };
    
    $scope.removeTrainer = function (trainer) {
    	if ($scope.editableTraining) {
    		var newHeldBy = [];
    		angular.forEach($scope.editableTraining.heldbytrainer, function(value) {
    			if (value !== trainer) {
    				newHeldBy.push(value);
    			}
    		});
    		$scope.editableTraining.heldbytrainer = newHeldBy;
    	}
    };
})
  
;
