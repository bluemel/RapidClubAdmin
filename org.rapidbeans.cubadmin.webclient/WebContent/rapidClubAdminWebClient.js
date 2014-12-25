angular.module('RapidClubAdminWebClient', [])

.service("Helpers", function() {
    // map a dayofweek to its short representation
    this.dayOfWeekShort = function(dayofweek) {
        if (dayofweek == 'monday') {
          return 'MO';
        } else if (dayofweek == 'tuesday') {
          return 'DI';
        } else if (dayofweek == 'wednesday') {
          return 'MI';
        } else if (dayofweek == 'thursday') {
          return 'DO';
        } else if (dayofweek == 'friday') {
          return 'FR';
        }
      };
// asplanned training0Default.png
// modified training1InWork.png
// checked training2Checked.png
// cancelled training3Cancelled.png
// closed training4Closed.png
    this.stateToDescriptionShort = function(state) {
      if (state == 'asplanned') {
          return 'Betreuung gemäß Planung';
        } else if (state == 'modified') {
          return 'Betreuung geändert';
        } else if (state == 'checked') {
          return 'Betreuung bestätigt';
        } else if (state == 'cancelled') {
          return 'Training abgesagt';
        } else if (state == 'closed') {
          return 'Trainingsort geschlossen';
        }
      };
    this.formatDateGerman = function(date) {
      return date.slice(6, 8) + "." + date.slice(4, 6) + "." + date.slice(0, 4);
    };
    this.formatDateTimeGerman = function(dtime) {
      return dtime.slice(6, 8) + "." + dtime.slice(4, 6) + "." + dtime.slice(0, 4)
        + " " + dtime.slice(8, 10) + ":" + dtime.slice(10, 12);
    };
  })

.factory('TrainingSelector', function(Helpers) {
    var selectedTraining;
    return {
      getSelectedTraining: function() {
        return selectedTraining;
      },
      getSelectedTrainingHeldbytrainers: function() {
        heldbyarray = [];
        prtype = Object.prototype.toString.call(selectedTraining.training.heldbytrainer);
        if (prtype == '[object Array]') {
          heldbyarray = selectedTraining.training.heldbytrainer;
        } else {
          heldbyarray.push(selectedTraining.training.heldbytrainer);
        }
        return heldbyarray;
      },
      getSelectedTrainingTitle: function() {
        return Helpers.dayOfWeekShort(selectedTraining.trainingdate.dayofweek)
          + ", " + Helpers.formatDateGerman(selectedTraining.training.date)
          + ", " + selectedTraining.trainingdate.name
          + ", " + selectedTraining.trainingdate.location;
      },
      getSelectedTrainingDayOfWeekShort: function() {
        return Helpers.dayOfWeekShort(selectedTraining.trainingdate.dayofweek);
      },
      getSelectedTrainingState: function() {
        return Helpers.stateToDescriptionShort(selectedTraining.training.state);
      },
      setSelectedTraining: function(training) {
        selectedTraining = training;
      },
      init: function(trainings) {
        // search the first training not yet closed, cancelled or checked
        selectedTraining = trainings[1];
        for (i = 0; i < trainings.length; i++) {
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
    var map = new Map();
    return {
      findById: function(id) {
        return map.get(id);
      },
      getFullName: function(id) {
          return map.get(id).lastname + ", " + map.get(id).firstname;
        },
      init: function(users) {
        for (i = 0; i < users.length; i++) {
          map.set(users[i].id, users[i]);
        }
      }
    };
  })

  .factory('TrainerModel', function() {
    var map = new Map();
    return {
      findById: function(id) {
        return map.get(id);
      },
      getFullName: function(id) {
          return map.get(id).lastname + ", " + map.get(id).firstname;
        },
      init: function(trainers) {
        for (i = 0; i < trainers.length; i++) {
          map.set(trainers[i].id, trainers[i]);
        }
      }
    };
  })

  .service("Comparators", function() {
    // compare two training table entries
    this.compareTrainingsAccordingToDateAndTime = function (t1, t2) {
      if (t1.date - t2.date != 0) {
        return t1.training.date - t2.training.date;
      }
      return t1.trainingdate.timestart - t2.trainingdate.timestart;
    };
  })

// Example URL for trainer picture download
// http://trainer.budo-club-ismaning.de/rapidclubadmin/fileio.php?password=musashi09&file=trainerIcons/Russ_Kevin_.jpg&op=read

  .controller('TrainingsListCtrl', function($scope, $http, TrainingSelector, UserModel, TrainerModel, Comparators, Helpers) {
    $scope.trainingSelector = TrainingSelector;
    $scope.helpers = Helpers;
    $scope.userModel = UserModel;
    $scope.trainerModel = TrainerModel;
    // for local test
    $http.get('trainingslist2.json').then(function(httpResponse) {
    // $http.get('http://trainer.budo-club-ismaning.de/rapidclubadmin/fileio.php?password=musashi09&file=current/Aikido/trainingslist.xml&op=readj').then(function(httpResponse) {
      $scope.trainingslist = httpResponse;
      $scope.trainingsForTable = [];
      $scope.userModel.init(httpResponse.data.user);
      $scope.trainerModel.init(httpResponse.data.trainer);
      // build an array of flat trainings objects combined with their parent Trainingdates
      for (i = 0; i < httpResponse.data.club.department.trainingdate.length; i++) {
        trainingdate = httpResponse.data.club.department.trainingdate[i];
        trainingsOfTd = new Array(trainingdate.training.length);
        for (j = 0; j < trainingdate.training.length; j++) {
          training = trainingdate.training[j];
          // enforce a new String copied by value in order to apply a Javascript built-in function
          date = new String(training.date);
          trainingsOfTd[j] = {
            // the Training to render
            "training": training,
            // the parent Trainingdate
            "trainingdate": trainingdate,
          };
        }
        $scope.trainingsForTable = $scope.trainingsForTable.concat(trainingsOfTd);
      }
      // sort the trainings array according to training.date and trainingdate.timestart
      $scope.trainingsForTable = $scope.trainingsForTable.sort(Comparators.compareTrainingsAccordingToDateAndTime);
      $scope.trainingSelector = TrainingSelector;
    }
    );
  })
;
