package com.kidyone.filepass;

import android.text.TextUtils;

import java.util.Collection;
import java.util.Map;

public class GenericUtil {

	private GenericUtil() {
	}

	public static <E extends Collection<?>> boolean isEmpty(final E list) {
		return list == null || list.size() == 0;
	}

	public static <E extends Map<?, ?>> boolean isEmpty(final E map) {
		return map == null || map.size() == 0;
	}

	public static <E> boolean isEmpty(final E[] array) {
		return array == null || array.length == 0;
	}


	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNotNull(final String str) {
		if (isEmpty(str)) {
			return false;
		} else {
			return !"null".equals(str);
		}
	}

	public static String getNotNullString(final String str){
		if(isNotNull(str)){
			return str;
		} else {
			return "";
		}
	}

	public static <E extends Collection<?>> boolean isEmpty(final E list,int position) {
		return isEmpty(list) || position <0 || position > list.size();
	}

	public static <E extends Map<?, ?>> boolean isEmpty(final E map,int position) {
		return isEmpty(map) || position < 0 || position>map.size();
	}

	public static <E> boolean isEmpty(final E[] array,int position) {
		return isEmpty(array) || position < 0 || position > array.length;

	}

	public static String expand(String string,String st2){
		return String.format("%s%s", TextUtils.isEmpty(string)?"":string,TextUtils.isEmpty(st2)?"":st2);
	}

}
