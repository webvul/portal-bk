

function global_onThingStateChange(params,context,done){

	var bucket=context.getAppAdminContext().bucketWithName("_states");

	var obj=bucket.createObjectWithID(params.objectID);

	obj.refresh({

	      		success: function(theObject) {

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


function global_onThingCommandResponse(params,context,done){



	var bucket=context.getAppAdminContext().bucketWithName("_commands");

	var obj=bucket.createObjectWithID(params.objectID);

	obj.refresh({

	      		success: function(theObject) {

                	doRemoteCall(context,"commandResponse",theObject._customInfo,done);

 		         },

          		failure: function(theObject, anErrorString) {
            		 console.log("get state object fail:"+params.objectID);
             		 done(anErrorString);
         		 }
	});

}
