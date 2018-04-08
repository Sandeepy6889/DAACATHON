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
            var promise = $http.get($rootScope.enggAssets + "/get");
            promise.then(function (response) {
                var result = response.data.map(function (assetParams) { return new Asset(assetParams); });
                deferred.resolve(result);
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
            var promise = $http.get($rootScope.opcAssetSubUrl + "/" + assetId);
            promise.then(function (response) {
                deferred.resolve(response.data);
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
            $scope.message = '';
            $scope.title = '';
            $scope.asset = '';
            $scope.isAlertEnable = false;
            
            filterNonConfiguredAssets = function() {
            	assetService.getEngineeredAssets().then(function (enggAssets) {
            		console.log('All enggAssets ', enggAssets);
            		assetService.getConfiguredAssets().then(function (confAssets) {
            			for(var i = 0;i < enggAssets.length;i++){
            				for(var j = 0;j < confAssets.length;j++){
            					if(enggAssets[i].assetID === confAssets[j].assetID){
            						enggAssets.splice(i,1);
            					}
            				}
                    	}
            			$scope.nonConfAssets = enggAssets;
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
                     $element.find('#modelMessage').removeClass('alert-warning');
                     $element.find('#modelMessage').addClass('alert-success');
                	 if(result !== ""){
                		 var asset = new Asset(result);
                		 $scope.confAssets.push(asset);
                		 assetService.notifyOpcForSubscription($scope.assetID).then(function (result) {
                			 if(result === 'success'){
                				 $scope.isAlertEnable = true;
                				 $scope.title = 'Success!';
                                 $scope.message = ' Asset '+$scope.newAsset.assetID+' configured successfully';
                                 $element.find('#modelMessage').addClass('alert-success');
                			 }
                			 else
                			 {
                				 $scope.isAlertEnable = true;
                				 $scope.title = 'Warning!';
                                 $scope.message = ' Asset '+$scope.newAsset.assetID+' configured successfully but opc subscription failed';
                                 $element.find('#modelMessage').addClass('alert-warning');
                			 }
                		 });
                		 for(var i = 0;i < $scope.nonConfAssets.length;i++){
                     		if($scope.nonConfAssets[i].assetID === $scope.newAsset.assetID){
                     			$scope.nonConfAssets.splice(i,1);
                     			break;
                     		}
                     	}
                	 }else{
                		 $scope.isAlertEnable = true;
        				 $scope.message = 'Failure! Asset '+$scope.newAsset.assetID+' cannot be configured'; 
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