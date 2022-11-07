angular.module("myApp",['ngCookies'])
    .controller("loginCtr",function($scope,$http,$window,$cookies)
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
        $scope.createAccountErrorList = [0,0,0,0];

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
                        console.log(console.log("ERROR"))
                        return;
                    }
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