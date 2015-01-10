angular.module('rcaFilters', [])

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

.filter('resolveUserByMap', function(){
	return function(id, map) {
		if (!id) {
			return '';
		}
		if (!map || !map[id]) {
			return 'User with id ' +  id;
		}
		var user = map[id];
		return user.lastname + ", " + user.firstname;
	};
})

;