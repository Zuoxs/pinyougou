// 定义控制器:
app.controller("userCountController",function($scope,$controller,$http,userCountService){
	// AngularJS中的继承:伪继承
	$controller('baseController',{$scope:$scope});


    $scope.userCount = function(){
        // 向后台发送请求:
        userCountService.userCount().success(function(response){
            $scope.entity = response;
        });
    }

});
