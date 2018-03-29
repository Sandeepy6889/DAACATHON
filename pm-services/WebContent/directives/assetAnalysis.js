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
    }
   
    return KPI;
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
        	kpiService.getAll().then(function (result) {
                 console.log("all data ", result);
                 $scope.kpis = result;
             });
        }

    };
});