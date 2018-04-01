var assetsMgmtAppApplication = angular.module("assetsMgmtApp", ["trainingModelApp", "assetsManagementApp", "assetsAnalysisApp", "assetReportApp"]);
assetsMgmtAppApplication.config(function($httpProvider) {
	  $httpProvider.defaults.useXDomain = true;
});


assetsMgmtAppApplication.run(function ($rootScope) {
    $rootScope.templateURL = "dashboard.html";
    $rootScope.menus = "menus.html";
    $rootScope.modelURI =  "http://kpipredictionohio-dev.us-east-2.elasticbeanstalk.com";
    console.log('assetsMgmtAppApplication bugs modiefied called');
});
assetsMgmtAppApplication.controller("assetsController", function ($scope) {

    $scope.showTraingForm = function (name) {
        console.log('formName ', name);
        $scope.templateURL = name;
    }

});