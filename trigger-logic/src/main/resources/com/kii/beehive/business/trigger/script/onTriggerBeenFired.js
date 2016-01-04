
function global_onTriggerArrive(params,endpoint,context,done){
 var triggerID=params["triggerID"];

 var thingID=params["thingID"];

 var output={};

 output["triggerID"]=triggerID;
 output["thingID"]=thingID;

 var request=new Global_RemoteKiiRequest(endpoint,context,done);

 request.execute(output,function(){done();});

}

function global_onSimpleTriggerArrive(params,context,done){

	global_onTriggerArrive(params,"simple",context,done);

}

function global_onPositiveTriggerArrive(params,context,done){

	global_onTriggerArrive(params,"positive",context,done);

}

function global_onSummaryTriggerArrive(params,context,done){

	global_onTriggerArrive(params,"summary",context,done);

}


function global_onNegitiveTriggerArrive(params,context,done){

	global_onTriggerArrive(params,"negitive",context,done);


}