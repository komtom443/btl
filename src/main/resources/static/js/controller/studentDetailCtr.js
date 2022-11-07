angular.module("myApp",[])
    .controller("orderDetailCtr",function($scope,$http,$window)
    {
        $scope.displayOrderData;
        $scope.editOrderData;
        $scope.sudentEdit;
        $scope.btnOrderEdit = fnBtnOrderEdit;
        $scope.btnOrderUpload = fnBtnOrderUpload;
        $scope.btnOrderClose = fnBtnOrderClose;
        
        init()
        function init()
        {
            
            var id = document.getElementById("mode").innerHTML;
            if(id=="1")
            {
                $scope.sudentEdit = 0;
                getData();
            }
            else
            {
                $scope.sudentEdit = 1;
                $scope.displayOrderData = {id:parseInt(document.getElementById("orderID").innerHTML),
                                            name:"",
                                            dob:"2001-01-01",
                                            major: "",
                                            vaccinated: 0};
                $scope.editOrderData = {id:parseInt(document.getElementById("orderID").innerHTML),
                    name:"",
                    dob:"2001-01-01",
                    major: "",
                    vaccinated: 0};
            }
            
        }
        function getData()
        {
            $http({
                method: 'GET',
                url: 'http://localhost:8080/'+document.getElementById("orderID").innerHTML+'_detail'
            }).then(function(resp){
                $scope.displayOrderData =  resp.data;
            })
            $http({
                method: 'GET',
                url: 'http://localhost:8080/'+document.getElementById("orderID").innerHTML+'_detail'
            }).then(function(resp){
                $scope.editOrderData = resp.data;
            })
        }

        function fnBtnOrderEdit()
        {
            $scope.sudentEdit = ($scope.sudentEdit+1)%2;
            getData();
        }

        function fnBtnOrderUpload()
        {
            if($scope.editOrderData.vaccinated == true || $scope.editOrderData.vaccinated == 1)
            {
                $scope.editOrderData.vaccinated = 1; 
            }
            if($scope.editOrderData.vaccinated == false || $scope.editOrderData.vaccinated == 0)
            {
                $scope.editOrderData.vaccinated = 0; 
            }
            var date = moment($scope.editOrderData.dob);
            if(date.isValid()==true)
            {
                var ans = $scope.editOrderData.id+";"+$scope.editOrderData.name+";"+$scope.editOrderData.dob+";"+$scope.editOrderData.major+";"+$scope.editOrderData.vaccinated;
            }
            else
            {
                var ans = $scope.editOrderData.id+";"+$scope.editOrderData.name+";"+";"+$scope.editOrderData.major+";"+$scope.editOrderData.vaccinated;
            }
            
            if(document.getElementById("mode").innerHTML == '0')
            {
                ans+=";1";
            }
            
            
            console.log(ans)
            $http({
                method: 'POST',
                url: 'http://localhost:8080/upload',
                data:ans
            }).then(function(resp){
                var code = resp.data.status;
                if(code == 200)
                {
                    alert("Lưu thành công");
                    $window.location.href = "/";
                }
            })
            console.log(ans);
        }
        
        function fnBtnOrderClose()
        {

        }
    })