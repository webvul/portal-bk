

function global_onThingStateChange(params,context,done){


	var adminCtx=context.getAppAdminContext;
	var bucket=	adminCtx.bucketWithName(params.bucketID);

	var object=bucket.createObjectWithID(params.objectID);

	object.refresh({

	      success: function(theObject) {

              var request=new Global_RemoteKiiRequest("stateChange",context,done);

              request.execute(theObject,function(){done();});

          },

          failure: function(theObject, anErrorString) {
             console.log("get state object fail:"+params.objectID);
             done(params);
          }
	});


}