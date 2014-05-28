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
package org.sola.services.common.repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation required to identify a child entity that has a one to one relationship with 
 * the parent entity. This information is used to control the entity load and save process. If the
 * child entity should not be updated via the parent entity, set the readOnly flag to true. 
 * @author soladev
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChildEntity {

    /**
     * Indicates if the child entity should be inserted before the parent entity. This is an 
     * optional value. The default is true.
     * <p>This value must be set to false if the child entity references the id of the parent entity.
     * Note that this flag is also used to determine the delete order. If the parent must be inserted 
     * before the child, then the child must be deleted before the parent.</p> 
     * <p> Where the parent entity references the child (i.e. insertBeforeParen = true), the preSave 
     * method on the parent entity should be overridden to set the child id field to null if the 
     * child is marked for Delete or Disassociate. See Party.preSave() for an example. </p>
     */
    boolean insertBeforeParent() default true;

    /**
     * The name of the field on the child that contains the parent id. Required if insertBeforeParent
     * = false. Default is empty string.  
     * <p>Setting this value will ensure the parent id on the child  entity is automatically 
     * updated during the save process. </p>
     */
    String parentIdField() default "";
    
        /**
     * The name of the field on the parent that contains the child id. Required if insertBeforeParent
     * = true. Default is empty string.  
     */
    String childIdField() default "";
    
    /**
     * Flag to indicate the entity is read only and should not be updated via the parent entity.  
     * Default false.  
     */
    boolean readOnly() default false; 
        
}
