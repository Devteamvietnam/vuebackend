package com.devteamvietnam.vuebackend.converter.base;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

public class BaseConverter {
	
	protected ModelMapper mapper= new ModelMapper();


	public <T> T map(Object src, Class<T> className) {

		
		return mapId(src, mapper.map(src, className));
		
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T mapId(Object src, Object dest) {
		return (T) dest;
	}
	
	public <D, T> List<D> mapAll(final Collection<T> entityList, Class<D> outCLass) {
		return entityList.stream()
				.map(entity -> map(entity, outCLass))
				.collect(Collectors.toList());
	}


}
