/**
 * 版权：zcc
 * 作者：c0z00k8
 * @data 2018年7月5日
 */
package com.zcc.datasync.mq;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.zcc.datasync.service.EshopProductService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author c0z00k8
 *
 */
@Component  
@RabbitListener(queues = "data-change-queue")  
public class DataChangeQueueReceiver {

	@Autowired
	private EshopProductService eshopProductService;
	@Autowired
	private JedisPool jedisPool;
	@Autowired
	private RabbitMQSender rabbitMQSender;
	private Set<String> dimDataChangeMessageSet=Collections.synchronizedSet(new HashSet<String>());
	
	
	public DataChangeQueueReceiver() {
		new SendThread().start();
	}
	
	@RabbitHandler  
    public void process(String message) {  
    	// 对这个message进行解析
    	JSONObject jsonObject = JSONObject.parseObject(message);
    	
    	// 先获取data_type
    	String dataType = jsonObject.getString("data_type");  
    	if("brand".equals(dataType)) {
    		processBrandDataChangeMessage(jsonObject);  
    	} else if("category".equals(dataType)) {
    		processCategoryDataChangeMessage(jsonObject); 
    	} else if("product_intro".equals(dataType)) {
    		System.out.println("dataType:"+dataType);
    		processProductIntroDataChangeMessage(jsonObject); 
    	} else if("product_property".equals(dataType)) {
    		processProductPropertyDataChangeMessage(jsonObject);
     	} else if("product".equals(dataType)) {
     		processProductDataChangeMessage(jsonObject); 
     	} else if("product_specification".equals(dataType)) {
     		processProductSpecificationDataChangeMessage(jsonObject);  
     	}
    }  
	
	
	private void processBrandDataChangeMessage(JSONObject messageJSONObject) {
    	Long id = messageJSONObject.getLong("id"); 
    	String eventType = messageJSONObject.getString("event_type"); 
    	
    	if("add".equals(eventType) || "update".equals(eventType)) { 
    		JSONObject dataJSONObject = JSONObject.parseObject(eshopProductService.findBrandById(id));  
    		if(null != dataJSONObject){
    			Jedis jedis = jedisPool.getResource();
        		jedis.set("brand_" + dataJSONObject.getLong("id"), dataJSONObject.toJSONString());
    		}
    	} else if ("delete".equals(eventType)) {
    		Jedis jedis = jedisPool.getResource();
    		jedis.del("brand_" + id);
    	}
    	  
//    	dimDataChangeMessageSet.add("{\"dim_type\": \"brand\", \"id\": " + id + "}");
    	rabbitMQSender.send("aggr-data-change-queue", "{\"dim_type\": \"brand\", \"id\": " + id + "}");
    }
    
    private void processCategoryDataChangeMessage(JSONObject messageJSONObject) {
    	Long id = messageJSONObject.getLong("id"); 
    	String eventType = messageJSONObject.getString("event_type"); 
    	
    	if("add".equals(eventType) || "update".equals(eventType)) { 
    		JSONObject dataJSONObject = JSONObject.parseObject(eshopProductService.findCategoryById(id));  
    		Jedis jedis = jedisPool.getResource();
    		jedis.set("category_" + dataJSONObject.getLong("id"), dataJSONObject.toJSONString());
    	} else if ("delete".equals(eventType)) {
    		Jedis jedis = jedisPool.getResource();
    		jedis.del("category_" + id);
    	}
    	  
//    	dimDataChangeMessageSet.add("{\"dim_type\": \"category\", \"id\": " + id + "}");
    	rabbitMQSender.send("aggr-data-change-queue", "{\"dim_type\": \"category\", \"id\": " + id + "}");
    }
    
