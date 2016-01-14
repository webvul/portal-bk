
function global_onTriggerArrive(params,endpoint,context,done){
 var triggerID=params["triggerID"];

 var thingID=params["thingID"];

 var output={};

 output["triggerID"]=triggerID;
 output["thingID"]=thingID;


 doRemoteCall(context,endpoint,params,done);

}

function global_onSimpleTriggerArrive(params,context,done){

	global_onTriggerArrive(params,"simple",context,done);

}

function global_onPositiveTriggerArrive(params,context,done){

	global_onTriggerArrive(params,"positive",context,done);

}


function global_onNegativeTriggerArrive(params,context,done){

	global_onTriggerArrive(params,"negative",context,done);


}