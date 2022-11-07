var ang =angular.module("myApp",['ngCookies'])
ang.controller("headerCtr",function($scope,$http,$window,$cookies)
{
    $scope.key= $cookies.get("sessionID");
    console.log($scope.key);
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
        
    }

})

ang.controller("profileCtr",function($scope,$http,$window,$cookies)
{
    $scope.key= $cookies.get("sessionID");
    $scope.account;
    $scope.profileEditToggle = [0,0,0,0];
    $scope.btnProfileEditToggle = fnBtnProfileEditToggle;
    $scope.saveChange = fnSaveChange;
    accountDetail();
    function fnBtnProfileEditToggle(a)
    {
        $scope.profileEditToggle[a] = ($scope.profileEditToggle[a] + 1) % 2;
    }
    function accountDetail()
    {
        if($scope.key == undefined)
        {
            $window.location.href = "/";
        }
        console.log()
        $http({
            method  : "POST",
            url     : "http://localhost:8080/api/accountDetail",
            data    :
            {
                key:$scope.key
            }
        }).then(function(resp){
            console.log(resp.data);
            if(resp.data.status == false)
            {
                $cookies.remove("sessionID");
                $window.location.href = "/";
            }
            else
            {
                $scope.account = resp.data;
            }
        })
    }

    function fnSaveChange(attribute,value,index)
    {
        console.log(attribute,value);
        if($scope.key == undefined)
        {
            $window.location.href = "/";
        }
        $http({
            method  : "POST",
            url     : "http://localhost:8080/api/update/user/attribute",
            data    :
            {
                key:$scope.key,
                attribute: attribute,
                value: value,
                userid: $scope.account.userData.id
            }
        }).then(function(resp){
            console.log(resp.data);
            if(resp.data.status == false)
            {
                $cookies.remove("sessionID");
                $window.location.href = "/";
            }
            if(resp.data.success == true)
            {
                alert("Thay đổi thành công");
                $scope.profileEditToggle[index] = 0;
            }
            else
            {
                alert("Thay đổi thất bại");
            }
        })
    }
})