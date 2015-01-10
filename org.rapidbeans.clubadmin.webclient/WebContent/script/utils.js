angular.module('rcaUtils', [])

.constant('ensureArray', function(potentialArray){
	if (!potentialArray) {
		return null;
	}
	if (angular.isArray(potentialArray)) {
		return potentialArray;
	}
	return [ potentialArray ];
})

;
