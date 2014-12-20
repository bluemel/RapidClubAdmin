// compare two
function compareTrainingsAccordingToDateAndTime(t1, t2) {
  if (t1.date - t2.date != 0) {
    return t1.training.date - t2.training.date;
  }
  return t1.trainingdate.timestart - t2.trainingdate.timestart;
}

angular.module('RapidClubAdminWebClient', [])
  .factory('TrainingSelector', function() {
    var selectedTraining;
    return {
      getSelectedTraining: function() {
        return selectedTraining;
      },
      setSelectedTraining: function(training) {
        selectedTraining = training;
      },
      init: function(trainings) {
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
  .controller('TrainingsListCtrl', function($scope, $http, TrainingSelector) {
    $http.get('trainingslist.json').then(function(trainingslistJsonRoot) {
      $scope.trainingSelector = TrainingSelector;
      $scope.trainingslist = trainingslistJsonRoot;
      $scope.trainingsForTable = [];
      // build an array of flat Trainings objects combined with their parent Trainingdates
      for (i = 0; i < trainingslistJsonRoot.data.bean.club.department.trainingdate.length; i++) {
        trainingdate = trainingslistJsonRoot.data.bean.club.department.trainingdate[i];
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
            // the Training's date in German date format
            "datestr": date.slice(6, 8) + "." + date.slice(4, 6) + "." + date.slice(0, 4)
          };
        }
        $scope.trainingsForTable = $scope.trainingsForTable.concat(trainingsOfTd);
      }
      // sort the trainings array according to training.date and trainingdate.timestart
      $scope.trainingsForTable = $scope.trainingsForTable.sort(compareTrainingsAccordingToDateAndTime);
      $scope.trainingSelector = TrainingSelector;
    }
    );
  })
;
