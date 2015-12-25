Global_RemoteKiiRequest = (function(){

	function Global_RemoteKiiRequest(endpoint,context,done){

		this.adminCtx=context.getAppAdminContext();
		this.done=done;

	    this.path =endpoint;

	    this.method = "POST";
	    this.headers = {
	      "accept": "*/*"
	    };


	    this.headers['x-kii-appid'] = context.getAppID();
        this.headers['Content-Type']= "application/json";
        this.headers['Authorization']='Bearer '+context.getAccessToken();


		var _this=this;

		this._success = function(json, status) {
        	console.log("success:"+json+" \n status:"+status);
        };
        this._failure = function(errString, status) {
        	console.log("failure:"+errString+" \n status:"+status);
        };

	    this.onSuccess=function(anything, textStatus,jqXHR) {

	        if ((200 <= (_ref1 = jqXHR.status) && _ref1 < 400)) {
	            if (jqXHR.status==204) {
	              return _this._success(null, textStatus);
	            } else {
					if (anything.errorCode != null) {
					  var errString=anything.errorCode+anything.message;
					  return _this._failure(errString, jqXHR.status, anything.errorCode);
	                } else {
	                  return _this._success(anything, textStatus);
	                }
	            }
	        } else {
	            var errString = xhr.status + " : " + _this._path;
	            var json = decodeURIComponent(jqXHR.responseText);

	            return _this._failure(errString, jqXHR.status,resp);
	       }
	    };

		this.onError=function(jqXHR,textStatus,errorThrown){
            var errString = textStatus + " : "  + _this._path;
            var resp = decodeURIComponent(jqXHR.responseText);

            return _this._failure(errString, jqXHR.status,resp);
       };

   	}



   	Global_RemoteKiiRequest.prototype.executeToLocal=function(param,callback){

	    var bucket=this.adminCtx.bucketWithName("trigger_result");

		var object = bucket.createObject();

		object.set("param",param);
		object.set("endpoint",this.path);


		var _this=this;
		object.save({
           success:function(obj){
               callback(obj);
           },
           failure:function(anErrorString){
               global_recordLog(_this.done,anErrorString,"save trigger result fail");
           }
		});

   	}

	Global_RemoteKiiRequest.prototype.executeToRemote=function(param,callback){

		if(callback["success"]!=null){
			this._success=callback["success"];
		}
		if((callback["failure"]!=null)){
			this._failure=callback["failure"];
		}

		var ajaxParam={};
		ajaxParam["success"]=this.onSuccess;
        ajaxParam["error"]=this.onError;

		ajaxParam["type"]=this.method;
		ajaxParam["headers"]=this.headers;
		ajaxParam["data"]=JSON.stringify(param);

		$.ajax("http:///"+this.path,ajaxParam);

	}

	Global_RemoteKiiRequest.prototype.execute=function(param,callback){
       	this.executeToLocal(param,callback);
    }


	return Global_RemoteKiiRequest;
})();


function global_recordLog(done,log,type) {

    console.log("log:"+log+" type:"+type);

    if(typeof(done)=="function"){
        done(result);
    }

}