    private void processProductIntroDataChangeMessage(JSONObject messageJSONObject) {
    	System.out.println("jsonObj:`````````"+messageJSONObject);
    	Long id = messageJSONObject.getLong("id"); 
    	Long productId = messageJSONObject.getLong("product_id");
    	String eventType = messageJSONObject.getString("event_type"); 
    	
    	if("add".equals(eventType) || "update".equals(eventType)) { 
    		JSONObject dataJSONObject = JSONObject.parseObject(eshopProductService.findProductIntroById(id));  
    		Jedis jedis = jedisPool.getResource();
    		jedis.set("product_intro_" + productId, dataJSONObject.toJSONString());
    	} else if ("delete".equals(eventType)) {
    		Jedis jedis = jedisPool.getResource();
    		jedis.del("product_intro_" + productId);
    	}
    	  
    	dimDataChangeMessageSet.add("{\"dim_type\": \"product_intro\", \"id\": " + productId + "}");
//    	rabbitMQSender.send("aggr-data-change-queue", "{\"dim_type\": \"product\", \"id\": " + productId + "}");
    }
    
    private void processProductDataChangeMessage(JSONObject messageJSONObject) {
    	Long id = messageJSONObject.getLong("id"); 
    	String eventType = messageJSONObject.getString("event_type"); 
    	
    	if("add".equals(eventType) || "update".equals(eventType)) { 
    		JSONObject dataJSONObject = JSONObject.parseObject(eshopProductService.findProductById(id));  
    		Jedis jedis = jedisPool.getResource();
    		jedis.set("product_" + id, dataJSONObject.toJSONString());
    	} else if ("delete".equals(eventType)) {
    		Jedis jedis = jedisPool.getResource();
    		jedis.del("product_" + id);
    	}
    	  
    	dimDataChangeMessageSet.add("{\"dim_type\": \"product\", \"id\": " + id + "}");
//    	rabbitMQSender.send("aggr-data-change-queue", "{\"dim_type\": \"product\", \"id\": " + id + "}");
    }
    
    private void processProductPropertyDataChangeMessage(JSONObject messageJSONObject) {
    	Long id = messageJSONObject.getLong("id"); 
    	Long productId = messageJSONObject.getLong("product_id");
    	String eventType = messageJSONObject.getString("event_type"); 
    	
    	if("add".equals(eventType) || "update".equals(eventType)) { 
    		JSONObject dataJSONObject = JSONObject.parseObject(eshopProductService.findProductPropertyById(id));  
    		Jedis jedis = jedisPool.getResource();
    		jedis.set("product_property_" + productId, dataJSONObject.toJSONString());
    	} else if ("delete".equals(eventType)) {
    		Jedis jedis = jedisPool.getResource();
    		jedis.del("product_property_" + productId);
    	}
    	  
    	dimDataChangeMessageSet.add("{\"dim_type\": \"product\", \"id\": " + productId + "}");
//    	rabbitMQSender.send("aggr-data-change-queue", "{\"dim_type\": \"product\", \"id\": " + productId + "}");
    }
    
    private void processProductSpecificationDataChangeMessage(JSONObject messageJSONObject) {
    	Long id = messageJSONObject.getLong("id"); 
    	Long productId = messageJSONObject.getLong("product_id");
    	String eventType = messageJSONObject.getString("event_type"); 
    	
    	if("add".equals(eventType) || "update".equals(eventType)) { 
    		JSONObject dataJSONObject = JSONObject.parseObject(eshopProductService.findProductSpecificationById(id));  
    		Jedis jedis = jedisPool.getResource();
    		jedis.set("product_specification_" + productId, dataJSONObject.toJSONString());
    	} else if ("delete".equals(eventType)) {
    		Jedis jedis = jedisPool.getResource();
    		jedis.del("product_specification_" + productId);
    	}
    	  
    	dimDataChangeMessageSet.add("{\"dim_type\": \"product\", \"id\": " + productId + "}");
//    	rabbitMQSender.send("aggr-data-change-queue", "{\"dim_type\": \"product\", \"id\": " + productId + "}");
    }
    
    private class SendThread extends Thread {
    	@Override
    	public void run() {
    		while(true) {
    			if(!dimDataChangeMessageSet.isEmpty()) {
    				for(String message : dimDataChangeMessageSet) {
    					rabbitMQSender.send("aggr-data-change-queue", message);   
    					System.out.println("【将去重后的维度数据变更消息发送到下一个queue】,message=" + message); 
    				}
    				dimDataChangeMessageSet.clear();
    			}
    			try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
    		}
    	}
    }
    
    
}
