var assetsManagementApp = angular.module("assetsManagementApp", [])
    .run(function ($rootScope) {
        $rootScope.Wim = "parent";
    })
// Factory functions
function getAssetClass() {
    function Asset(defaults) {
        defaults = defaults || {};
        this.assetID = defaults.assetID || 0;
        this.assetName = defaults.assetName;
        this.ratedPower = defaults.ratedPower;
        this.motorEfficiency = defaults.motorEfficiency;
        this.motorRatedSpeed = defaults.motorRatedSpeed;
        this.minRatedFlowOfPump = defaults.minRatedFlowOfPump;
        this.waterDensity = defaults.waterDensity;
        this.threadholdLT = defaults.threadholdLT;
        this.suctionDiameter= defaults.suctionDiameter,
        this.dischargeDiameter= defaults.dischargeDiameter,
        this.eleveationDiff= defaults.eleveationDiff
    }
    return Asset;
};
assetsManagementApp.value("baseUri", "services");
assetsManagementApp.factory("Asset", getAssetClass);
assetsManagementApp.factory("assetService", function ($http,$rootScope, Asset, $q, baseUri) {
    return {
    	getConfiguredAssets: function () {
            var deferred = $q.defer();
            var promise = $http.get(baseUri + "/assetPrmtz/getAll");
            promise.then(function (response) {
                var result = response.data.map(function (assetParams) { return new Asset(assetParams); });
                deferred.resolve(result);
            });
            return deferred.promise;
        },
        getEngineeredAssets: function () {
            var deferred = $q.defer();
            var promise = $http.get($rootScope.appUrls.enggAssets + "/get");
            promise.then(function (response) {
                var result = response.data.map(function (assetParams) { return new Asset(assetParams); });
                deferred.resolve(result);
            }).catch(function(error) {
            	  console.log(JSON.stringify(error));
            });
            return deferred.promise;
        },
        addAsset: function (assetParamsData) {
            var deferred = $q.defer();
            var promise = $http.post(baseUri + "/assetPrmtz/insert", assetParamsData);
            promise.then(function (response) {
                deferred.resolve(response.data);
            });
            return deferred.promise;
        },
        notifyOpcForSubscription: function (assetId) {
            var deferred = $q.defer();
            var promise = $http.get($rootScope.appUrls.assetCreated + "/" + assetId);
            promise.then(function (response) {
                deferred.resolve(response.data);
            }).catch(function(error) {
          	  console.log(JSON.stringify(error));
        	  deferred.resolve(error);
            });
            return deferred.promise;
        },
        notifyPumpDataClient: function (assetId) {
            var deferred = $q.defer();
            var promise = $http.get($rootScope.appUrls.assetCreatedPClient + "/" + assetId);
            promise.then(function (response) {
            	var resp = response;
            	console.log('response from pump data client ',resp);
                deferred.resolve(response.data);
            }).catch(function(error) {
            	  console.log(JSON.stringify(error));
            	  deferred.resolve(error);
            });
            return deferred.promise;
        }
    }
});
assetsManagementApp.directive("assetsManagement", function () {
    return {
        restrict: 'EA',
        scope: {
            name: "="
        },
        templateUrl: 'assets-info.html',
        controller: function ($scope, $element, Asset, assetService) {
            $scope.confAssets = [];
            $scope.nonConfAssets = [];
            $scope.newAsset = {};
            $scope.message = ['','',''];
            $scope.title = ['','',''];
            $scope.assetValue = '';
            $scope.isAlertEnable = false;
            
            filterNonConfiguredAssets = function() {
            	assetService.getEngineeredAssets().then(function (enggAssets) {
            		console.log('All enggAssets ', enggAssets);
            		var assets = [];
            		assetService.getConfiguredAssets().then(function (confAssets) {
            			var isConfigured = false;
            			for(var i = 0;i < enggAssets.length;i++){
            				isConfigured = false;
            				for(var j = 0;j < confAssets.length;j++){
            					if(enggAssets[i].assetID === confAssets[j].assetID){
            						isConfigured = true;
            						break;
            					}
            				}
            				if(!isConfigured)
            					assets.push(enggAssets[i]);
                    	}
            			$scope.nonConfAssets = assets;
            			$scope.confAssets = confAssets;
            			console.log('nonConfAssets ', $scope.nonConfAssets);
            			console.log('confAssets ', $scope.confAssets);
                    });
                });
            }();
             
            $scope.initAssetForm = function(){
            	if($scope.assetValue === ''){
            		$scope.newAsset = {};
            		return;
            	}
            	$scope.newAsset = JSON.parse($scope.assetValue);
            }
            
            $scope.addNewAsset = function () {
            	$scope.isAlertEnable = false;
                 assetService.addAsset($scope.newAsset).then(function (result) {
                	 $element.find('#modelMessage').removeClass('alert-danger');
                     $element.find('#modelMessage').addClass('alert-success');
                	 if(result !== ""){
                		 var asset = new Asset(result);
                		 $scope.confAssets.push(asset);
                		 
                		 $scope.title[0] = 'Success!';
                		 $scope.message[0] = ' Asset '+asset.assetID+' configured successfully';
                		 
                		 assetService.notifyOpcForSubscription(asset.assetID).then(function (result) {
                			 if(result === 'success'){
                				 $scope.title[1] = 'Success!';
                        		 $scope.message[1] = ' For Asset ID '+asset.assetID+', notification sent to KPI Calculation app';
                			 }
                			 else
                			 {
                				 $scope.title[1] = 'Faliure!';
                        		 $scope.message[1] = ' For Asset ID '+asset.assetID+', notification failure for KPI Calculation app';
                			 }
                		 });
                		 
                		 assetService.notifyPumpDataClient(asset.assetID).then(function (result) {
                			 if(result === 'success'){
                				 $scope.title[2] = 'Success!';
                        		 $scope.message[2] = ' For Asset ID '+asset.assetID+', notification send to Pump data client app';
                			 }
                			 else
                			 {
                				 $scope.title[2] = 'Faliure!';
                        		 $scope.message[2] = ' For Asset ID '+asset.assetID+', notification failure for Pump data client app';
                			 }
                		 });
                		 
                		 
                		 for(var i = 0;i < $scope.nonConfAssets.length;i++){
                     		if($scope.nonConfAssets[i].assetID === $scope.newAsset.assetID){
                     			$scope.nonConfAssets.splice(i,1);
                     			break;
                     		}
                     	}
                		 $scope.isAlertEnable = true;
                		 $element.find('#modelMessage').addClass('alert-success');
                	 }else{
                		 $scope.title[0] = 'Failure!';
        				 $scope.message[0] = ' Asset '+$scope.newAsset.assetID+' cannot be configured'; 
        				 $element.find('#modelMessage').addClass('alert-danger');
                	 }
                      $("#exampleModalLong").modal("hide");
                  });
            }
           
            $scope.removeAsset = function (rmAsset) {
            	for(var i = 0;i < $scope.confAssets.length;i++){
            		if($scope.confAssets[i].assetID === rmAsset.assetID){
            			$scope.confAssets.splice(i,1);
            			break;
            		}
            	}
            }
            $scope.showAssetInfo = function(asset){
            	$scope.assetInfo = asset;
            	 $("#assetInfoBox").modal("show");
            	console.log(asset);
            }
        },
        link: function (scope, $element, $attr) {

        }
    };
});