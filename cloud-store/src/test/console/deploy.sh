#!/bin/sh
echo  "[app.info]  [deploy|listSchedule|showLog]  <serviceName>  "

exec_base=./kii-cli/bin

exec=$exec_base/kii-servercode.js

exec_log=$exec_base/kii-logs.js

while read line 
do
	if  [[ "$line" =~ "$1.appID"* ]] 
	then
		id=${line#*=}
	fi
	if  [[ "$line" =~ "$1.appKey"* ]] 
	then
		key=${line#*=}
	fi
	if  [[ "$line" =~ "$1.clientID"* ]] 
	then
		clientID=${line#*=}
	fi
	if  [[ "$line" =~ "$1.secret"* ]] 
	then
		secret=${line#*=}
	fi
	if  [[ "$line" =~ "$1.siteUrl"* ]] 
	then
		site_url=${line#*=}
	elif [[ "$line" =~ "$1.site"* ]] 
	then
		site=${line#*=}
	fi
	if  [[ "$line" =~ "$1.logSiteUrl"* ]]
	then
		logSiteUrl=${line#*=}
	fi

done < kiicloudSite.config

if [ -z ${site_url+x} ]
then
	site_param="--site "$site
else
	site_param="--site-url "$site_url
fi

if  [ "$2" == "deploy" ]
then
	hook=""
	if [ -f "$3.hook" ] &&  [ "$4" !=  "nohook" ]
	then
		hook="--hook-config "$3".hook"  
	fi


	node $exec deploy-file \
       --file $3.js \
       $site_param   \
       --app-id $id  \
       --app-key $key  \
       --client-id $clientID  \
       --client-secret $secret  \
       $hook 
fi

if [ "$2" == "listSchedule" ]
then

	yesterday=` date -j  -v-2d +%Y-%m-%d`

	node $exec list-scheduled-execution \
	  $site_param \
	  --app-id $id \
	  --app-key $key \
	  --client-id $clientID \
	  --client-secret $secret \
	  --from $yesterday
	
fi

if [ "$2"  ==  "showLog" ]
then

    if [ -z ${site_url+x} ]
    then
        site_param="--site "$site
    else

        site_param="--site-url "$logSiteUrl
    fi

    #level=""
	#if [ "$3" ==  "error" ] || ["$3" == "debug" ] || ["$3"=="info"]
	#then
	#	level="--level "$3
	#fi
	
	node $exec_log -t \
	  $site_param \
	  --app-id $id \
	  --app-key $key \
	  --client-id $clientID \
	  --client-secret $secret  \
	#  level
	
fi

if [ "$2" == "maintain" ]
then
	
	node $exec get-hook-config \
	  --code-version fof0dzp306heojvoof931hx13 \
	  $site_param \
	  --app-id $id \
	  --app-key $key \
	  --client-id $clientID \
	  --client-secret $secret 
	
fi
