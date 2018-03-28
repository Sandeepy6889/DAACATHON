package com.siemens.primecult.kpiUtils;

import java.io.IOException;

/*
 * Copyright (c) Siemens AG 2014 ALL RIGHTS RESERVED.
 *
 * R8  
 * 
 */
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Generic class used to serialize object to JSON string and deserialize it in specified objectType
 */
public class ObjectToJSonMapper
{
    private ObjectMapper objMapper;
    
    public ObjectToJSonMapper()
    {
        this.objMapper = new ObjectMapper();
        this.objMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        this.objMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    }
    
    public ObjectMapper getObjectMapper()
    {
    	return objMapper;
    	
    }
    
    public String mapObjToJSonStr(Object obj) throws IOException
    {
    	 String jsonStr;
        if (obj == null)
            throw new RuntimeException("Object must not be null.");
        try
        {
            jsonStr = objMapper.writeValueAsString(obj);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException("JSON processing failed: {0}");
        }
        return jsonStr;
        
    }
    
    public <T> T mapJSonStrToObj(String jsonStr, Class<T> objType)
    {
        if (jsonStr == null)
            throw new RuntimeException("JSON string must not be null");
        
        try
        {
            T obj = objMapper.readValue(jsonStr, objType);

            return obj;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Deserialisation failed");
        }
    }
}

/*
 * Copyright (c) Siemens AG 2014 ALL RIGHTS RESERVED
 * R8
 */
