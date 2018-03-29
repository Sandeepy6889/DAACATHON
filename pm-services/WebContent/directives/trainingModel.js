trainingModelApp = angular.module("trainingModelApp", [])
    .run(function ($rootScope) {
        console.log("trainingModelApp initialized")
    })

function getTraningRecordClass() {
    function TraningRecord(defaults) {
        defaults = defaults || {};
        this.assetId = defaults.assetId;
        this.id = defaults.id;
        this.xFlow = defaults.xFlow;
        this.yHeight = defaults.yHeight;
        this.yEta = defaults.yEta;
        
    }
    TraningRecord.prototype.toggle = function () {

    };
    return TraningRecord;
};

trainingModelApp.value("baseUri", "services");
trainingModelApp.factory("TraningRecord", getTraningRecordClass);

trainingModelApp.factory("trainingDataService", function ($http, TraningRecord, $q, baseUri) {
    return {
        getAssetTrainingData: function (assetId) {
            var deferred = $q.defer();
            var promise = $http.get(baseUri + "/modelTraining/getAssetTrainingData/"+assetId);
            promise.then(function (response) {
                var result = response.data.map(function (assetParams) { return new TraningRecord(assetParams); });
                deferred.resolve(result);
            });
            return deferred.promise;
        },
        getAssetsIDS : function(){
        	var deferred = $q.defer();
            var promise = $http.get(baseUri + "/modelTraining/getAssetsIds");
            promise.then(function (response) {
                deferred.resolve(response.data);
            });
            return deferred.promise;
        },
        addTraningRecord: function (traningRecordData) {
            var deferred = $q.defer();
            var promise = $http.post(baseUri + "/modelTraining/insert", traningRecordData);
            promise.then(function (response) {
                deferred.resolve(new TraningRecord(response.data));
            });
            return deferred.promise;
        }
    }
});

trainingModelApp.directive("assetTrainingModel", function () {
    return {
        restrict: 'EA',
        scope: {

        },
        templateUrl: 'trainingForm.html',
        controller: function ($scope, TraningRecord, trainingDataService) {
            $scope.trainingRecords = [];
            $scope.assetsIds = [];
            $scope.assetId = '';
            trainingDataService.getAssetsIDS().then(function (result) {
                $scope.assetsIds = result;
            });

            $scope.getAssetTrainingData = function(){
            	if($scope.assetId === null){
            		$scope.trainingRecords=[];
            		return;
            	}
            	trainingDataService.getAssetTrainingData($scope.assetId.value).then(function (result) {
                    $scope.trainingRecords = result;
                });
            }
            
            $scope.addTraningRecord = function () {
                var record = new TraningRecord({
                	assetId: $scope.assetId.value,
                    xFlow: $scope.xFlow,
                    yHeight: $scope.yHeight,
                    yEta: $scope.yEta
                });
                trainingDataService.addTraningRecord(record).then(function (result) {
                    $scope.trainingRecords.push(result);
                    $("#trainingModalDialog").modal("hide");
                });
               
            }
        },
        link: function (scope, $element, $attr) {
            console.log('link function called');
        }
    };
});