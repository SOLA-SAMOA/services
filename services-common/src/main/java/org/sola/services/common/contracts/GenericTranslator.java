/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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
import org.sola.common.mapping.MappingManager;
import org.sola.common.mapping.MappingUtility;

/**
 * This class provides generic translation of entities to or from a Transfer Object (TO). The
 * Translator uses the Dozer Bean Mapping library along with some customizations to address issues
 * with the Dozer mapper.
 *
 * @see MappingManager
 * @see #getMapper()
 */
public final class GenericTranslator {

    private static final String SERVICE_MAPPING_CONFIG = "/dozerMappingConfigServices.xml";

    /**
     * Obtains an instance of the Mapper and sets the GenericTranslatorListener as well as the extra
     * mapping file used by SOLA Services. <p>Lighthouse Bug Fixes: <ul><li>#178 - Added extra
     * mapping config file for services</li></ul></p>
     */
    private static Mapper getMapper() {
        String serviceMappingConfigFile = GenericTranslator.class.getResource(SERVICE_MAPPING_CONFIG).toString();
        return MappingManager.getMapper(new GenericTranslatorListener(), serviceMappingConfigFile);
    }

    /**
     * Generically translates from an entity object tree to a TO object tree using the Dozer Bean
     * Mapper.
     *
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
     * Translates a list of entity objects to a list of TO objects using the Dozer Bean Mapper. This
     * method wraps {@linkplain #toTO(java.lang.Object, java.lang.Class) toTO}
     *
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
     * Translates the TO object tree onto an entity object tree using the Dozer Bean Mapper.
     *
     * @param <T> The generic type of the entity class to translate to.
     * @param resultTO The TO object to translate from.
     * @param entityClass The concrete type of the entity class to translate to.
     * @param entity Should be a reference to an entity retrieved from the database but can be null.
     * @return If an attached entity was passed in, then this will be an updated version of the
     * entity. If null was passed in, this will be a new entity object tree that is not attached to
     * the persistence context.
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

    /**
     * Translates a list of TO objects to a list of entity objects using the Dozer Bean Mapper. Note
     * that the method ensures that TOs are converted into the relevant object in the entity list
     * using the {@linkplain MappingUtility}. <p>Lighthouse Bug Fixes: <ul><li>#125 -
     * Modified to use the {@linkplain MappingUtility} as lists passed directly to Dozer
     * suffer from type erasure which means Dozer is unable to determine the correct entity type.</li></ul></p>
     *
     * @param <T> The generic entity type to translate the TO objects into
     * @param <S> The generic type of the TO class
     * @param toList The list of TOs to translate
     * @param entityClass The entity class to translate the TO's into
     * @param entityList The existing list of entities to translate the TO's into
     * @return The translated list of entities or NULL if both the toList and entityList are null. 
     * @see MappingUtility#translateList(java.util.List, java.util.List, java.lang.Class, org.dozer.Mapper) 
     * MappingUtility.translateList
     */
    public static <T, S extends AbstractTO> List<T> fromTOList(
            List<S> toList, Class<T> entityClass, List<T> entityList) {
        // Default the return list to the list of entities passed in
        return MappingUtility.translateList(toList, entityList, entityClass, getMapper());
    }
}
