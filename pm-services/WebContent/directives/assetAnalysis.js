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
			var assetId = 'assetid';
			var timestamp = 1522393670;
			var promise = $http.get(baseUri + "/kpi/calculatedKPI/" + assetId + "/"
					+ timestamp);
			promise.then(function(response) {
				debugger;
				var result = response.data;
				console.log("calculatedKPI data ", result);
//				.map(function(kpiParams) {
//					return new KPI(kpiParams);
//				});
				deferred.resolve(result);
			});
			return deferred.promise;
		},
		getReferencedAllKPI : function() {
			var deferred = $q.defer();
			var assetId = 'assetid';
			var timestamp = 1522393670;
			var promise = $http.get(baseUri + "/kpi/referencedKPI/" + assetId + "/"
					+ timestamp);
			promise.then(function(response) {
				var result = response.data.map(function(RefkpiParams) {
					return new RefKPI(RefkpiParams);
				});
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
					$scope.calculatedKPIs = result;
					$scope.isCalculatedKPIs = true;
					displayDiv();
					toggleAlarms();
				});
				kpiService.getReferencedAllKPI().then(function(result) {
					console.log("getReferencedAllKPI", result);
					$scope.referencedKPIs = result;
					$scope.isReferencedKPIs = true;
				});

				/*while(true){
					if($scope.isCalculatedKPIs === true && $scope.isReferencedKPIs === true){
						debugger;
						$scope.isCalculatedKPIs = false;
						$scope.isReferencedKPIs = false;
						break;
						TDHChartName="#flot-line-chart1";
						var actualTDHData,RefTDHData;
						
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
									min : -1.2,
									max : 1.2
								}
							};
						plot(actualTDHData,RefTDHData,optionsTDH);
						TDHChartName="#flot-line-chart2";
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
									min : -1.2,
									max : 1.2
								},
								tooltip : true,
								tooltipOpts : {
									content : "'%s' of %x.1 is %y.4",
									shifts : {
										x : -60,
										y : 25
									}
								}
							};
						plotEfficiency(actualEffData,RefEffData,optionsEff);
					}
				}*/
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

function displayDiv() {
    var x = document.getElementById("kpiDisplay");
    x.style.display = "block";
}

function toggleAlarms(){
	debugger;
	var x = [1,0,1,1];
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
}