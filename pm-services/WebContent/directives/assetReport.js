var assetReportApp = angular.module("assetReportApp", []).run(
		function($rootScope) {

		})

function getAlarmClass() {
	function Alarm(defaults) {
		defaults = defaults || {};
		this.alarmType = defaults.alarmType;
		this.alarmStatus = defaults.alarmStatus;
		this.date = defaults.date;
		this.flow = defaults.flow;
		this.suctionPressure = defaults.suctionPressure;
		this.dischargePressure = defaults.dischargePressure;
		this.motorInput = defaults.motorInput;
	}
	return Alarm;
};

assetReportApp.value("baseUri", "services");
assetReportApp.factory("Alarm", getAlarmClass);
assetReportApp.factory("alarmService", function($http, $q, baseUri,Alarm) {
	return {
		getAlarms : function(assetId) {
			var deferred = $q.defer();
			var promise = $http.get(baseUri + "/alarms/getAlarms/"+assetId);
			promise.then(function(response) {
				var result = response.data.map(function(alarm) {
					return new Alarm(alarm);
				});
				deferred.resolve(result);
			});
			return deferred.promise;
		},
		getAlarmsForDuration : function(assetId, begin, end) {
			var deferred = $q.defer();
			var promise = $http.get(baseUri + "/alarms/getAlarms/"+assetId+"/"+begin+"/"+end);
			promise.then(function(response) {
				var result = response.data.map(function(alarm) {
					return new Alarm(alarm);
				});
				deferred.resolve(result);
			});
			return deferred.promise;
		},
		getAssetsIDS : function() {
			var deferred = $q.defer();
			var promise = $http.get(baseUri + "/modelTraining/getAssetsIds");
			promise.then(function(response) {
				deferred.resolve(response.data);
			});
			return deferred.promise;
		},
	}
});
assetReportApp.directive("assetReport", function() {
	return {
		restrict : 'EA',
		scope : {},
		templateUrl : 'report.html',
		controller : function($scope, $element, alarmService) {
			$scope.assetsIds = [];
			$scope.alarms = [];
			$scope.alarmTableUrl = '';
			$scope.from='';
			$scope.to='';
			$scope.beginTimeStamp = 0;
			$scope.endTimeStamp = 0;
			alarmService.getAssetsIDS().then(function(result) {
				$scope.assetsIds = result;
			});
			
			$scope.clearAlarms = function(){
				$scope.alarmTableUrl = '';
				$scope.alarms = [];
				$scope.from='';
				$scope.to='';
			}
			
			$scope.getAlarmsForDuration = function(){
				$scope.fromDate = new Date($scope.from).getTime();
				$scope.toDate = new Date($scope.to).getTime();
				$scope.alarmTableUrl = '';
				$scope.beginTimeStamp = new Date($scope.from).getTime();
				$scope.endTimeStamp = new Date($scope.to).getTime();
				alarmService.getAlarmsForDuration($scope.assetId,$scope.beginTimeStamp,$scope.endTimeStamp).then(function (result) {
					$scope.alarmTableUrl = 'alarm-table.html';
	                 $scope.alarms = result;
	             });
			}
		}

	};
});