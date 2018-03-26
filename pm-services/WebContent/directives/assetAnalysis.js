var assetsManagementApp = angular.module("assetsAnalysisApp", [])
    .run(function ($rootScope) {
        
    })
assetsManagementApp.directive("assetsAnalysis", function () {
    return {
        restrict: 'EA',
        scope: {
            name: "="
        },
        templateUrl: 'analysis.html',
        controller: function ($scope, $element, Asset, assetService) {
        }

    };
});