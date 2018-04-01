trainingModelApp = angular.module("trainingModelApp", [])
    .run(function ($rootScope) {
        console.log("trainingModelApp initialized")
    })

function getErrorMessage(value, maxValue){
	if(value > maxValue)
		return " should be less than or equal to "+maxValue;
	return "";
}
    
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

trainingModelApp.factory("trainingDataService", function ($http, TraningRecord, $q, baseUri, $rootScope) {
    return {
        getAssetTrainingData: function (assetId) {
            var deferred = $q.defer();
            var promise = $http.get(baseUri + "/modelTraining/getAssetTrainingData/" + assetId);
            promise.then(function (response) {
                var result = response.data.map(function (assetParams) { return new TraningRecord(assetParams); });
                deferred.resolve(result);
            });
            return deferred.promise;
        },
        getAssetTraingMaxValues: function (assetId) {
            var deferred = $q.defer();
            var promise = $http.get(baseUri + "/modelTraining/getAssetMaxValues/" + assetId);
            promise.then(function (response) {
                var result = response.data.map(function (assetParams) { return new TraningRecord(assetParams); });
                deferred.resolve(result);
            });
            return deferred.promise;
        },
        getAssetsIDS: function () {
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
        },
        addMaxValuesForModel: function (maxValuesRecord) {
            var deferred = $q.defer();
            var promise = $http.post(baseUri + "/modelTraining/insert/maxValues", maxValuesRecord);
            promise.then(function (response) {
                deferred.resolve(new TraningRecord(response.data));
            });
            return deferred.promise;
        },
        trainigModel: function (assetId) {
            var deferred = $q.defer();
            var promise = $http.get($rootScope.modelURI + "/train?asset_id=" + assetId);
            promise.then(function (response) {
                deferred.resolve(response.data);
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
        controller: function ($scope, $element, TraningRecord, trainingDataService) {
            $scope.trainingRecords = [];
            $scope.assetsIds = [];
            $scope.assetId = '';
            $scope.isTeachingModel = false;
            $scope.title = '';
            $scope.message = '';
            $scope.isAlertEnable = false;
            $scope.assetMaxValues = [];
            $scope.maxValuesButtonDiabled = true;
            $scope.maxValueErrors = [];

            trainingDataService.getAssetsIDS().then(function (result) {
                $scope.assetsIds = result;
            });

            $scope.getAssetTrainingData = function () {
                $scope.isAlertEnable = false;
                if ($scope.assetId === null) {
                    $scope.trainingRecords = [];
                    $scope.assetMaxValues = [];
                    return;
                }
                trainingDataService.getAssetTrainingData($scope.assetId).then(function (result) {
                    $scope.trainingRecords = result;
                });
                
                trainingDataService.getAssetTraingMaxValues($scope.assetId).then(function (result) {
                    $scope.assetMaxValues = result;
                });
                
            }

            $scope.addTraningRecord = function () {
            	$scope.maxValueErrors = [];
            	var isvalid = true;
            	var msg = '';
            	if((msg = getErrorMessage($scope.xFlow, $scope.assetMaxValues[0].xFlow)).length > 0){
            		$scope.maxValueErrors.push(msg);
            		isvalid  = false;
            	}
            	else{
            		$scope.maxValueErrors.push(msg);
            	}
                
            	if((msg = getErrorMessage($scope.yHeight, $scope.assetMaxValues[0].yHeight)).length > 0){
            		$scope.maxValueErrors.push(msg);
            		isvalid  = false;
            	}
            	else{
            		$scope.maxValueErrors.push(msg);
            	}
            	if((msg = getErrorMessage($scope.yEta, $scope.assetMaxValues[0].yEta)).length > 0){
            		$scope.maxValueErrors.push(msg);
            		isvalid  = false;
            	}
            	else{
            		$scope.maxValueErrors.push(msg);
            	}
            	if(!isvalid)
            		return;
            	
                var record = new TraningRecord({
                    assetId: $scope.assetId,
                    xFlow: $scope.xFlow,
                    yHeight: $scope.yHeight,
                    yEta: $scope.yEta
                });
                trainingDataService.addTraningRecord(record).then(function (result) {
                    $scope.trainingRecords.push(result);
                    $("#trainingModalDialog").modal("hide");
                });
            }

            $scope.registerAssetMaxValues = function () {
                var record = new TraningRecord({
                    assetId: $scope.assetId,
                    xFlow: $scope.xFlowM,
                    yHeight: $scope.yHeightM,
                    yEta: $scope.yEtaM
                });
                trainingDataService.addMaxValuesForModel(record).then(function (result) {
                    $scope.maxValues.push(result);
                    $scope.maxValuesButtonDiabled = true;
                    $("#maxValuesForModelDiaglog").modal("hide");
                });
            }

            $scope.trainingModel = function () {
                $scope.isAlertEnable = false;
                var assetId = $scope.assetId;
                if (assetId !== null) {
                    $scope.isTeachingModel = true;
                    trainingDataService.trainigModel(assetId).then(function (result) {
                        $scope.isAlertEnable = true;
                        if (result === "success") {
                            $scope.title = 'Success!';
                            $scope.message = 'Model trained successfully';
                            $element.find('#modelMessage').removeClass('alert-danger');
                            $element.find('#modelMessage').addClass('alert-success');
                        }
                        else {
                            $scope.title = 'Failure!';
                            $scope.message = result;
                            $element.find('#modelMessage').removeClass('alert-success');
                            $element.find('#modelMessage').addClass('alert-danger');
                        }
                        $scope.isTeachingModel = false;
                    });
                }

            }
        },
        link: function (scope, $element, $attr) {
            console.log('link function called');
        }
    };
});