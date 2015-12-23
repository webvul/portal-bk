
function global_onTriggerArrive(params,endpoint,context,done){
 var triggerID=params.get("triggerID");

 var thingID=params.get("thingID");

 var output={};

 output["triggerID"]=triggerID;
 output["thingID"]=thingID;

 var request=new Global_RemoteKiiRequest(endpoint,context.getAppAdminContext(),done);

 request.execute(output,function(){done();});

}

function global_onPositiveTriggerArrive(params,context,done){

	global_onTriggerArrive(params,"positive",context,done);

}




function global_onNegitiveTriggerArrive(params,context,done){

	global_onTriggerArrive(params,"negitive",context,done);


}