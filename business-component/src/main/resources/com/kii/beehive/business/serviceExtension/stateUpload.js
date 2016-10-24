

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


function global_onThingCmdResponse(params,context,done){


	var thingID=params.objectScope.thingID;

	var bucket=context.getAppAdminContext().thingWithID(thingID).bucketWithName("_commands");

	var obj=bucket.createObjectWithID(params.objectID);


	obj.refresh({

	      		success: function(theObject) {

					var val=theObject._customInfo;
					var.put("commandID",theObject.getID());
                	doRemoteCall(context,"commandResponse",theObject._customInfo,done);

 		         },

          		failure: function(theObject, anErrorString) {
            		 console.log("get state object fail:"+params.objectID);
             		 done(anErrorString);
         		 }
	});

}
