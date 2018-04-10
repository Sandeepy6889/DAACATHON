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
assetsAnalysisApp.factory("kpiService", function ($http,$rootScope, KPI, RefKPI, $q, baseUri) {
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
        getAlarmStatus: function (assetId) {
            var deferred = $q.defer();
            var promise = $http.get($rootScope.appUrls.alarmsStatus + "/"+assetId);
            promise.then(function (response) {
                deferred.resolve(response.data);
            }).catch(function(error) {
            	  console.log(JSON.stringify(error));
            });
            return deferred.promise;
        },
        getVibrationData: function (assetId) {
            var deferred = $q.defer();
            var promise = $http.get('http://kpicalc-env.us-east-2.elasticbeanstalk.com/kpi-services/KPI-Calculation/fftdata' + "/"+assetId);
            promise.then(function (response) {
                deferred.resolve(response.data);
            }).catch(function(error) {
            	  console.log(JSON.stringify(error));
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
            	
            	var newSubscriptionId = $scope.assetId;
            	console.log('on change newSubscriptionId ', newSubscriptionId);
            	if (angular.isDefined($scope.Timer)) {
            		$scope.stop = $interval.cancel($scope.Timer);
                } 
                if ($scope.assetId === null) {
                    var x = document.getElementById("kpiDisplay");
                    x.style.display = "none";
                    $scope.alarms = [];
                    return;
                }
                
                $element.find("#flot-line-chart2").empty();
                $element.find("#flot-line-chart1").empty();
                $element.find("#flot-line-chart3").empty();
                suppressLowPumpEfficiency();
                suppressBlockage();
                suppressDryRunning();
                suppressDeviatedTDH();
                suppressImplerWearing();
                var x = document.getElementById("kpiDisplay");
                if(x.style.display === "none")
                	x.style.display = "block";
                
                $scope.Timer = $interval(function (){
                	var endTimestamp = new Date().getTime();
                    kpiService.getCalculatedAllKPI(endTimestamp, $scope.assetId).then(function (kpiResult) {
                    	console.log('newSubscriptionId === $scope.assetId',newSubscriptionId, $scope.assetId)
                    	if(newSubscriptionId === $scope.assetId) {
                        	
                        	kpiService.getVibrationData($scope.assetId).then(function (vibeData) {
                        		plotCharts(kpiResult, vibeData);
                        		console.log("Vibration data ", vibeData)
                        	});
                        	
    					kpiService.getAlarmStatus($scope.assetId).then(function(result) {
    						var x = result;
    						//blockage
    						if(x[0]!==null && x[0]==1){
    							blockage();
    						}
    						else{
    							suppressBlockage();
    						}
    						//lowPumpEfficiency
    						if(x[1]!==null && x[1]==1){
    							lowPumpEfficiency();
    						}
    						else{
    							suppressLowPumpEfficiency();
    						}
    						//dryRunning
    						if(x[2]!==null && x[2]==1){
    							dryRunning();
    						}
    						else{
    							suppressDryRunning();
    						}
    						//deviatedTDH
    						if(x[3]!==null && x[3]==1){
    							deviatedTDH();
    						}
    						else{
    							suppressDeviatedTDH();
    						}
    						//implerWearing
    						if(x[4]!==null && x[4]==1){
    							implerWearing();
    						}
    						else{
    							suppressImplerWearing();
    						}
    					});
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
function implerWearing() {
	$('.implerWearing').removeClass("offline");
	$('.implerWearing').addClass("online");
}
function suppressImplerWearing() {
	$('.implerWearing').removeClass("online");
	$('.implerWearing').addClass("offline");
}



function toggleAlarms() { }

function plotCharts(result, vibeData) {

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
    
    
    vibChartName="#flot-line-chart3";
	var actualVibData=vibeData;
	var refVibData= [];
	var optionsVib = {
			series : {
				lines : {
					show : true
				},
				points : {
					show : true
				}
			},
			grid : {
				hoverable : true
			}
		};
	plot(actualVibData,refVibData,optionsVib,vibChartName);

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