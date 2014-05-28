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
import org.sola.services.common.ejbs.AbstractEJBLocal;

/**
 * Annotation used to mark child entity fields that require an external EJB to load or save them. 
 * This annotation must be used in combination with {@linkplain ChildEntity} and 
 * {@linkplain ChildEntityList}. 
 * @author soladev
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExternalEJB {

    /**
     * The Local Interface class of the EJB that will be used to load or save the child entity or
     * child entity list. Required.
     */
    Class<? extends AbstractEJBLocal> ejbLocalClass();

    /**
     * The name of the method on the EJB Local interface used to load the child entity or child
     * entity list. If this annotation is for a one to one child, the load method should accept a
     * string value as its parameter (i.e. id of the entity). If this annotation is for a list of
     * child entities, the load method should accept a List of strings (i.e. list of child ids). 
     * Required.  
     */
    String loadMethod();

    /** 
     * The name of the method on the EJB Local interface used to save the child entity. The save
     * method should accept the child entity as its only parameter. Note that if the child field
     * is a list, each entity in the list will be passed to the EJB save method. 
     * Optional. Omitting a value for this member will effectively make the external entity 
     * read only.    
     */
    String saveMethod() default "";
}
