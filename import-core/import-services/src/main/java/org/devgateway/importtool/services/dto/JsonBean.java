package org.devgateway.importtool.services.dto;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author jdeanquin
 * 
 */
public class JsonBean {
	private static final Logger logger = Logger.getLogger(JsonBean.class);

	public JsonBean() {

	}

	protected LinkedHashMap<String, Object> param = new LinkedHashMap<String, Object>();

	public Object get(String name) { 
		return param.get(name);
	}

	@JsonAnyGetter
	public Map<String, Object> any() {
		return param;
	}

	@JsonAnySetter
	public void set(String name, Object value) {
		// we first try to translate the text
		param.put(name, value);
	}
	
	@JsonIgnore
	public Integer getSize() {
		return param.size();
	}
	
	public String getString(String name) {
		Object o = get(name);
		if (o != null) {
			return o.toString();
		} else {
			return null;
		}
	}

	public static JsonBean getJsonBeanFromString(String jb) {
		try {
			if (jb == null) {
				return null;
			}
			ObjectMapper mapper11 = SerializationHelper.getDefaultMapper();
			return mapper11.readValue(jb, JsonBean.class);
		} catch (IOException e) {
			logger.error("Cannot deserialize json bean", e);
			return null;
		}
	}


	@Override
	public String toString() {
        ObjectMapper mapper = new ObjectMapper().configure(
        		DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        String json=null;
		try {
			json = mapper.writer().writeValueAsString(this);
		} catch (IOException e) {
				
		}
		return json.toString();
	}
	
}
