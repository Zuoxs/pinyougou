//服务层
app.service('loginService',function($http){
	//读取列表数据绑定到表单中
	this.showName=function(){
		return $http.get('../login/name.do');
	}
	this.findList=function () {
		return $http.get("../collect/findList.do");
    }
    this.findSeckilList=function () {
		return $http.get("../seckill/findSeckillList.do")
    }
	
});