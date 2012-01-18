/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.common.repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Custom annotation used to define fields and sort order to sort entities.*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultSorter {
    String sortString() default "";
}
