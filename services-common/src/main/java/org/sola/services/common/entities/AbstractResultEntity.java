/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.services.common.entities;

import java.io.Serializable;
import java.lang.reflect.Field;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.Immutable;

/**
 * This is an abstract class that has to be overriden from the entities that will be used to map
 * results of the queries.
 * 
 * @author Elton Manoku
 */
@MappedSuperclass
public class AbstractResultEntity implements Serializable {

    @Id
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
    
    public AbstractResultEntity(){
        
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    final public boolean equals(Object object) {
        if (!(object instanceof AbstractResultEntity)) {
            return false;
        }
        AbstractResultEntity other = (AbstractResultEntity) object;
        return this.id.equals(other.id);
    }

    @Override
    final public String toString() {
        Field[] fields = this.getClass().getDeclaredFields();
        String objPresentation = String.format("Presentation: id => %s", this.getId());
        for (Field field : fields) {
            System.out.println("Field name: " + field.getName());
            String fieldValue = "Not stringable";
            try {
                field.setAccessible(true);
                Object value = field.get(this);
                if (value == null) {
                    fieldValue = "null";
                } else {
                    fieldValue = value.toString();
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            objPresentation =
                    String.format("%s\n   - %s -> %s", objPresentation, field.getName(), fieldValue);
        }

        return objPresentation;
    }
}
