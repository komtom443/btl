var ang =angular.module("myApp",['ngCookies'])

ang.controller("profileCtr",function($scope,$http,$window,$cookies)
{
    $scope.key= $cookies.get("sessionID");
    $scope.account;
    $scope.mode = $cookies.get("accountType");
    $scope.accountType = $cookies.get("accountType");
    $scope.cart=[];
    $scope.originalCart=[];
    $scope.adminTab = {data:[{name:"Thông tin tài khoản",value:true,index:1,visible:1},{name:"Phân quyền",value:true,index:2},{name:"Lịch sử hoạt động",value:true,index:3},{name:"Giỏ hàng",value:true,index:4,visible:1}],
        currentTab:4,
        changeTab: function(index)
        {
            console.log(index);
            this.currentTab = index;
        },
        currentTabStyle:function(index){
            if(this.currentTab == index)
            {
                return {"background-color":"white",
                        "border-top-left-radius": "6px",
                        "border-top-right-radius": "6px",
                        "color":"black",}
            }
        }};
    $scope.profileEditToggle = [0,0,0,0];
    $scope.btnProfileEditToggle = fnBtnProfileEditToggle;
    $scope.saveChange = fnSaveChange;
    $scope.btnEditStateChange = fnBtnEditStateChange;
    $scope.btnSaveAll = fnBtnSaveAll;
    accountDetail();
    getCart();
    function fnBtnSaveAll()
    {
        fnSaveChange('name',$scope.account.userData.name,0,0);
        fnSaveChange('dob',$scope.account.userData.dob,1,0);
        fnSaveChange('email',$scope.account.userData.email,2,0);
    }
    function fnBtnEditStateChange(value)
    {
        for(var i = 0; i < $scope.profileEditToggle.length;i++)
        {
            $scope.profileEditToggle[i] = value;
        }
    }
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
                resp.data.userData.dob = new Date(resp.data.userData.dob);
                $scope.account = resp.data;
                console.log(resp.data)
            }
        })
    }
    function getCart()
    {
        if($scope.key == undefined)
        {
            $window.location.href = "/";
        }
        console.log()
        $http({
            method  : "POST",
            url     : "http://localhost:8080/api/cart/",
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

                for(var i = 0; i < resp.data.length;i++)
                {
                    resp.data[i].isEdited = false;
                }
                $scope.cart = resp.data;
                $scope.originalCart = angular.copy(resp.data);
                console.log(resp.data)
            }
        })
    }
    function fnSaveChange(attribute,value1,index,mode)
    {
        var value = angular.copy(value1);
        console.log(attribute,value);
        if($scope.key == undefined)
        {
            $window.location.href = "/";
        }
        if(attribute == 'dob')
        {
            var tmp = value.getFullYear()+'-'+(value.getMonth()+1)+'-'+value.getDate();
            value = tmp;
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
                if(mode == 1)
                {
                    alert("Thay đổi thành công");   
                }
                $scope.profileEditToggle[index] = 0;
            }
            else
            {
                if(mode == 1)
                {
                    alert("Thay đổi thất bại");
                }
            }
        })
    }
    $scope.deleteCart = function(record)
    {
        if($scope.key == undefined)
        {
            $window.location.href = "/";
        }
        console.log()
        $http({
            method  : "POST",
            url     : "http://localhost:8080/api/cart/delete",
            data    :
            {
                key:$scope.key,
                value: record.id +""
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
                getCart();
            }
        })
    }
    $scope.cartUpload = function(record)
    {
        var req=
        {
            key: $cookies.get("sessionID"),
            bookID: record.book.id,
            value: Math.ceil(record.value)+'',
            mode: '1'
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
                record.isEdited = false;
                getCart();
            }
        })
    }
    $scope.editCart = function(record,index)
    {
        if(record.isEdited == true)
        {
            record.value = angular.copy($scope.originalCart[index].value); 
            return record.isEdited = false;
        }
        return record.isEdited = true;
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