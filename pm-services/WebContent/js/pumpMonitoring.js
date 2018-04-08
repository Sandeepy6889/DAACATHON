var assetsMgmtAppApplication = angular.module("assetsMgmtApp", ["trainingModelApp", "assetsManagementApp", "assetsAnalysisApp", "assetReportApp"]);
assetsMgmtAppApplication.config(function($httpProvider) {
	  $httpProvider.defaults.useXDomain = true;
});


assetsMgmtAppApplication.run(function ($rootScope) {
    $rootScope.templateURL = "dashboard.html";
    $rootScope.menus = "menus.html";
});

assetsManagementApp.value("baseUri", "services");
assetsMgmtAppApplication.factory("appUrlsService", function ($http, $q, baseUri) {
    return {
    	getAllUrls: function () {
            var deferred = $q.defer();
            var promise = $http.get(baseUri + "/appUrls/get");
            promise.then(function (response) {
                var result = response.data;
                deferred.resolve(result);
            });
            return deferred.promise;
        }
    }
});

assetsMgmtAppApplication.controller("assetsController", function ($scope, $rootScope, appUrlsService) {
	
	appUrlsService.getAllUrls().then(function(result){
		$rootScope.appUrls = result;
    	window.scope = $scope;
    });
	
    $scope.showTraingForm = function (name) {
        console.log('formName ', name);
        $scope.templateURL = name;
    }

});