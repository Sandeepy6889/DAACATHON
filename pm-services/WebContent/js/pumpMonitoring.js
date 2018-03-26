var assetsMgmtAppApplication = angular.module("assetsMgmtApp", ["trainingModelApp", "assetsManagementApp", "assetsAnalysisApp", "assetReportApp"]);
assetsMgmtAppApplication.run(function ($rootScope) {
    $rootScope.templateURL = "dashboard.html";
    $rootScope.menus = "menus.html";

    console.log('assetsMgmtAppApplication bugs modiefied called');
});
assetsMgmtAppApplication.controller("assetsController", function ($scope) {

    $scope.showTraingForm = function (name) {
        console.log('formName ', name);
        $scope.templateURL = name;
    }

});