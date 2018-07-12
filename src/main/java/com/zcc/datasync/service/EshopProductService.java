/**
 * 版权：zcc
 * 作者：c0z00k8
 * @data 2018年7月5日
 */
package com.zcc.datasync.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zcc.datasync.service.fallback.EshopProductServiceFallback;

/**
 * @author c0z00k8
 *
 */
@FeignClient(value = "eshop-product-service",fallback=EshopProductServiceFallback.class)
public interface EshopProductService {

	@RequestMapping(value = "/brand/findById",method = RequestMethod.GET)
    String findBrandById(@RequestParam(value = "id") Long id);
	
	@RequestMapping(value = "/category/findById",method = RequestMethod.GET)
    String findCategoryById(@RequestParam(value = "id") Long id);
    
    @RequestMapping(value = "/product-intro/findById",method = RequestMethod.GET)
    String findProductIntroById(@RequestParam(value = "id") Long id);
    
    @RequestMapping(value = "/product-property/findById",method = RequestMethod.GET)
    String findProductPropertyById(@RequestParam(value = "id") Long id);
    
    @RequestMapping(value = "/product/findById",method = RequestMethod.GET)
    String findProductById(@RequestParam(value = "id") Long id);
    
    @RequestMapping(value = "/product-specification/findById",method = RequestMethod.GET)
    String findProductSpecificationById(@RequestParam(value = "id") Long id);
    
}
