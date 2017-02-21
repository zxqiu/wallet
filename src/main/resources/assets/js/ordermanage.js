/*
	ordermanage.js binding with ordermanage app
	author lucifer.yqh
	contact lucifer.yqh@gmail.com
*/

var orderManage = angular.module('orderManage', ['ngRoute'])

	.config(function ($routeProvider) {
		$routeProvider
			.when('/', {	// URL TBChange
				templateUrl: 'views/home.html'
			})
			.when('/address', {
				templateUrl: 'views/address.html'
			})
			.otherwise({
				redirectTo: '/'
			})
	})

orderManage.controller("itemselect", function ($scope, $location) {
	$scope.text = $location.absUrl();
});
