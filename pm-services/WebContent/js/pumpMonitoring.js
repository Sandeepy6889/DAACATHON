var assetsMgmtAppApplication = angular.module("assetsMgmtApp", ["trainingModelApp", "assetsManagementApp", "assetsAnalysisApp", "assetReportApp"]);
assetsMgmtAppApplication.config(function($httpProvider) {
	  $httpProvider.defaults.useXDomain = true;
});


assetsMgmtAppApplication.run(function ($rootScope) {
    $rootScope.templateURL = "dashboard.html";
    $rootScope.menus = "menus.html";
    $rootScope.modelURI =  "http://kpipredictionohio-dev.us-east-2.elasticbeanstalk.com";
    $rootScope.opcAssetSubUrl =  "http://kpicalc-env.us-east-2.elasticbeanstalk.com/kpi-services/KPI-Calculation/assetcreated";
    $rootScope.notifyTrainingStatusUrl = 'http://kpicalc-env.us-east-2.elasticbeanstalk.com/kpi-services/KPI-Calculation/assetTrainStatus';
    $rootScope.enggAssets = 'http://assetsengg.us-east-2.elasticbeanstalk.com/services/assetsEngineering';
    console.log('assetsMgmtAppApplication bugs modiefied called');
});
assetsMgmtAppApplication.controller("assetsController", function ($scope) {

    $scope.showTraingForm = function (name) {
        console.log('formName ', name);
        $scope.templateURL = name;
    }

});