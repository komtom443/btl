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
ang.controller("indexCtr",function($scope,$http,$window,$cookies)
{
    $scope.key= $cookies.get("sessionID");
    $scope.data = 
    {
        categories: undefined,
        authors: undefined,
        books:undefined
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
            url     : "http://localhost:8080/api/book/",
        }).then(function(resp){
            console.log(resp,"Book");
            $scope.data.books = resp.data;
        })

        $http({
            method  : "GET",
            url     : "http://localhost:8080/api/category",
        }).then(function(resp){
            $scope.data.categories = resp.data;
        })


        $http({
            method  : "GET",
            url     : "http://localhost:8080/api/author",
        }).then(function(resp){
            $scope.data.authors = resp.data;
        })
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