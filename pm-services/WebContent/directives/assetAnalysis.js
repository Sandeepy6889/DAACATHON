var assetsAnalysisApp = angular.module("assetsAnalysisApp", []).run(
		function($rootScope) {

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
assetsAnalysisApp.factory("kpiService", function($http, KPI,RefKPI, $q, baseUri) {
	return {
		getAll : function() {
			var deferred = $q.defer();
			var promise = $http.get(baseUri + "/kpi/get");
			promise.then(function(response) {
				var result = response.data.map(function(kpiParams) {
					return new KPI(kpiParams);
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
		getCalculatedAllKPI : function() {
			var deferred = $q.defer();
			debugger;
			var assetId = $('#selectedAsset').find(":selected").text();
//			var timestamp = (new Date).getTime();
//			console.log("selectedAsset", assetId);
//			var assetId = 'assetid';
			var timestamp = 1522393678;
			var promise = $http.get(baseUri + "/kpi/calculatedKPI/" + assetId + "/"
					+ timestamp);
			promise.then(function(response) {
				debugger;
				var result = response.data;
				console.log("calculatedKPI data ", result);
				deferred.resolve(result);
			});
			return deferred.promise;
		},
		getAlarmStatus : function() {
			var deferred = $q.defer();
			var assetId = $('#selectedAsset').find(":selected").text();
			//var assetId = 'assetid';
			var promise = $http.get(baseUri + "/kpi/getAlarmStatus/" + assetId);
			promise.then(function(response) {
				var result = response.data;
				console.log("alarm status data ", result);
				deferred.resolve(result);
			});
			return deferred.promise;
		}
	}
});
assetsAnalysisApp.directive("assetsAnalysis", function() {
	return {
		restrict : 'EA',
		scope : {
			name : "="
		},
		templateUrl : 'analysis.html',
		controller : function($scope, $element, KPI, kpiService) {
			$scope.kpis = [];
			$scope.calculatedKPIs = [];
			$scope.referencedKPIs = [];
			$scope.isCalculatedKPIs = false;
			$scope.isReferencedKPIs = false;
			$scope.assetsIds = [];
			kpiService.getAll().then(function(result) {
				console.log("all data ", result);
				$scope.kpis = result;
			});
			kpiService.getAssetsIDS().then(function(result) {
				$scope.assetsIds = result;
			});
			$scope.getKpi = function() {
				kpiService.getCalculatedAllKPI().then(function(result) {
					console.log("getCalculatedKPI", result);
					displayDiv();
					plotCharts(result);
					kpiService.getAlarmStatus().then(function(result) {
						console.log("getAlarmStatus", result);
						var x = result;
						//blockage
						if(x[0]==1){
							blockage();
						}
						else{
							suppressBlockage();
						}
						//lowPumpEfficiency
						if(x[1]==1){
							lowPumpEfficiency();
						}
						else{
							suppressLowPumpEfficiency();
						}
						//dryRunning
						if(x[2]==1){
							dryRunning();
						}
						else{
							suppressDryRunning();
						}
						//deviatedTDH
						if(x[3]==1){
							deviatedTDH();
						}
						else{
							suppressDeviatedTDH();
						}
					});

				});
			}
		}

	};
});

function blockage() {
	debugger;
	$('.blockage').removeClass("offline");
	$('.blockage').addClass("online");
}
function suppressBlockage() {
	debugger;
	$('.blockage').removeClass("online");
	$('.blockage').addClass("offline");
}
function lowPumpEfficiency() {
	debugger;
	$('.lowPumpEfficiency').removeClass("offline");
	$('.lowPumpEfficiency').addClass("online");
}
function suppressLowPumpEfficiency() {
	debugger;
	$('.lowPumpEfficiency').removeClass("online");
	$('.lowPumpEfficiency').addClass("offline");
}
function dryRunning() {
	debugger;
	$('.dryRunning').removeClass("offline");
	$('.dryRunning').addClass("online");
}
function suppressDryRunning() {
	debugger;
	$('.dryRunning').removeClass("online");
	$('.dryRunning').addClass("offline");
}
function deviatedTDH() {
	debugger;
	$('.deviatedTDH').removeClass("offline");
	$('.deviatedTDH').addClass("online");
}
function suppressDeviatedTDH() {
	debugger;
	$('.deviatedTDH').removeClass("online");
	$('.deviatedTDH').addClass("offline");
}


function displayDiv() {
    var x = document.getElementById("kpiDisplay");
    x.style.display = "block";
}

function toggleAlarms(){}

function plotCharts(result){
	debugger;

	tDHChartName="#flot-line-chart1";
	var actualTDHData=result[0];
	var refTDHData=result[1];
	
	var optionsTDH = {
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
			// IMPORTANT! this is needed for tooltip to work
			},
			yaxis : {
				min : 0,
				max : 30
			}
		};
	plot(actualTDHData,refTDHData,optionsTDH,tDHChartName);
	effChartName="#flot-line-chart2";
	var actualEffData=result[2];
	var refEffData=result[3];
	var optionsEff = {
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
			// IMPORTANT! this is needed for tooltip to work
			},
			yaxis : {
				min : 0,
				max : 30
			}
		};
	plot(actualEffData,refEffData,optionsEff,effChartName);


}

function plot(actualData,RefData,options,chartName) {
	debugger;

	var plotObj = $.plot($(chartName), [ {
		data : actualData,
		label : "Actual"
	}, {
		data : RefData,
		label : "Reference"
	} ], options);
}