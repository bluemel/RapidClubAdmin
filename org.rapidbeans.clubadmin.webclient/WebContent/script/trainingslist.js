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
						id: training.id,
						name : trainingdate.name,
						date: training.date,
						timestart: trainingdate.timestart,
						dayofweek : trainingdate.dayofweek,
						state: training.state,
						location: trainingdate.location,
						partipiciantscount : training.partipiciantscount,
						notes : training.notes,
						checkedByUser: training.checkedByUser,
						checkedDate : training.checkedDate,
						heldbytrainer: ensureArray(training.heldbytrainer),
						sortKey : training.date + '-' + trainingdate.timestart
					});
				}
			}
			
			trainings.sort(function(a, b) { 
			    return a.sortKey < b.sortKey ? -1 : 1;
			});
		}
    };
})
  
.controller('trainingsListCtrl', function($scope, $http, $rcaTrainingsData, $timeout) {
	$scope.availableDepartments = [];
	if ($scope.isSuperAdmin()) {
		$scope.availableDepartments = ['Aikido', 'Chanbara', 'Grundschule', 'Haidong Gumdo', 'Judo', 'Tang Soo Do'];
	} else {
		for (var i = 0; i < $scope.user.departments.length; ++i) {
			$scope.availableDepartments.push($scope.user.departments[i].split('/')[1]);
		}
	}
	
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
    
    // Search the first training not yet closed, cancelled or checked.
    // If id is provided, pick training with given id of possible.
    var resetSelectedTraining = function(id) {
    	var bestTraining = null;
		for ( var i = 0; i < $scope.trainings.length; i++) {
			var training = $scope.trainings[i];
			if (id && training.id == id) {
				bestTraining = training;
				break;
			}
			if (!isCompleted(training)
					&& (!bestTraining || training.sortKey < bestTraining.sortKey)) {
				bestTraining = training;
			}
		}
		$scope.setSelectedTraining (bestTraining);
    };

    $scope.loadTrainingsList = function (department, id) {
    	// file URL for local test
        // $http.get('data/trainingslist' + department + '.json').success(function(data) {
        $http.get('server.php?action=getlist&department=' + department).success(function(data) {
        	 $scope.selectedDepartment = department;
        	 $rcaTrainingsData.setData(data);
        	 $scope.trainings = $rcaTrainingsData.getTrainings();
        	 
        	 $scope.allTrainerIds = $rcaTrainingsData.getAllTrainerIds();
        	 $scope.trainerRoles = $rcaTrainingsData.getTrainerRoles();
        	 
        	 resetSelectedTraining (id);
        });
    };
    $scope.loadTrainingsList($scope.availableDepartments[0]);

    $scope.setSelectedTraining = function(training) {
    	$scope.selectedTraining = training;
    	console.log('set training to ' + training.id);
    	$scope.mayReopen = false;
    	if (!isCompleted (training)) {
    		$scope.editableTraining = angular.copy (training);
    	} else {
    		$scope.editableTraining = null;
    		if (training.state !== 'closed' && $scope.isDepartmentAdmin()) {
    			$scope.mayReopen = true;
    		}
    	}
    	
    	$timeout(function(){
			var container = $('#trainingslist-selector');
		    var containerHeight = container.height();

		    var selected = $('.selected-training');
		    var selectedTop = selected.position().top;
		    var selectedBottom = selectedTop + selected.height();

		    if (selectedTop < 0) {
		    	container.scrollTop(Math.max(0, container.scrollTop() + selectedTop - 20));		    	
		    } else if (selectedBottom > containerHeight) {
		    	container.scrollTop(container.scrollTop() + selectedBottom - containerHeight + 20);
		    }
		});
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
    
    var updateTraining = function (changedValues) {
    	var newTraining = angular.extend ({}, $scope.selectedTraining, $scope.editableTraining, changedValues);

    	var department = $scope.selectedDepartment;
		$http.post('server.php?action=updatetraining&department=' + department, newTraining)
			.success(function() {
	        	$scope.loadTrainingsList(department, newTraining.id);
	        });
    };
    
    $scope.confirmTraining = function () {
    	if ($scope.editableTraining.heldbytrainer.length == 0) {
    		alert ('Bitte mindestens einen Trainer eintragen!');
    		return;
    	}
    	
    	if (!$scope.editableTraining.partipiciantscount) {
    		alert ('Bitte Anzahl der Teilnehmer eintragen!');
    		return;
    	}
    	
    	updateTraining({ state: 'checked' });
    };
    
    $scope.cancelTraining = function () {
    	updateTraining({ heldbytrainer: [], state: 'cancelled', partipiciantscount: 0 });
    };
    
    $scope.reopenTraining = function () {
    	updateTraining({ state: 'modified' });
    };
    
    var selectNextTraining = function (offset) {
    	for (var i = 0; i < $scope.trainings.length; ++i) {
    		if ($scope.trainings[i] === $scope.selectedTraining) {
    			if (i + offset >= 0 && i + offset < $scope.trainings.length) {
    				$scope.setSelectedTraining ($scope.trainings[i + offset]);
    				$scope.$apply();
    			}
    			return;
    		}
    	}
    };
    
    var keyHandler = function(event){
    	if ($(event.target).is('textarea')) {
    		return;
    	}
    	
    	if (event.keyCode == 38 /* up */) {
    		selectNextTraining(-1);
    		event.preventDefault();
    	} else if (event.keyCode == 40 /* down */) {
    		selectNextTraining(+1);
    		event.preventDefault();
    	}
    };
    $(window).keydown(keyHandler);
    $scope.$on('$destroy', function() {
    	$window.unbind('keydown', keyHandler);
    });
})
  
;
