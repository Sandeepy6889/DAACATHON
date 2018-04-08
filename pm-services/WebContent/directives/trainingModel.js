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
        updateTraningRecord: function (traningRecordData) {
            var deferred = $q.defer();
            var promise = $http.post(baseUri + "/modelTraining/update", traningRecordData);
            promise.then(function (response) {
                deferred.resolve(response.data);
            });
            return deferred.promise;
        },
        deleteTraningRecord: function (traningRecordId) {
            var deferred = $q.defer();
            var promise = $http.get(baseUri + "/modelTraining/delete/"+traningRecordId);
            promise.then(function (response) {
                deferred.resolve(response.data);
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
        },
        notifyModelStatus: function (assetId) {
            var deferred = $q.defer();
            var promise = $http.get($rootScope.notifyTrainingStatusUrl + "/" + assetId);
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
            $scope.operation = '';
            $scope.oprTitle = '';
            $scope.id = '';

            trainingDataService.getAssetsIDS().then(function (result) {
                $scope.assetsIds = result;
            });
            
            $scope.setOperation = function(opr, title, record){
            	$scope.oprTitle = title;
            	$scope.operation = opr;
            	if(opr === 'UPDATE'){
	            	$scope.xFlow = record.xFlow;
	            	$scope.yHeight = record.yHeight;
	            	$scope.yEta = record.yEta;
	            	$scope.id = record.id;
            	}
            	else{
            		$scope.xFlow = '';
	            	$scope.yHeight = '';
	            	$scope.yEta = '';
	            	$scope.id = 0;
            	}
            }

            $scope.getAssetTrainingData = function () {
                $scope.isAlertEnable = false;
                if ($scope.assetId === null) {
                    $scope.trainingRecords = [];
                    return;
                }
                trainingDataService.getAssetTrainingData($scope.assetId).then(function (result) {
                    $scope.trainingRecords = result;
                });
            }
            
            $scope.addTraningRecord = function () {
                var record = new TraningRecord({
                	id: $scope.id,
                    assetId: $scope.assetId,
                    xFlow: $scope.xFlow,
                    yHeight: $scope.yHeight,
                    yEta: $scope.yEta
                });
                
                if($scope.operation === 'ADD')
                {
	                trainingDataService.addTraningRecord(record).then(function (result) {
	                    $scope.trainingRecords.push(result);
	                    $("#trainingModalDialog").modal("hide");
	                });
                }else if($scope.operation === 'UPDATE'){
                	trainingDataService.updateTraningRecord(record).then(function (result) {
                		var list = $scope.trainingRecords;
                		for(var i = 0;i<list.length;i++)
                		{
                			if(list[i].id === record.id){
                				list[i].xFlow = record.xFlow,
                				list[i].yHeight = record.yHeight,
                				list[i].yEta = record.yEta;
                				break;
                			}
                		}
	                    $("#trainingModalDialog").modal("hide");
	                });
                }
            }

            $scope.trainingModel = function () {
                $scope.isAlertEnable = false;
                var assetId = $scope.assetId;
                if (assetId !== null) {
                    $scope.isTeachingModel = true;
                    trainingDataService.trainigModel(assetId).then(function (modelResult) {
                        $scope.isAlertEnable = true;
                        $element.find('#modelMessage').removeClass('alert-danger');
                		$element.find('#modelMessage').removeClass('alert-success');
                        if (modelResult === "success"){
                        	trainingDataService.notifyModelStatus(assetId).then(function (notifyResult) {
                            	if (notifyResult === "success") {
                            		$scope.title = 'Success!';
                            		$scope.message = 'Model trained successfully and notification sent';
                            		$element.find('#modelMessage').addClass('alert-success');
                            	}
                            	else {
                            		$scope.title = 'Failure!';
                            		$scope.message = 'Model trained successfully and but notification failed';
                            		$element.find('#modelMessage').addClass('alert-danger');
                            	}
                            	$scope.isTeachingModel = false;
                        	 });
                        	}
                        else{
                        	$scope.title = 'Failure!';
                    		$element.find('#modelMessage').addClass('alert-danger');
                    		$scope.isTeachingModel = false;
                        }
                        
                    });
                }

            },
            $scope.deleteTraningRecord = function (recordId) {
            	
            	trainingDataService.deleteTraningRecord(recordId).then(function (result) {
            		if(result === 'SUCCESS'){
            			for(var i = 0;i < $scope.trainingRecords.length;i++){
            				if($scope.trainingRecords[i].id === recordId){
            					$scope.trainingRecords.splice(i,1);
            					break;
            				}
            			}
            		}
                });
            }
        },
        link: function (scope, $element, $attr) {
        }
    };
});