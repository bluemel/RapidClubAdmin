angular.module('rcaTrainingsList', ['rcaFilters'])

// TODO (BH): Maybe delete?
.service("Comparators", function() {
    // compare two training table entries
    this.compareTrainingsAccordingToDateAndTime = function (t1, t2) {
        return t1.training.date - t2.training.date || 
        	t1.trainingdate.timestart - t2.trainingdate.timestart;
    };
})

.filter('resolveTrainer', function($filter, $rcaTrainers){
	return function(id) {
		var resolve = $filter('resolveUserByMap');
		return resolve(id, $rcaTrainers.getMap());
	};
})

.filter('resolveUser', function($filter, $rcaUsers){
	return function(id) {
		var resolve = $filter('resolveUserByMap');
		return resolve(id, $rcaUsers.getMap());
	};
})

.factory('$rcaUsers', function() {
	var usersById = {};
	return {
		getMap : function() {
			return usersById;
		},
		setData : function(users) {
			usersById = {};
			if (!users) {
				return;
			}
			if (angular.isArray(users)) {
				for (var i = 0; i < users.length; i++) {
					usersById[users[i].id] = users[i];
				}
			} else {
				usersById[users.id] = users;
			}
		}
	};
})

.factory('$rcaTrainers', function() {
	var trainersById = {};
	return {
		getMap : function() {
			return trainersById;
		},
		setData : function(trainers) {
			trainersById = {};
			if (!trainers) {
				return;
			}
			if (angular.isArray(trainers)) {
				for (var i = 0; i < trainers.length; i++) {
					trainersById[trainers[i].id] = trainers[i];
				}
			} else {
				trainersById[trainers.id] = trainers;
			}
		}
	};
})

.factory('TrainingSelector', function() {
    var selectedTraining = null;
    return {
      getSelectedTraining: function() {
        return selectedTraining;
      },
      getSelectedTrainingHeldbytrainers: function() {
        heldbyarray = [];
        if (angular.isArray(selectedTraining.training.heldbytrainer)) {
          heldbyarray = selectedTraining.training.heldbytrainer;
        } else {
          heldbyarray.push(selectedTraining.training.heldbytrainer);
        }
        return heldbyarray;
      },
      setSelectedTraining: function(training) {
        selectedTraining = training;
      },
      setData: function(trainings) {
        // search the first training not yet closed, cancelled or checked
        selectedTraining = null;
        for (var i = 0; i < trainings.length; i++) {
          if (trainings[i].training.state != 'closed'
            && trainings[i].training.state != 'cancelled'
            && trainings[i].training.state != 'checked') {
            selectedTraining = trainings[i];
            break;
          }
        }
      },
    };
  })
  
  .factory('TrainingslistModel', function(Comparators) {
    var root;
    var trainingslistArray = [];
    return {
      getRoot: function() {
        return root();
      },
      getTrainingslist: function() {
        return trainingslistArray;
      },
      setData: function(dataRoot) {
        // build an array of flat trainings objects combined with their parent Trainingdates
        trainingslistArray = [];
        if (angular.isArray(dataRoot.club.department.trainingdate)) {
          var i;
          for (i = 0; i < dataRoot.club.department.trainingdate.length; i++) {
            trainingslistArray = trainingslistArray.concat(this.readTrainingsOfTd(dataRoot.club.department.trainingdate[i]));
          }
        } else {
        	trainingslistArray = trainingslistArray.concat(this.readTrainingsOfTd(dataRoot.club.department.trainingdate));
        }
        // sort the trainings array according to training.date and trainingdate.timestart
        trainingslistArray = trainingslistArray.sort(Comparators.compareTrainingsAccordingToDateAndTime);
        for (i = 0; i < trainingslistArray.length; i++) {
          trainingslistArray[i].index = i;
        }
      },
      readTrainingsOfTd: function(trainingdate) {
        trainingsOfTd = new Array(trainingdate.training.length);
        var j;
        for (j = 0; j < trainingdate.training.length; j++) {
          trainingsOfTd[j] = {
            // the Training to render
            "training": trainingdate.training[j],
            // the parent Trainingdate
            "trainingdate": trainingdate,
            "index": -1
          };
        }
        return trainingsOfTd;
      }
    };
  })

.controller('trainingsListCtrl', function($scope, $http, $timeout, TrainingSelector, TrainingslistModel, $rcaUsers, $rcaTrainers, Comparators) {

	// TODO (BH): Limit depending on user.
	$scope.availableDepartments = ['Aikido', 'Chanbara', 'Grundschule', 'Haidong Gumdo', 'Judo', 'Tang Soo Do'];
	
    $scope.loadTrainingslist = function (department) {
      // example URLfor browser test
      // $http.get('fileio.php?password=musashi09&file=current/Haidong%20Gumdo/trainingslist.xml&op=readj').then(function(httpResponse) {
      // file URL for local test
//      $http.get('data/trainingslist' + department + '.json').success(function(data) {
        $http.get('server.php?action=getlist&department=' + department).success(function(data) {
        	 $scope.selectedDepartment = department;
	          $scope.trainingslistModel.setData(data);
	          $rcaUsers.setData(data.user);
	          $rcaTrainers.setData(data.trainer);
	          // no effect here so we use
	          // ng-init="trainingSelector.setData(trainingslistModel.getTrainingslist())"
	          // in HTML code
	          // $scope.trainingSelector.setData(trainingslistModel.getTrainingslist());
        });
    };
    $scope.loadTrainingslist($scope.availableDepartments[0]);
    $scope.trainingSelector = TrainingSelector;
    $scope.trainingslistModel = TrainingslistModel;
})
  
;
