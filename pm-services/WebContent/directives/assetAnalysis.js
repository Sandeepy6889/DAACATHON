var assetsAnalysisApp = angular.module("assetsAnalysisApp", [])
    .run(function ($rootScope) {
        
    });
function getKPIClass() {
    function KPI(defaults) {
        defaults = defaults || {};
        this.assetID = defaults.assetID || 0;
        this.TDH = defaults.TDH;
        this.efficiency = defaults.efficiency;
        this.flow = defaults.flow;
        this.id = defaults.id;
        this.timeStamp= defaults.timeStamp;
    }
   
    return KPI;
};
function getRefKPIClass() {
    function RefKPI(defaults) {
        defaults = defaults || {};
        this.assetID = defaults.assetID || 0;
        this.TDH = defaults.refTDH;
        this.efficiency = defaults.refEfficiency;
        this.flow = defaults.refFlow;
        this.id = defaults.id;
        this.timeStamp= defaults.refTimeStamp;
    }
   
    return RefKPI;
};
assetsAnalysisApp.value("baseUri", "services");
assetsAnalysisApp.factory("KPI", getKPIClass);
assetsAnalysisApp.factory("kpiService", function ($http, KPI, $q, baseUri) {
    return {
        getAll: function () {
            var deferred = $q.defer();
            var promise = $http.get(baseUri + "/kpi/get");
            promise.then(function (response) {
                var result = response.data.map(function (kpiParams) { return new KPI(kpiParams); });
                deferred.resolve(result);
            });
            return deferred.promise;
        },
        getCalculatedAllKPI: function () {
            var deferred = $q.defer();
            var assestId='assestid';
            var timestamp= 1522393670;
            var promise = $http.get(baseUri + "/calculatedKPI/"+assetId+"/"+timeStamp);
            promise.then(function (response) {
                var result = response.data.map(function (kpiParams) { return new KPI(kpiParams); });
                deferred.resolve(result);
            });
            return deferred.promise;
        },
        getReferencedAllKPI: function () {
            var deferred = $q.defer();
            var assestId='assestid';
            var timestamp= 1522393670;
            var promise = $http.get(baseUri + "/referencedKPI/"+assetId+"/"+timeStamp);
            promise.then(function (response) {
                var result = response.data.map(function (RefkpiParams) { return new RefKPI(RefkpiParams); });
                deferred.resolve(result);
            });
            return deferred.promise;
        }
    }
});
assetsAnalysisApp.directive("assetsAnalysis", function () {
    return {
        restrict: 'EA',
        scope: {
            name: "="
        },
        templateUrl: 'analysis.html',
        controller: function ($scope, $element, KPI, kpiService) {
        	$scope.kpis = [];
        	$scope.calculatedKPIs = [];
        	$scope.referencedKPIs = [];
        	kpiService.getAll().then(function (result) {
                 console.log("all data ", result);
                 $scope.kpis = result;
             });
        	kpiService.getCalculatedAllKPI().then(function (result) {
                console.log("getReferencedAllKPI", result);
                $scope.calculatedKPIs = result;
            });
        	kpiService.getReferencedAllKPI().then(function (result) {
                console.log("getReferencedAllKPI", result);
                $scope.referencedKPIs = result;
            });
        }

    };
});