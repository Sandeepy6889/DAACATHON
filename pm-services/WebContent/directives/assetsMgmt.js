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
    Asset.prototype.toggle = function () {

    };
    return Asset;
};
assetsManagementApp.value("baseUri", "services");
assetsManagementApp.factory("Asset", getAssetClass);
assetsManagementApp.factory("assetService", function ($http, Asset, $q, baseUri) {
    return {
        getAll: function () {
            var deferred = $q.defer();
            var promise = $http.get(baseUri + "/assetPrmtz/getAll");
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
                deferred.resolve(new Asset(response.data));
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
            $scope.assets = [];
             assetService.getAll().then(function (result) {
                 console.log("all data ", result);
                 $scope.assets = result;
             });
            $scope.addNewAsset = function () {
                var asset = new Asset({
                    assetID: $scope.assetID,
                    assetName: $scope.assetName,
                    ratedPower: $scope.ratedPower,
                    motorEfficiency: $scope.motorEfficiency,
                    motorRatedSpeed: $scope.motorRatedSpeed,
                    minRatedFlowOfPump: $scope.minRatedFlowOfPump,
                    waterDensity: $scope.waterDensity,
                    threadholdLT: $scope.threadholdLT,
                    suctionDiameter: $scope.suctionDiameter,
                    dischargeDiameter: $scope.dischargeDiameter,
                    eleveationDiff: $scope.eleveationDiff
                });
                 assetService.addAsset(asset).then(function (asset) {
                      $scope.assets.push(asset);
                      $("#exampleModalLong").modal("hide");
                  });
            }
            $scope.updateAsset = function () {

            }
            $scope.removeAsset = function (rmAsset) {
            	for(var i = 0;i < $scope.assets.length;i++){
            		if($scope.assets[i].assetID === rmAsset.assetID){
            			$scope.assets.splice(i,1);
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