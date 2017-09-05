/**
 * 
 */
package com.mycreat.kiipu.utils;

import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.*;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author xiao.xu
 * 
 */
public class JsonUtil {

	public static String toJson(Object obj){
		Gson gson = new GsonBuilder().serializeNulls().create();
		return gson.toJson(obj);
	}
	public static String MapToJson(TreeMap<String, Object> map) {
		String string = "{ ";
		for (Iterator<Entry<String, Object>> it = map.entrySet().iterator(); it
				.hasNext();) {
			Entry<String, Object> e = (Entry<String, Object>) it.next();
			string += "\"" + e.getKey() + "\" : ";
			Object value = e.getValue();

			string += getJsonStrByDataType(value) + ", ";
		}
		string = string.substring(0, string.lastIndexOf(","));
		string += " }";
		return string;
	}

	/**
	 * 根据数据类型获取对应格式的JSON串
	 * 
	 * @return
	 */
	public static String getJsonStrByDataType(Object data) {
		StringBuffer str = new StringBuffer("");
		// Example "string"
		if (data instanceof String) {
			str.append("\"");
			str.append(data.toString());
			str.append("\"");
		} else {
			if(data instanceof Integer || data instanceof Boolean || data instanceof Float || data instanceof Double ){
				str.append(data + "");
			}else{
				// Example ["v1", "v2"]
				if (data instanceof ArrayList) {
					str.append("[ ");
					ArrayList<?> list = (ArrayList<?>) data;
	
					for (int i = 0; i < list.size(); i++) {
	
						str.append("\"");
						Object value = list.get(i);
						str.append(value.toString());
						str.append("\"");
	
						str.append(", ");
	
					}
					str = str.deleteCharAt(str.lastIndexOf(","));
					str.append(" ]");
				} else {
					// Example ["null"]
					if (data == null) // invalid or null?
					{
						str.append(" \"");
						str.append(" \"");
					} else {
						// Example {"key1":"v1", "key2":"null", "key3": ["v3", "v4"]
						// }
						if (data instanceof Map<?, ?>) {
							Map<?, ?> map = (Map<?, ?>) data;
							String string = "{ ";
	
							for (Object key : map.keySet()) {
								string += "\"" + key.toString() + "\" : ";
								string += getJsonStrByDataType(map.get(key)) + ", ";
							}
	
							string = string.substring(0, string.lastIndexOf(","));
							string += " }";
	
							str.append(string);
						} else {
							str.append("\"");
							str.append("" + data);
							str.append("\"");
						}
					}
				}
			}

		}

		return str.toString();
	}

	/**
	 * Json 转成树结构(Map为子树，List为孩子列表，基本数据类型为单个节点)
	 * 
	 * @param jsonStr
	 * @return
	 */
	@Nullable
	public static Map<String , Object> getMapTree(String jsonStr, @Nullable Object defaultValueIfNull) {
		Object obj = getObjectFromJson(jsonStr, defaultValueIfNull);
		Map<String, Object> mMap = new HashMap<>();
		if(obj instanceof Map ){
			Map map = (Map) obj;
			Set keySet = map.keySet();
			for(Object key : keySet){
				Object value = map.get(key);
				if(value == null && defaultValueIfNull != null) {
					value = defaultValueIfNull;
				}
				mMap.put(key.toString(), value);
			}
			return mMap;
		}else{
			return null;
		}
	}
	
	
	public static Object getObjectFromJson(String jsonStr, @Nullable Object defaultValueIfNull){
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonStr);

			Iterator<?> keyIter = jsonObject.keys();

			HashMap<String, Object> valueMap = new HashMap<>();
			while (keyIter.hasNext()) {
				Object key = keyIter.next();
				// 若值仍是JSONObject
				JSONObject value = jsonObject.optJSONObject(key.toString());
				if (value != null) {
					if (value.keys() != null) { // 若value仍然是个JSON
						Iterator<?> keyIter1 = value.keys();
						HashMap<String, Object> valueMap1 = new HashMap<String, Object>();
						while (keyIter1.hasNext()) {
							Object key1 = keyIter1.next();
							Object object = getObjectFromJson(value
									.getString(key1.toString()), defaultValueIfNull);
							valueMap1.put(key1.toString(), object);
						}
						valueMap.put(key.toString(), valueMap1);
					} else {
						if (value.names() != null) { // 若value仍然是个JSONObjectArray
							JSONArray values = value.names();
							List<Object> objects = new ArrayList<Object>();
							for (int i = 0; i < values.length(); i++) {
								objects.add(getObjectFromJson(values.getString(i), defaultValueIfNull));
							}
							valueMap.put(key.toString(), objects);
						} else {
							valueMap.put(key.toString(), value);
						}
					}
					continue;
				}else{
					if(defaultValueIfNull != null)
						valueMap.put(key.toString(), defaultValueIfNull);
				}

				// 若值是JSONArray
				JSONArray values = jsonObject.optJSONArray(key.toString());
				if (values != null) {
					List<Object> objects = new ArrayList<Object>();
					for (int i = 0; i < values.length(); i++) {
						objects.add(getObjectFromJson(values.getString(i), defaultValueIfNull));
					}
					valueMap.put(key.toString(), objects);
					continue;
				}else{
					valueMap.put(key.toString(), defaultValueIfNull);
				}

				// 若值是基本类型
				Object obj = jsonObject.opt(key.toString());
				if (obj != null) {
					valueMap.put(key.toString(), obj);
				} else {
					valueMap.put(key.toString(), null);
				}

			}
			return valueMap;
		} catch (JSONException e) {
			//可能是Json数组
			try {
				List<Object> objects = new ArrayList<>();
				JSONArray jsonArray = new JSONArray(jsonStr);

				for (int i = 0; i < jsonArray.length(); i++) {
					objects.add(getObjectFromJson(jsonArray.getString(i), defaultValueIfNull));
				}
				return objects;
			} catch (JSONException e1) {
				return jsonStr == null ? "":jsonStr;
			}
		}

	}
}
