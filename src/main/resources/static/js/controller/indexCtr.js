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
    $scope.key = $cookies.get("sessionID");
    $scope.mode = $cookies.get("accountType");
    $scope.passWorldConfirm = "";
    $scope.data = 
    {
        categories: undefined,
        authors: undefined,
        books:undefined,
        image:undefined,
    };
    $scope.searchKey=
    {
        book:"",
        author:"",
        category:"",
    };
    $scope.searchBook = fnSearchBook;
    $scope.delete = fnDelete;
    $scope.deleteBookID = "";

    $scope.toolBoxStyle = {}
    $scope.toolBoxStyle['addButton'] = function()
    {
        return
    }
    $scope.reload = function()
    {
        init();
    }
    $scope.filterAreaClass = fnFilterAreaClass;
    $scope.searchAreaClass = fnSearchAreaClass;
    function fnFilterAreaClass()
    {
        if($scope.mode == 1)
        {
            return 'filter_area_admin'
        }
        return 'filter_area_user'
    }
    
    function fnSearchAreaClass(value,value1)
    {
        return $('#searchBarDropdown').is( ":visible" );
        if($('#searchBarDropdown').is(':visible'))
        {
            return value;
        }
        return value1
    }
    init();
    function init()
    {
        $scope.data = 
        {
            categories: undefined,
            authors: undefined,
            books:undefined,
            image:undefined,
        };
        $scope.searchKey=
        {
            book:"",
            author:"",
            category:"",
        };
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

        $http({
            method  : "GET",
            url     : "http://localhost:8080/api/image",
        }).then(function(resp){
            $scope.data.image = resp.data;
        })

        $http({
            method  : "GET",
            url     : "http://localhost:8080/api/description",
        }).then(function(resp){
            $scope.data.description = resp.data;
            console.log(resp.data);
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
    $scope.warningModal = function(bookID)
    {
        $scope.passWorldConfirm = "";
        $scope.deleteBookID = bookID;
        console.log(bookID);
        $("#deleteWarning").modal('show');
    }
    function fnDelete()
    {
        if($cookies.get("sessionID")== undefined || $cookies.get("sessionID")== "" || $cookies.get("accountType")!= 1)
        {
            alert("Bạn không có quyền xóa!!!");
            return;
        }
        var req =
        {
            key: $cookies.get("sessionID"),
            mode: $cookies.get("mode"),
            passwd: $scope.passWorldConfirm,
            bookID:$scope.deleteBookID,
        }
        $http({
            method  : "POST",
            url     : "http://localhost:8080/api/book/delete",
            data:req
        }).then(function(resp){
            $("#deleteWarning").modal('hide');
            init();
        })
    }
    $scope.bookDetail = function(bookID)
    {
        $window.location.href = "/book/"+bookID;
    }
})
