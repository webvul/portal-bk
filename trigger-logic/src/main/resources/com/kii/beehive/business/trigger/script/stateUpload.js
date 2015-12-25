

function global_onThingStateChange(params,context,done){


	var bucket=context.getAppAdminContext().bucketWithName("_states");

			var obj=bucket.createObjectWithID(params.objectID);

			obj.refresh({

	      		success: function(theObject) {

              		var request=new Global_RemoteKiiRequest("stateChange",context,done);

              		request.execute(theObject._customInfo,function(){done(theObject);});

          },

          		failure: function(theObject, anErrorString) {
            		 console.log("get state object fail:"+params.objectID);
             		done(anErrorString);
         		 }
			});

}