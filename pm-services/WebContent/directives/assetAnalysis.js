var assetsAnalysisApp = angular.module("assetsAnalysisApp", []).run(
    function ($rootScope) {

    });
function getKPIClass() {
    function KPI(defaults) {
        defaults = defaults || {};
        this.assetID = defaults.assetID || 0;
        this.TDH = defaults.TDH;
        this.efficiency = defaults.efficiency;
        this.flow = defaults.flow;
        this.id = defaults.id;
        this.timeStamp = defaults.timeStamp;
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
        this.timeStamp = defaults.refTimeStamp;
    }

    return RefKPI;
};
assetsAnalysisApp.value("baseUri", "services");
assetsAnalysisApp.factory("KPI", getKPIClass);
assetsAnalysisApp.factory("RefKPI", getRefKPIClass);
assetsAnalysisApp.factory("kpiService", function ($http, KPI, RefKPI, $q, baseUri) {
    return {
        getAll: function () {
            var deferred = $q.defer();
            var promise = $http.get(baseUri + "/kpi/get");
            promise.then(function (response) {
                var result = response.data.map(function (kpiParams) {
                    return new KPI(kpiParams);
                });
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
        getCalculatedAllKPI: function (timestamp, assetId) {
            var deferred = $q.defer();
            var promise = $http.get(baseUri + "/kpi/calculatedKPI/" + assetId + "/"
                + timestamp);
            promise.then(function (response) {
                var result = response.data;
                deferred.resolve(result);
            });
            return deferred.promise;
        },
        getAlarmStatus: function () {
            var deferred = $q.defer();
            var assetId = $('#selectedAsset').find(":selected").text();
            var promise = $http.get(baseUri + "/kpi/getAlarmStatus/" + assetId);
            promise.then(function (response) {
                var result = response.data;
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
        controller: function ($scope, $element,$interval, KPI, kpiService) {
            $scope.kpis = [];
            $scope.calculatedKPIs = [];
            $scope.referencedKPIs = [];
            $scope.isCalculatedKPIs = false;
            $scope.isReferencedKPIs = false;
            $scope.assetsIds = [];
            $scope.Timer = null;
            $scope.stop = false;
            kpiService.getAll().then(function (result) {
                $scope.kpis = result;
            });
            kpiService.getAssetsIDS().then(function (result) {
                $scope.assetsIds = result;
            });
            
            $scope.$on('$destroy', function (event) {
            	if (angular.isDefined($scope.Timer)) {
            		$scope.stop = $interval.cancel($scope.Timer);
                } 
            });
            $scope.getKpi = function () {
            	$scope.stop = false;
            	if (angular.isDefined($scope.Timer)) {
            		$scope.stop = $interval.cancel($scope.Timer);
                } 
                if ($scope.assetId === null) {
                    var x = document.getElementById("kpiDisplay");
                    x.style.display = "none";
                    $scope.alarms = [];
                    return;
                }
                
                var x = document.getElementById("kpiDisplay");
                if(!$scope.stop){
                	x.style.display = "block";
            	}
                
                $scope.Timer = $interval(function (){
                    kpiService.getCalculatedAllKPI(new Date().getTime(), $scope.assetId).then(function (result) {
                        if(!$scope.stop){
                        	plotCharts(result);
                        }
                    });
                },1000);
            }
        }

    };
});

function blockage() {

    $('.blockage').removeClass("offline");
    $('.blockage').addClass("online");
}
function suppressBlockage() {

    $('.blockage').removeClass("online");
    $('.blockage').addClass("offline");
}
function lowPumpEfficiency() {

    $('.lowPumpEfficiency').removeClass("offline");
    $('.lowPumpEfficiency').addClass("online");
}
function suppressLowPumpEfficiency() {

    $('.lowPumpEfficiency').removeClass("online");
    $('.lowPumpEfficiency').addClass("offline");
}
function dryRunning() {

    $('.dryRunning').removeClass("offline");
    $('.dryRunning').addClass("online");
}
function suppressDryRunning() {

    $('.dryRunning').removeClass("online");
    $('.dryRunning').addClass("offline");
}
function deviatedTDH() {

    $('.deviatedTDH').removeClass("offline");
    $('.deviatedTDH').addClass("online");
}
function suppressDeviatedTDH() {

    $('.deviatedTDH').removeClass("online");
    $('.deviatedTDH').addClass("offline");
}



function toggleAlarms() { }

function plotCharts(result) {

    tDHChartName = "#flot-line-chart1";
    var actualTDHData = result[0];
    var refTDHData = result[1];

    var optionsTDH = {
        series: {
            lines: {
                show: true
            },
            points: {
                show: true
            }
        },
        grid: {
            hoverable: true
        }
    };
    plot(actualTDHData, refTDHData, optionsTDH, tDHChartName);
    effChartName = "#flot-line-chart2";
    var actualEffData = result[2];
    var refEffData = result[3];
    var optionsEff = {
        series: {
            lines: {
                show: true
            },
            points: {
                show: true
            }
        },
        grid: {
            hoverable: true
        }
    };
    plot(actualEffData, refEffData, optionsEff, effChartName);


}

function plot(actualData, RefData, options, chartName) {
    $(chartName).empty();
    var plotObj = $.plot($(chartName), [{
        data: actualData,
        label: "Actual"
    }, {
        data: RefData,
        label: "Reference"
    }], options);
}