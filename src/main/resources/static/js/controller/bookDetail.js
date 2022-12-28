var ang =angular.module("myApp",['ngCookies'])

ang.controller("bookDetailCtr",function($scope,$http,$window,$cookies){
    $scope.data = undefined;
    $scope.uploadButtonTurnOn = false;
    $scope.key = $cookies.get("sessionID");
    $scope.accountType = $cookies.get("accountType");
    $scope.bookID= document.getElementById("bookID").innerHTML;
    $scope.mode = document.getElementById("mode").innerHTML;
    $scope.bookEdit = [0,0,0,0,0,0,0];
    $scope.rating = [];
    $scope.bookEditError = {};
    $scope.authors = {data:{},authorID:{},idArray:[]};
    $scope.categories = {};
    $scope.comments = [];
    $scope.myComment="";
    $scope.editModeEnable = fnEditModeEnable;
    $scope.btnEditAuthor = fnBtnEditAuthor;
    $scope.btnAuthorDetail = fnBtnAuthorDetail;
    $scope.btnShowAuthorList = fnBtnShowAuthorList;
    $scope.editAuthorSearch = {value:"",search:fnEditAuthorSearch,new:1};
    $scope.addTempAuthor = fnAddTempAuthor;
    $scope.btnSaveAuthor = fnBtnSaveAuthor;
    $scope.editCategoryEnable = fnEditCategoryEnable;
    $scope.chooseImage = fnChooseImage;
    $scope.create = fnCreate;
    $scope.attributeStyle = fnAttributeStyle;
    $scope.imageURL = 
    {
        currentData: '',
        editData: ''
    };
    $scope.upload = {};
    $scope.bookData = 
    {
        currentData:undefined,
        editData:undefined,
    }
    $scope.upload['descprition'] = function()
    {
        var req  = {
            key : $cookies.get("sessionID"),
            data: $scope.bookData.editData.descprition
        }
        $http({
            method  : "POST",
            url     : "http://localhost:8080/api/book/"+document.getElementById("bookID").innerHTML+"/description/upload",
            data: req
        }).then(function(resp){
            console.log(resp.data);
            if(resp.data.status == false)
            {
                $cookies.remove("sessionID");
                $window.location.href = "/";
            }
            else
            {
                $scope.bookData.currentData.descprition = angular.copy($scope.bookData.editData.descprition);
                $scope.editModeEnable(5);
            }

        })
    }
    function resetData(index)
    {
        if(index == 0)
        {
            $scope.bookData.editData.name = angular.copy($scope.bookData.currentData.name);
        }
        if(index == 1)
        {
            $scope.bookData.editData.date = angular.copy($scope.bookData.currentData.date);
        }
        if(index == 2)
        {
            $scope.bookData.editData.page = angular.copy($scope.bookData.currentData.page);
        }
        if(index == 5)
        {
            $scope.bookData.editData.descprition = angular.copy($scope.bookData.currentData.descprition);
        }
    }
    
    function fnEditModeEnable(index)
    {
        if($scope.bookEdit[index] == 1)
        {
            resetData(index);
        }
        if(index == 5)
        {
            if($scope.bookEdit[index] == 0)
            {
                $('#describeUI').collapse('show');
            }
        }
        $scope.bookEdit[index] = ($scope.bookEdit[index] + 1) % 2;
    }
    function fnEditCategoryEnable(mode)
    {
        if(mode == 0)
        {
            $scope.bookData.currentData.category = angular.copy($scope.categories[$scope.bookData.editData.category.id]);
            console.log($scope.bookData.currentData);
            if($scope.bookData.currentData.category == undefined)
            {
                $scope.bookEditError['noCategory'] = 1;
                return;
            }
            $scope.upload['attribute']('category',$scope.bookData.currentData.category.id);
        }
        $scope.bookData.editData.category = angular.copy($scope.bookData.currentData.category);
        $scope.bookEdit[4] = ($scope.bookEdit[4] + 1) % 2;
    }
    init();
    function init()
    {   
        $('#describeUI').collapse('show');
        $('#authorUI').collapse('show');
        $('#imageUI').collapse('show');
        if($scope.mode == 0)
        {
            $http({
                method  : "GET",
                url     : "http://localhost:8080/api/book/"+document.getElementById("bookID").innerHTML,
            }).then(function(resp){
                console.log(resp,"book");
                resp.data.date = new Date(resp.data.date);
                $scope.bookData.currentData = angular.copy(resp.data);
                $scope.bookData.editData = angular.copy(resp.data);
                getAuthorList()
            })
            getRate();
        }
        else
        {
            $http({
                method  : "POST",
                url     : "http://localhost:8080/api/checkSessionID",
                data    :   {key:$cookies.get("sessionID")}
                
            }).then(function(resp){
                if(resp.data.status == false)
                {
                    $window.location.href = "/";
                }
            })
            var data = 
            {
                authors:[],
                category:{
                    id:"",
                    name:""
                },
                date: new Date(),
                id:$scope.bookID,
                name:$scope.bookID,
                page:1,
            }
            $scope.bookData.currentData = angular.copy(data);
            $scope.bookData.editData = angular.copy(data);
            getAuthorList()
        }
        $http({
            method  : "GET",
            url     : "http://localhost:8080/api/category",
        }).then(function(resp){
            //console.log(resp);
            for(var i = 0; i < resp.data.length;i++)
            {
                $scope.categories[resp.data[i].id] = resp.data[i];
            }
        })
        getImg();
        getComment();
        getRating();
        $("#editAuthorUIOff").collapse("show");
    }
    $scope.enterCheck = function(event){
        console.log(document.getElementById("commentBox").value);
        if(event.which == 13 && !event.shiftKey)
        {
            uploadComment();
        }
    }
    function getRate()
    {
        $http({
            method  : "GET",
            url     : "http://localhost:8080/api/book/"+document.getElementById("bookID").innerHTML+"/rate",
        }).then(function(resp){
            $scope.rate = resp.data;
            $scope.rate.detail.reverse();
            console.log($scope.rate,"rate");
        })
    }
    function getImg()
    {
        $http({
            method  : "GET",
            url     : "http://localhost:8080/api/image/"+document.getElementById("bookID").innerHTML+"",
        }).then(function(resp){
            resp = resp.data.data;
            if(resp != "")
            {
                $scope.deleteImgBtn = false;
                $scope.imageURL.currentData = resp;
                $scope.imageURL.editData = resp;
            }
        })
    }
    function getComment()
    {
        console.log(document.getElementById("bookID").innerHTML);
        $http({
            method  : "GET",
            url     : "http://localhost:8080/api/book/"+document.getElementById("bookID").innerHTML+"/comment",
        }).then(function(resp){
            resp.data.reverse();
            $scope.comments = resp.data;
        })
    }
    function uploadComment()
    {
        var req = {
            key: $scope.key,
            data: document.getElementById("commentBox").value,
        };
        console.log($scope.myComment);
        $http({
            method  : "POST",
            url     : "http://localhost:8080/api/book/"+document.getElementById("bookID").innerHTML+"/comment/upload",
            data    :  req
        }).then(function(resp){
            if(resp.data.status == false)
            {
                $cookies.remove("accountType");
                $cookies.remove("sessionID");
                alert("Vui lòng đăng nhập lại");
                $window.location.href = "/";
            }
            else
            {
                $scope.myComment = "";
            }
            document.getElementById("commentBox").value = "";
            getComment();
        })
    }
    function getAuthorList()
    {
        $http({
            method  : "GET",
            url     : "http://localhost:8080/api/author",
        }).then(function(resp){
            console.log(resp);
            $scope.authors.authorNumber = resp.data.length;
            for(var i = 0; i < resp.data.length;i++)
            {
                $scope.authors.data[resp.data[i].id] = angular.copy(resp.data[i]) 
                $scope.authors.authorID[resp.data[i].id] = false;
                $scope.authors.idArray.push(resp.data[i].id);
            }
            for(var i = 0; i < $scope.bookData.currentData.authors.length;i++)
            {
                $scope.authors.authorID[$scope.bookData.currentData.authors[i].id] = true;
            }
        })

        $http({
            method  : "GET",
            url     : "http://localhost:8080/api/book/"+document.getElementById("bookID").innerHTML+"/description",
        }).then(function(resp){
            console.log(resp);
            $scope.bookData.editData.descprition = angular.copy(resp.data.data);
            $scope.bookData.currentData.descprition = angular.copy(resp.data.data);
        })
    }
    function fnBtnEditAuthor()
    {
        editAuthorToggle();
    }
    function fnBtnAuthorDetail(authorID)
    {
        $window.open("http://localhost:8080/author/"+authorID);
    }
    function fnBtnShowAuthorList()
    {
        $("#editAuthorUIOn").collapse("toggle");
    }
    function fnEditAuthorSearch(authorName)
    {
        if(authorName.toLowerCase().search($scope.editAuthorSearch.value.toLowerCase()) == -1)
        {
            return false
        }
        return true;
    }
    function fnAddTempAuthor()
    {
        $scope.bookEditError['blankNewAuthor'] = 1;
        if($scope.editAuthorSearch.value != "")
        {
            $scope.bookEditError['blankNewAuthor'] = 0;
            $scope.authors.data["new"+$scope.editAuthorSearch.new] = {id:"new"+$scope.editAuthorSearch.new,name:angular.copy($scope.editAuthorSearch.value),email:null,count:0};
            $scope.authors.authorID["new"+$scope.editAuthorSearch.new] = true;
            $scope.authors.idArray.push("new"+$scope.editAuthorSearch.new);
            $scope.editAuthorSearch.new += 1;
            console.log($scope.authors);
            return;
        }
    }
    function fnBtnSaveAuthor()
    {
        $scope.bookEditError['noAuthor'] = 0;
        var req =
        {
            key: $cookies.get("sessionID"),
            author:"",
            newAuthor:""
        };
        var tmp = [];
        var tmp1 = 0;
        for(var i = 0; i <$scope.authors.idArray.length;i++)
        {   
            if($scope.authors.authorID[$scope.authors.idArray[i]] == 1)
            {
                tmp.push($scope.authors.data[$scope.authors.idArray[i]]);
                if($scope.authors.idArray[i].search("new")!= -1)
                {
                    req.newAuthor += ";" + $scope.authors.data[$scope.authors.idArray[i]].name;
                }
                else
                {
                    req.author +=";"+angular.copy($scope.authors.idArray[i]);
                }
                tmp1 = 1
            };
            //console.log($scope.authors.idArray,i);
        }
        if(tmp1 == 0)
        {
            $scope.bookEditError['noAuthor'] = 1;
            return;
        }
        console.log($scope.authors.idArray,"idArray");
        req.author = req.author.substring(1);
        req.newAuthor =req.newAuthor.substring(1); 
        
        console.log("request",req);
        
        $http({
            method  : "POST",
            url     : "http://localhost:8080/api/book/"+$scope.bookData.currentData.id+"/updateAuthors",
            data    :req
            
        }).then(function(resp){
            console.log(resp.data.status);
            if(resp.data.status == false)
            {
                $cookies.remove("accountType");
                $cookies.remove("sessionID");
                $window.location.href = "/";
            }
            console.log(tmp);
        })
        $scope.bookData.currentData.authors = []
        $scope.bookData.currentData.authors = angular.copy(tmp);
        editAuthorToggle()
    }
    function editAuthorToggle()
    {
        if($scope.bookEdit[3] == 1)
        {
            $("#editAuthorUIOn").collapse("hide");
            $("#editAuthorUIOff").collapse("show");
        }
        else
        {
            $("#editAuthorUIOn").collapse("show");
            $("#editAuthorUIOff").collapse("hide");
            $scope.editAuthorSearch.value = "";
        }
        $scope.bookEdit[3] = ($scope.bookEdit[3] + 1) % 2;
    } 
    $scope.upload['attribute'] = function(attribute,value,index)
    {
        if(attribute == 'date')
        {
            var ad = "";
            if(value.getMonth() < 10)
            {
                ad += "0";
            }
            ad += (value.getMonth()+1)+"/";
            if(value.getDate() < 10)
            {
                ad += "0";
            }
            ad += value.getDate()+"/"+value.getFullYear()
            console.log(ad);
            value = ad;
        }
        var req =
        {
            key: $cookies.get("sessionID"),
            attribute: attribute,
            data: value+""
        }
        $http({
            method  : "POST",
            url     : "http://localhost:8080/api/book/"+$scope.bookData.currentData.id+"/attribute/upload",
            data    :req
            
        }).then(function(resp){
            if(resp.data.status == false)
            {
                $cookies.remove("accountType");
                $cookies.remove("sessionID");
                alert("Vui lòng đăng nhập lại");
                $window.location.href = "/";
            }
            else
            {
                alert("Cập nhật "+attribute+" thành công");
                $scope.bookEdit[index] = 0;
            }
        })
    }
    $scope.ratingUpload = function(index)
    {
        var req =
        {
            key: $cookies.get("sessionID"),
            data: ((parseFloat(index) + 1)/2)+""
        }
        console.log(req);
        $http({
            method  : "POST",
            url     : "http://localhost:8080/api/book/"+$scope.bookData.currentData.id+"/rate/upload",
            data    :req
            
        }).then(function(resp){
            if(resp.data.status == false)
            {
                $cookies.remove("accountType");
                $cookies.remove("sessionID");
                alert("Vui lòng đăng nhập lại");
                $window.location.href = "/";
            }
            else
            {
                getRate();
            }
        })
    }
    function fnChooseImage(file)
    {
        console.log("Image choose");
        var input = file.target;
        var reader = new FileReader();
        reader.onload = function(){
            $scope.imageURL.editData = reader.result;
            $scope.uploadButtonTurnOn = true;
            $scope.$apply();
        };
        reader.readAsDataURL(input.files[0]);
    }

    $scope.upload['image'] = function()
    {
        if($scope.imageURL.editData == '')
        {
            $scope.bookEditError['noImg'] = 1;
            return;
        }
        var req = {
            key: $cookies.get("sessionID"),
            fileType: angular.copy($scope.imageURL.editData).split("/")[1].split(";")[0],
            fileData: angular.copy($scope.imageURL.editData).split(",")[1]//.substring(4),
        }
        console.log(req);
        $http({
            method  : "POST",
            url     : "http://localhost:8080/api/book/"+$scope.bookData.currentData.id+"/image/upload",
            data    :req
            
        }).then(function(resp){
            if(resp.data.status == false)
            {
                $cookies.remove("accountType");
                $cookies.remove("sessionID");
                alert("Vui lòng đăng nhập lại");
                $window.location.href = "/";
            }
            alert("Cập nhật ảnh thành công");
        })
    }
    function fnCreate()
    {
        var validAuthor = 0;
        console.log($scope.authors.idArray.length);
        for(var i = 0; i <$scope.authors.idArray.length;i++)
        {
            if($scope.authors.authorID[$scope.authors.idArray[i]] == 1)
            {
                validAuthor = 1;
                break;
            }
        }
        if(validAuthor == 0)
        {
            alert("Vui lòng chọn ít nhất 1 tác giả");
            $('#authorUI').collapse('show');
            $scope.bookEdit[3] = 1;
            $("#describeUI").collapse("hide");
            $("#editAuthorUIOn").collapse("show");
            $("#editAuthorUIOff").collapse("hide");
            $scope.bookEditError['noAuthor'] = 1;
            return;
        }
        console.log($scope.bookData.currentData.category);
        if($scope.bookData.currentData.category.id == "")
            {
                alert("Vui lòng chọn 1 chủ đề")
                $scope.bookEditError['noCategory'] = 1;
                return;
            }
        var req=
        {
            key: $cookies.get("sessionID"),
            bookID: document.getElementById("bookID").innerHTML,
        }
        $http({
            method  : "POST",
            url     : "http://localhost:8080/api/book/create",
            data    :req
            
        }).then(function(resp){
            if(resp.data.status == false)
            {
                $cookies.remove("sessionID");
                $cookies.remove("accountType");
                alert("Vui lòng đăng nhập lại");
            }
            alert("Cập nhật ảnh thành công");
            $window.location.href = "/";
        })
    }
    function fnAttributeStyle()
    {
        
    }
    function getRating()
    {
        for(var i = 0; i < 10;i++)
        {
            $scope.rating.push({active:false});
        }
    }
    $scope.ratingBoxStyle = function(index)
    {
        var style={}
        if(index % 2 == 0)
        {
            style['border-right'] = '0px';
        }
        else
        {
            style['border-left'] = '0px';
        }
        if($scope.rating[index].active == true)
        {
            style['background-color'] = 'gold';
            return style;
        }
        style['background-color'] ='rgba(0,0,0,0.18)';
        return style;
    }
    $scope.ratingBoxHover = function(index)
    {
        for(var i = 0; i <= index;i++)
        {
            $scope.rating[i].active = true;
            
        }
        for(var i = index+1; i <$scope.rating.length ;i++)
        {
            $scope.rating[i].active = false;
        }
    }
    $scope.ratingScore = function()
    {
        var score= 0,scoreStr = "";
        for(var i = 0; i <$scope.rating.length ;i++)
        {
            if($scope.rating[i].active == true)
            {
                score +=1;
            }
        }
        scoreStr = (score / 2) +"";
        if(score % 2 == 0)
        {
            scoreStr +=".0";
        }
        return scoreStr+"/5.0";
    }
    $scope.adminImg = function()
    {
        if($scope.accountType == '1')
        {
            return 'adminImg';
        }
        return '';
    }
    $scope.deleteImg = function()
    {
        var req=
        {
            key: $cookies.get("sessionID"),
            bookID: document.getElementById("bookID").innerHTML,
        }
        $http({
            method  : "POST",
            url     : "http://localhost:8080/api/book/delete/image",
            data    :req
            
        }).then(function(resp){
            if(resp.data.status == false)
            {
                $cookies.remove("sessionID");
                $cookies.remove("accountType");
                alert("Vui lòng đăng nhập lại");
                $window.location.href = "/";
            }
            else
            {
                alert("Cập nhật ảnh thành công");
                getImg();
            }
            
        })
    }
    $scope.ratingDefault = [0,1,2,3,4]
    $scope.ratingDisplay = 
    {
        value1:[
        [1,1,1,1,1],[1,1,1,1,0.5],
        [1,1,1,1,0],[1,1,1,0.5,0],
        [1,1,1,0,0],[1,1,0.5,0,0],
        [1,1,0,0,0],[1,0.5,0,0,0],
        [1,0,0,0,0],[0.5,0,0,0,0],
        ],
        value2:[0,1,2,3,4],
        value3: function(index)
        {
            var sum = 0;
            for(var i = 0; i < index.length;i++)
            {
                sum += index[i];
            }
            return sum;
        }   
    }
    $scope.cartValue = 1;
    $scope.cartUpload = function()
    {
        var req=
        {
            key: $cookies.get("sessionID"),
            bookID: document.getElementById("bookID").innerHTML,
            value: $scope.cartValue+''
        }
        console.log(req);
        $http({
            method  : "POST",
            url     : "http://localhost:8080/api/cart/new/",
            data    :req
            
        }).then(function(resp){
            if(resp.data.status == false)
            {
                $cookies.remove("sessionID");
                $cookies.remove("accountType");
                alert("Vui lòng đăng nhập lại");
                $window.location.href = "/";
            }
            else
            {
                alert("Đặt hàng hành công");
            }
        })
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