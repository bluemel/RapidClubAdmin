angular.module('rcaTrainingsList', [])

.filter('shortDayOfWeek', function(){
	return function(dayofweek) {
		return {
			monday : 'MO',
			tuesday : 'DI',
			wednesday : 'MI',
			thursday : 'DO',
			friday : 'FR',
			saturday : 'SA',
			sunday : 'SO'
		}[dayofweek] || dayofweek;
	};
})

.filter('stateImageName', function(){
	return function(state) {
		return {
			asplanned : 'training0Default.png',
			modified : 'training1InWork.png',
			checked : 'training2Checked.png',
			cancelled : 'training3Cancelled.png',
			closed : 'training4Closed.png'
		}[state] || 'unknown.png';
	};
})

.filter('stateShortDescription', function(){
	return function(state) {
		return {
			asplanned : 'Betreuung gemäß Planung',
			modified : 'Betreuung geändert',
			checked : 'Betreuung bestätigt',
			cancelled : 'Training abgesagt',
			closed : 'Trainingsort geschlossen'
		}[state];
	};
})

.filter('formatDate', function(){
	return function(date) {
		if (!date) {
			return '';
		}
		return date.slice(6, 8) + "." + date.slice(4, 6) + "."
			+ date.slice(0, 4);
	};
})

.filter('formatDateTime', function($filter){
	return function(dateTime) {
		if (!dateTime) {
			return '';
		}
		var dateFilter = $filter('formatDate');
		return dateFilter(dateTime) + " "
				+ dateTime.slice(8, 10) + ":"
				+ dateTime.slice(10, 12);
	};
})

// TODO (BH): Maybe delete?
.service("Comparators", function() {
    // compare two training table entries
    this.compareTrainingsAccordingToDateAndTime = function (t1, t2) {
        return t1.training.date - t2.training.date || 
        	t1.trainingdate.timestart - t2.trainingdate.timestart;
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

  .factory('UserModel', function() {
    var userMap = new Map();
    return {
      findById: function(id) {
        return userMap.get(id);
      },
      getFullName: function(id) {
        if (!id) {
          return '';
        }
        user = userMap.get(id);
        if (!user) {
          return 'ERROR: unknown user "' + id + '"';
        } else {
          return user.lastname + ", " + user.firstname;
        }
      },
      setData: function(users) {
    	userMap.clear();
        if (!users) {
          return;
        }
        if (angular.isArray(users)) {
          var i;
          for (i = 0; i < users.length; i++) {
            userMap.set(users[i].id, users[i]);
          }
        } else {
          userMap.set(users.id, users);
        }
      }
    };
  })

  .factory('TrainerModel', function() {
    var trainerMap = new Map();
    return {
      findById: function(id) {
        return trainerMap.get(id);
      },
      getFullName: function(id) {
        if (!id) {
          return '';
        }
        trainer = trainerMap.get(id);
        if (!trainer) {
          return 'ERROR: unknown trainer "' + id + '"';
        } else {
          return trainer.lastname + ", " + trainer.firstname;
        }
      },
      setData: function(trainers) {
      	trainerMap.clear();
        if (!trainers) {
          return;
        }
        if (angular.isArray(trainers)) {
          var i;
          for (i = 0; i < trainers.length; i++) {
            trainerMap.set(trainers[i].id, trainers[i]);
          }
        } else {
          trainerMap.set(trainers.id, trainers);
        }
      }
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

.controller('trainingsListCtrl', function($scope, $http, $timeout, TrainingSelector, TrainingslistModel, UserModel, TrainerModel, Comparators) {

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
	          $scope.userModel.setData(data.user);
	          $scope.trainerModel.setData(data.trainer);
	          // no effect here so we use
	          // ng-init="trainingSelector.setData(trainingslistModel.getTrainingslist())"
	          // in HTML code
	          // $scope.trainingSelector.setData(trainingslistModel.getTrainingslist());
        });
    };
    $scope.loadTrainingslist($scope.availableDepartments[0]);
    

    $scope.trainingSelector = TrainingSelector;
    
    $scope.trainingslistModel = TrainingslistModel;
    $scope.userModel = UserModel;
    $scope.trainerModel = TrainerModel;
})
  
;
