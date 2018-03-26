trainingModelApp = angular.module("trainingModelApp", [])
    .run(function ($rootScope) {
        console.log("trainingModelApp initialized")
    })

function getTraningRecordClass() {
    function TraningRecord(defaults) {
        defaults = defaults || {};
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
        getAll: function () {
            var deferred = $q.defer();
            var promise = $http.get(baseUri + "/modelTraining/getAll/");
            promise.then(function (response) {
                var result = response.data.map(function (assetParams) { return new TraningRecord(assetParams); });
                deferred.resolve(result);
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
            trainingDataService.getAll().then(function (result) {
                console.log("all data ", result);
                $scope.trainingRecords = result;
            });
            //  $scope.trainingRecords.push(new TraningRecord());
            window.scope = $scope;

            $scope.getAllTrainingRecords = function () { }
            $scope.addTraningRecord = function () {
                var record = new TraningRecord({
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