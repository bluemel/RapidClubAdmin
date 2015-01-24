angular.module('rcaUtils', [])

.constant('ensureArray', function(potentialArray){
	if (!potentialArray) {
		return [];
	}
	if (angular.isArray(potentialArray)) {
		return potentialArray;
	}
	return [ potentialArray ];
})

;
