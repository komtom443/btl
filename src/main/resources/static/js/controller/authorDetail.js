var ang =angular.module("myApp",['ngCookies'])
ang.controller("headerCtr",function($scope,$http,$window,$cookies)
{
    $scope.header = 
    {
        categories: undefined,
        authors: undefined,
    }
    init();
    function init()
    {
        $http({
            method  : "GET",
            url     : "http://localhost:8080/api/category",
        }).then(function(resp){
            console.log(resp);
            $scope.header.categories = resp.data;
        })


        $http({
            method  : "GET",
            url     : "http://localhost:8080/api/author",
        }).then(function(resp){
            console.log(resp);
            $scope.header.authors = resp.data;
        })
        console.log($cookies.key);
    }

})