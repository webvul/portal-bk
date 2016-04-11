package com.kii.beehive.portal.common.utils;

public class StringRandomTools {


	//0-9  48-57
	//a-z  64-90
	//A-Z  97-122


	public static String getRandomStr(int size){


		char[] chs=new char[size];

		for(int i=0;i<size;i++){

			Math.random();

			int value= (int) (Math.random()*62);

			int result=value;
			if(value<10) {
				result+='0';
			}else if(value<37){
				result+=('A'-10);
			}else{
				result+=('a'-37);
			}

			chs[i]=(char)result;

		}


		return  String.copyValueOf(chs);
	}
}
