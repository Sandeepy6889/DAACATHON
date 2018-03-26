var assetsManagementApp = angular.module("assetReportApp", [])
    .run(function ($rootScope) {
        
    })
assetsManagementApp.directive("assetReport", function () {
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