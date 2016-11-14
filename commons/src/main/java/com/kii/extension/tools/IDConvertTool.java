package com.kii.extension.tools;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;

import com.google.common.base.Charsets;

public class IDConvertTool {



	private static final byte[] MASK=StringUtils.repeat("This这是Is包含A中文Mask掩码",10).getBytes(Charsets.UTF_8);

	private static final long OfferBase=11111111L;

	public static final  String encode(long id){

		id-=OfferBase;

		String idStr = Long.toUnsignedString(id,7);

		int length=idStr.length();

		String expendStr= StringUtils.repeat(StringUtils.reverse(idStr),"8",10);

		String trimStr=StringUtils.substring(expendStr,13,13+42);

		byte[] bytes1=trimStr.getBytes(Charsets.UTF_8);

		byte[] result=new byte[bytes1.length];

		for(int i=0;i<bytes1.length;i++){
			int a=bytes1[i];
			int b=MASK[length+i];

			int c=a^b;

			result[i]=Byte.valueOf(String.valueOf(c));
		}

		String finalStr= Base64Utils.encodeToString(result);

		if(length<11) {
			length += 50;
		}

		String tail=Integer.toUnsignedString(length,11);
		return finalStr+tail;
	}

	public static final long  decode(String id){

		String tail=StringUtils.substring(id,id.length()-2);
		int length=Integer.parseUnsignedInt(tail,11);

		String base=StringUtils.substring(id,0,id.length()-2);

		if(length>50){
			length-=50;
		}
		byte[] result=Base64Utils.decodeFromString(base);

		byte[] bytes1=new byte[result.length];

		for(int i=0;i<bytes1.length;i++){
			int a=result[i];
			int b=MASK[length+i];

			int c=a^b;

			bytes1[i]=Byte.valueOf(String.valueOf(c));
		}

		String trimStr=new String(bytes1,Charsets.UTF_8);

//		String first=StringUtils.substringBefore(trimStr,"8");
//		String end=StringUtils.substringAfter(trimStr,"8");


		String idStr=StringUtils.substringBetween(trimStr,"8","8");

		long lID= Long.parseUnsignedLong(StringUtils.reverse(idStr),7)+OfferBase;

		return lID;

	}


}
