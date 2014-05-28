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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.common.repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.sola.services.common.repository.entities.AbstractEntity;

/**
 * Annotation required to identify child entities that have a One or Many or Many to Many 
 * relationship with the parent entity. This information is used to control the entity load
 * and save process. 
 * @author soladev
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChildEntityList {

    /**
     * The name of the field on the child that contains the parent id. Required for all One to Many 
     * and Many to Many relationships.
     * <p>For One to Many relationships, setting this value will ensure the parent id on the child 
     * entity is automatically updated during the save process. </p>
     * <p>For Many to Many relationships this value indicates the name of the field on the many
     * to many class that contains the id of the parent entity. </p>
     */
    String parentIdField();

    /**
     * The name of the field on the many to many entity that contains the child id. Required for
     * Many to Many relationships. Default is empty string. 
     */
    String childIdField() default "";

    /**
     * The entity class representing the many to many relationship between the parent entity and the
     * child entity. This class must extent AbstractEntity. Required for Many to Many relationships.
     * Default is AbstractEntity.class. 
     * <p> The save process will automatically insert or delete the Many to Many database record
     * depending on the EntityAction of the child class. </p>
     */
    Class<? extends AbstractEntity> manyToManyClass() default AbstractEntity.class;
    
    /** 
     * Flag to indicate whether the child entities should be automatically cascade deleted if the 
     * parent entity is marked for delete. Optional for One to Many and Many to Many relationships. 
     * Default false. 
     */
    boolean cascadeDelete() default false; 
    
    /**
     * Flag to indicate the entity is read only and should not be updated via the parent entity. 
     * Optional for Many to Many relationships where it may be necessary to manage the many to many
     * entity, but not update the child entity. Also optional for One to Many relationships. 
     * Default false.  
     */
    boolean readOnly() default false; 
}
