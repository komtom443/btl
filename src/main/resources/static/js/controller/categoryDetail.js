var ang =angular.module("myApp",['ngCookies'])

ang.controller("categoryCtr",function($scope,$http,$window,$cookies)
{
    $scope.key= $cookies.get("sessionID");
    $scope.state = parseInt(document.getElementById("state").innerHTML);
    $scope.error = 0;
    $scope.quickPick = {name:'',data:[]};
    $scope.data = 
    {
        categories: undefined,
        authors:    undefined,
        books:      undefined,
        title:      undefined,
    };
    $scope.searchKey=
    {
        book:"",
        author:"",
        category:"",
    };
    $scope.searchBook = fnSearchBook;
    init();
    function init()
    {
        $http({
            method  : "GET",
            url     : "http://localhost:8080/api/category",
        }).then(function(resp){
            $scope.data.categories = resp.data;
            if($scope.state == 1){
                $scope.quickPick.name = 'category'
                $scope.quickPick.data = $scope.data.categories
            }
        })


        $http({
            method  : "GET",
            url     : "http://localhost:8080/api/author",
        }).then(function(resp){
            $scope.data.authors = resp.data;
            if($scope.state == 0){
                $scope.quickPick.name = 'author'
                $scope.quickPick.data = $scope.data.authors
            }
        })


        if($scope.state == 1)
        {
            
            $scope.searchKey.category = document.getElementById("categoryID").innerHTML;
            $http({
                method  : "GET",
                url     : "http://localhost:8080/api/book/get/category/"+$scope.searchKey.category,
            }).then(function(resp){
                if(resp.data.status == 1)
                {
                    $scope.error = 1;
                }
                else
                {
                    console.log(resp,"Book");
                    $scope.data.books = resp.data;
                }
            })

            $http({
                method  : "GET",
                url     : "http://localhost:8080/api/category/"+$scope.searchKey.category,
            }).then(function(resp){
                $scope.title = resp.data;
            })
        }
        else
        {
            $scope.searchKey.author = document.getElementById("authorID").innerHTML;
            $http({
                method  : "GET",
                url     : "http://localhost:8080/api/book/get/author/"+$scope.searchKey.author,
            }).then(function(resp){
                if(resp.data.status == 1)
                {
                    $scope.error = 1;
                }
                else
                {
                    console.log(resp,"Book");
                    $scope.data.books = resp.data;
                }
            })

            $http({
                method  : "GET",
                url     : "http://localhost:8080/api/author/"+$scope.searchKey.author,
            }).then(function(resp){
                $scope.title = resp.data;
            })
        }
    }

    function fnSearchBook(book)
    {
        var authorSearch = false;
        if(book.name.toLowerCase().search($scope.searchKey.book.toLowerCase()) == -1)
        {
            return false
        }
        if(book.category.id.toLowerCase().search($scope.searchKey.category.toLowerCase()) == -1)
        {
            return false
        }
        for(var i = 0; i < book.authors.length;i++)
        {
            if(book.authors[i].id.search($scope.searchKey.author) != -1)
            {
                authorSearch = true;
            }
        }
        return authorSearch;
    }
})

ang.controller("headerCtr",function($scope,$http,$window,$cookies)
{
    $scope.key= $cookies.get("sessionID");
    console.log($scope.key);
    $scope.header = 
    {
        categories: undefined,
        authors: undefined,
    }
    $scope.toProfile= fnToProfile;
    $scope.logout = fnLogout;
    
    function fnToProfile()
    {
        $http({
            method  : "GET",
            url     : "http://localhost:8080/profile",
        })
    }

    function fnLogout()
    {
        $http({
            method  : "POST",
            url     : "http://localhost:8080/logout",
            data    : 
            {
                key:$cookies.get("sessionID")
            }
        })
        $cookies.remove("sessionID");
        $window.location.href = "/";
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