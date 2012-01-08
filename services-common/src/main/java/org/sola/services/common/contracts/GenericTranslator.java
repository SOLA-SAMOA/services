/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.services.common.contracts;

import java.util.ArrayList;
import java.util.List;
import org.dozer.Mapper;
import org.sola.common.MappingManager;

/**
 * This class provides generic translation of entities to or from a Transfer Object (TO). 
 * <p>
 * The entity class to translate must be a descendent of AbstractEntity and the Transfer Object
 * class must be a descendent of AbstractReadWriteTO. 
 * </p>
 * <p>
 * The translation uses the get or set methods on the Transfer Object to determine the appropriate
 * fields to get or set on the entity. Child objects and child list objects are also translated. 
 * </p>
 */
public final class GenericTranslator {
    
    /**
     * Obtains an instance of the Mapper and sets the GenericTranslatorListener. 
     * @return 
     */
    private static Mapper getMapper() {
        MappingManager.setEventListener(new GenericTranslatorListener());
        return MappingManager.getMapper();        
    }

    /**
     * Generically translates from an entity object tree to a TO object tree using the Dozer
     * Bean Mapper.
     * @param <T> The type of TO class to translate to. Must extend AbstractTO. 
     * @param entity The entity object to translate from.
     * @param toClass The concrete TO class to translate to. e.g. ApplicationTO.class
     * @return The translated TO object or null if the entity was null.
     */
    public static <T extends AbstractTO> T toTO(Object entity, Class<T> toClass) {
        T resultTO = null;
        if (entity != null) {
            resultTO = getMapper().map(entity, toClass);
        }
        return resultTO;
    }

    /**
     * Translates a list of entity objects to a list of TO objects. 
     * @param <T> The type of TO class to translate to. Must extend AbstractTO. 
     * @param <S> The type of entity class to translate from. Must extend AbstractEntity 
     * @param entityList The list of entity objects to translate from.  
     * @param toClass The concrete class of the TO to translate to. e.g. PartyTO.class 
     * @return A list of TO objects or null. 
     */
    public static <T extends AbstractTO, S> List<T> toTOList(
            List<S> entityList, Class<T> toClass) {
        
        List<T> resultList = null;
        if (entityList != null && entityList.size() > 0) {
            resultList = new ArrayList<T>();
            for (S entity : entityList) {
                resultList.add(toTO(entity, toClass));
            }
        }
        return resultList;
    }

    /**
     * Translates the TO object tree onto an entity object tree. This includes translation of any
     * child objects and/or lists.
     * <p>
     * This method works by matching the getters from the TO object to the getters and setters on the 
     * entity object. If the TO getter name cannot be matched to a entity getter / setter name, a
     * warning is logged and the translation continues. 
     * </p>
     * <p>
     * The entity parameter of this method should be a reference to an attached entity. This will
     * allow this method to merge the changes in the TO object tree into the entity object tree and
     * result in an entity object tree that is attached to the persistence context, simplifying 
     * management of the entity objects and any subsequent data updates. Note that persist will need 
     * to be called on the entity manager to ensure any new entities in the object tree are correctly
     * saved. 
     * </p>
     * @param <T> The generic type of the entity class to translate to. Must extend AbstractTO
     * @param resultTO The TO object to translate from.
     * @param entityClass The concrete type of the entity class to translate to. Must extend
     * AbstractEntity to match the requirements for T. e.g. Party.class.
     * @param entity Should be a reference to an attached entity but can be null.
     * @return If an attached entity was passed in, then this will be an updated version of the entity. 
     * If null was passed in, this will be a new entity object tree that is not attached to the 
     * persistence context. 
     */
    public static <T> T fromTO(AbstractTO to,
            Class<T> entityClass, Object entity) {
        
        T resultEntity = null;
        if (to != null) {
            if (entity == null) {
                resultEntity = getMapper().map(to, entityClass);
            } else {
                getMapper().map(to, entity);
                resultEntity = (T) entity;
            }
        }
        return resultEntity;
    }
    
    public static <T, S extends AbstractTO> List<T> fromTOList(
            List<S> toList, Class<T> entityClass, List<T> entityList) {

        // Default the return list to the list of entities passed in
        List<T> resultList = entityList;
        if (toList != null && toList.size() > 0) {
            if (entityList == null) {
                entityList = new ArrayList<T>();                
            }
            getMapper().map(toList, entityList);
            resultList = entityList;            
        }
        return resultList;
    }
}
