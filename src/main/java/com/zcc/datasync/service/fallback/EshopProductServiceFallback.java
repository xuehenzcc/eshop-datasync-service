/**
 * 版权：zcc
 * 作者：c0z00k8
 * @data 2018年7月10日
 */
package com.zcc.datasync.service.fallback;

import org.springframework.stereotype.Component;

import com.zcc.datasync.service.EshopProductService;

/**
 * @author c0z00k8
 *
 */
@Component
public class EshopProductServiceFallback implements EshopProductService{

	@Override
	public String findBrandById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findCategoryById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findProductIntroById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findProductPropertyById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findProductById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findProductSpecificationById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
