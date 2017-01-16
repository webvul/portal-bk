
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
        this.headers['Authorization']='Bearer '+this.adminCtx._token;


		var _this=this;

		this._success = function(json, status) {
        	console.log("success:"+json+" \n status:"+status);
        	_this.done(status);
        };
        this._failure = function(errString, status) {
        	console.log("failure:"+errString+" \n status:"+status);
        	_this.done(status);
        };

	    this.onSuccess=function(anything, textStatus,jqXHR) {
			console.log("request success");

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
			console.log("request fail:"+textStatus+" "+jqXHR.responseText);

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

		console.log("do remote post:"+JSON.stringify(param));

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

		console.log("header:"+JSON.stringify(this.headers));
		console.log("url:"+this.path);

		$.ajax(this.path,ajaxParam);

	}

	Global_RemoteKiiRequest.prototype.execute=function(param,callback){

		if(this.debug==true){
				this.executeToLocal(param,callback);
		}else{
		       	this.executeToRemote(param,callback);

		}
    }


	return Global_RemoteKiiRequest;
})();


function doRemoteCall(context,name,param,done){

	var bucket=context.getAppAdminContext().bucketWithName("beehive_parameters");

	var obj=bucket.createObjectWithID("beehive_callback_url");

	obj.refresh({

	      success: function(theObject) {
				var baseUrl=theObject.get("baseUrl");

				var method=theObject.get(name);

				var request=new Global_RemoteKiiRequest(baseUrl+"/"+method,context,done);

				var debug=theObject.get("debug");
				request.debug=debug;

                request.execute(param,
                 	{
                 		success:function(){
                 			done(theObject);
                 		},
                 		failure:function(errString){
                 			console.log("get state object fail:"+param.objectID);
                            done("remote request fail:"+errString);
                        }
                 	}
                );

          },
          failure: function(theObject, anErrorString) {
            	console.log("get state object fail:"+param.objectID);
             	done(anErrorString);
          }
	})

}

function global_recordLog(done,log,type) {

    console.log("log:"+log+" type:"+type);

    var result={};
    result["log"]=log;
    result["type"]=type;

    if(typeof(done)=="function"){
        done(result);
    }

}


function global_onThingStateChange(params,context,done){

	var bucket=context.getAppAdminContext().bucketWithName("_states");

	var obj=bucket.createObjectWithID(params.objectID);

	obj.refresh({

	      		success: function(theObject) {

//					console.log("status"+JSON.stringify(theObject));

                	doRemoteCall(context,"stateChange",theObject._customInfo,done);

 		         },

          		failure: function(theObject, anErrorString) {
            		 console.log("get state object fail:"+params.objectID);
             		 done(anErrorString);
         		 }
	});

}

function global_onThingOnBoard(params,context,done){

	doRemoteCall(context,"thingCreated",params,done);

}

function global_onThingRemoved(params,context,done){

	doRemoteCall(context,"thingRemoved",params,done);

}