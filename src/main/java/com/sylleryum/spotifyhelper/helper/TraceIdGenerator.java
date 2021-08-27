package com.sylleryum.spotifyhelper.helper;

import com.sylleryum.spotifyhelper.controller.AuthController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class TraceIdGenerator {

    private static Logger logger;
    public static final String METHOD_NAME_NOT_FOUND = "method name not found";

    public static String generate(){
        return UUID.randomUUID().toString();
    }

    private static void setClass(Class aClass){
        logger = LoggerFactory.getLogger(aClass);
    }
    public static String writeTrace(Class aClass, String methodName){
        setClass(aClass);
        String traceId = generate();
        logger.trace("-> {} {}", methodName, traceId);
        return traceId;
    }

    public static String writeDebug(String message, Class aClass, String methodName){
        setClass(aClass);
        String traceId = generate();
        logger.debug("-> {}, {} - {}", message, methodName, traceId);
        return traceId;
    }

    public static String writeError(Class aClass, Exception e){
        setClass(aClass);
        String traceId = generate();
        logger.error("-> {}", traceId);
        e.printStackTrace();
        return traceId;
    }
}
