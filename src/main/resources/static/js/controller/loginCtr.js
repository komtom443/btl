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

ang.controller("loginCtr",function($scope,$http,$window,$cookies)
    {
        $scope.account = {
            username:"",
            passwd  :""
        }
        $scope.newAccount = {
            username        :"",
            passwd          :"",
            reEnterPasswd   :""
        } 
        $scope.state = 1;
        $scope.login = fnLogin;
        $scope.create = fnCreate;
        $scope.forget = fnForget;
        $scope.backToLogin = fnBackToLogin;
        $scope.createAccountErrorList = [0,0,0,0,0];

        function fnLogin()
        {
            $http({
                method  : 'POST',
                url     : "http://localhost:8080/loginProcess",
                data    : $scope.account
            }).then(function(req){
                data = req.data;
                console.log(req);
                if(data.login==true)
                {
                    $cookies.put("sessionID",data.key);
                    $cookies.put("accountType",parseInt(data.mode));
                    $window.location.href =("/");
                }
                console.log($cookies.get("sessionID"));
            })
        }

        function fnCreate()
        {
            if($scope.state != 2)
            {
                $scope.state = 2;
            }
            else
            {
                $scope.createAccountErrorList[0] = nullValidate($scope.newAccount.username);
                $scope.createAccountErrorList[1] = nullValidate($scope.newAccount.passwd);
                $scope.createAccountErrorList[2] = nullValidate($scope.newAccount.reEnterPasswd);
                console.log($scope.newAccount.passwd);
                if($scope.newAccount.passwd.match(/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$/) == null)
                {
                    console.log("Match Passwd")
                    $scope.createAccountErrorList[1] = 1;
                }
                if($scope.newAccount.passwd != $scope.newAccount.reEnterPasswd)
                {
                    $scope.createAccountErrorList[3] = 1;
                }
                for(var i = 0; i < $scope.createAccountErrorList.length;i++)
                {
                    if($scope.createAccountErrorList[i] == 1)
                    {
                        console.log(console.log("ERROR",i))
                        return;
                    }
                    $scope.createAccountErrorList[i] = 0;
                }
                var data = {
                    username        :$scope.newAccount.username,
                    passwd          :$scope.newAccount.passwd,
                    mode            :0
                }
                $http({
                    method  : "POST",
                    url     : "http://localhost:8080/create_account",
                    data    : data
                }).then(function(resq)
                {
                    console.log(resq) 
                    $scope.createAccountErrorList[4] == 0;
                    if(resq.data.login == true)
                    {
                        alert("Tạo tài khoản thành công");
                    }
                    else
                    {
                        if(resq.data.error == 1)
                        {
                            $scope.createAccountErrorList[4] = 1;
                        }
                    }
                })
            }
        }

        function fnForget()
        {
            $scope.state = 3;
        }

        function fnBackToLogin()
        {
            $scope.state = 1;
        }
        function nullValidate(inputString)
        {
            if(inputString == "" || inputString == undefined)
            {
                return 1;
            }
            return 0;
        }
    })