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
			alarmService.getAssetsIDS().then(function(result) {
				$scope.assetsIds = result;
			});
			$scope.getAlarms = function() {
				if ($scope.assetId === null) {
					$scope.alarms = [];
					$scope.alarmTableUrl = '';
					return;
				}
				alarmService.getAlarms($scope.assetId).then(function (result) {
					$scope.alarmTableUrl = 'alarm-table.html';
	                 console.log("all data ", result);
	                 $scope.alarms = result;
	                 setTimeout(() => {
	                 	$element.find('#dataTables-example').DataTable({
	                 		responsive: true
	                 	});
	 				}, 100);
	             });
			}
		}

	};
});