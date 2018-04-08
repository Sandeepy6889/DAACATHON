var assetsManagementApp = angular.module("urlsManagementApp", [])
    .run(function ($rootScope) {
        $rootScope.Wim = "parent";
    })

    
    function getURLsInfoClass() {
    function URLsInfo(defaults) {
        defaults = defaults || {};
        this.id = 1;
        this.assetCreated = defaults.assetCreated;
        this.assetCreatedPClient = defaults.assetCreatedPClient;
        this.assetTrained = defaults.assetTrained;
        this.alarmsStatus = defaults.alarmsStatus;
        this.teachModel = defaults.teachModel;
        this.enggAssets = defaults.enggAssets;
    }
    return URLsInfo;
};
assetsManagementApp.value("baseUri", "services");
assetsManagementApp.factory("URLsInfo", getURLsInfoClass);
assetsManagementApp.factory("appUrlsService", function ($http, URLsInfo, $q, baseUri) {
    return {
    	getAllUrls: function () {
            var deferred = $q.defer();
            var promise = $http.get(baseUri + "/appUrls/get");
            promise.then(function (response) {
                var result = response.data;
                deferred.resolve(result);
            }).catch(function(error) {
        	  console.log(JSON.stringify(error));
            });
            return deferred.promise;
        },
        updateUrls: function (urlsInfo) {
            var deferred = $q.defer();
            var promise = $http.post(baseUri + "/appUrls/update", urlsInfo);
            promise.then(function (response) {
                deferred.resolve(response.data);
            }).catch(function(error) {
        	  console.log(JSON.stringify(error));
            });
            return deferred.promise;
        },
    }
});

assetsManagementApp.controller("appUrlsController",function ($scope, $element, URLsInfo, appUrlsService) {
    $scope.urlsInfo= {};
    $scope.message = '';
    appUrlsService.getAllUrls().then(function(result){
    	$scope.urlsInfo = result;
    });
    
    $scope.update = function(){
    	 appUrlsService.updateUrls($scope.urlsInfo).then(function(result){
    		 $scope.message = result;
         });
    }
});

