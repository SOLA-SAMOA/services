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
package org.sola.services.common;

import java.util.HashMap;
import java.util.logging.Level;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.TransactionSynchronizationRegistry;
import org.sola.common.logging.LogUtility;

/**
 * Holds references to objects and/or values that may need to be accessed by different parts of 
 * the program. If this class is being used from a JEE container, the values will be placed in the 
 * Transaction Synchronization Registry which is local to a transaction and may span multiple threads. 
 * If this class is being used outside of a JEE container (e.g. for testing, etc), The values are 
 * placed in ThreadLocal storage to ensure they are unique to each thread. 
 * To obtain a reference value simply call LocalInfo.get(<objectName>);
 * <p>
 * Fields that require frequent access (such as user name) have explicity get methods on this class.
 * Note that values that need to be accessed outside the scope of a transaction should be placed in
 * Thread Local storage (e.g. userName) as the transaction storage is not available once the transaction
 * has been committed or rolled back. 
 * </p>
 * @author soladev
 */
public final class LocalInfo {

    /** 
     * Hash map that contains the objects in ThreadLocal storage. Thread Local storage is only used
     * if the Transaction Synchronization Registry (i.e. Transaction Local storage) is unavailable. 
     */
    private static ThreadLocal<HashMap<String, Object>> localInfo;
    /**
     * Constant used as the key for the Username attribute. 
     */
    public static String USER_NAME = "Local.UserName";
    public static String TRANSACTION_ID = "Local.TransactionId";

    /**
     * @return The Transaction Synchronization Registry from the JEE container or null if it is not
     * available. 
     */
    private static TransactionSynchronizationRegistry getTransactionRegistry() {
        TransactionSynchronizationRegistry registry = null;
        try {
            Context context = new InitialContext();
            registry = (TransactionSynchronizationRegistry) context.lookup("java:comp/TransactionSynchronizationRegistry");
        } catch (NamingException ex) {
            // Unable to obtain a Transaction Local storage area - possibly this is a test that is 
            // not being run in the context of a JEE container. Use Thread Local Storage instead. 
            LogUtility.log("Failed to lookup java:comp/TransactionSynchronizationRegistry. "
                    + "Will use Thread Local storage instead.", Level.WARNING);
            System.out.println("Warning: Failed to lookup java:comp/TransactionSynchronizationRegistry."
                    + "Will use Thread Local storage instead.");
        }
        // Make sure a transaction is associated with the registry
        if (registry != null && registry.getTransactionStatus() == Status.STATUS_NO_TRANSACTION) {
            registry = null;
        }
        return registry;
    }

    /**
     * Obtains the name of the currently logged in user or null if this is not set. 
     * @return The logged in users user name. User name is placed in Thread Local storage 
     * as it may be necessary to access the value outside the scope of a transaction. 
     */
    public static String getUserName() {
        return get(USER_NAME, String.class, true);
    }

    /**
     * Sets the user name for the currently logged in user. The SOLA EJB set this value during the
     * postConstructor phase after all resources have been injected. User name is placed in
     * Thread Local storage as it may be necessary to access the value outside the scope of a 
     * transaction. 
     * @param userName The user name to set. 
     */
    public static void setUserName(String userName) {
        set(USER_NAME, userName, true, true);
    }

    public static String getTransactionId() {
        return get(TRANSACTION_ID, String.class);
    }

    public static void setTransactionId(String userName) {
        set(TRANSACTION_ID, userName, true);
    }

    /**
     * Gets the object referenced by the key value from local storage. If the key does not 
     * exist in local storage, null is returned. 
     * @param key The key used to reference the target object. 
     * @return The target object or null. 
     */
    public static Object get(String key) {
        return get(key, Object.class, false);
    }

    /**
     * Generic method that returns the object referenced by the key value from local storage 
     * cast to the type of localClass. If the object does not exist, or it cannot be cast to the
     * localClass, null is returned. 
     * @param <T> Generic type placeholder variable. 
     * @param key The key used to reference the target object. 
     * @param localClass The concrete class of the object to return. 
     * @return The object cast to localClass or null if the object does not exist or cannot be cast. 
     */
    public static <T> T get(String key, Class<T> localClass) {
        return get(key, localClass, false);
    }

    /**
     * Generic method that returns the object referenced by the key value from local storage 
     * cast to the type of localClass. If the object does not exist, or it cannot be cast to the
     * localClass, null is returned. 
     * @param <T> Generic type placeholder variable. 
     * @param key The key used to reference the target object. 
     * @param localClass The concrete class of the object to return. 
     * @param useThreadLocal Forces the object to be obtained from ThreadLocal storage. This may 
     * be useful if the object must be accessed outside the scope of a transaction (e.g. userName 
     * may be accessed during exception processing). 
     * @return The object cast to localClass or null if the object does not exist or cannot be cast. 
     */
    public static <T> T get(String key, Class<T> localClass, boolean useThreadLocal) {
        T result = null;
        Object value = null;
        TransactionSynchronizationRegistry registry = getTransactionRegistry();
        if (registry != null && !useThreadLocal) {
            // Get the value from Transaction Local storage
            value = registry.getResource(key);
        } else if (localInfo != null) {
            HashMap<String, Object> map = localInfo.get();
            if (map != null) {
                // Get the value from Thread Local Storage
                value = map.get(key);

            }
        }
        if (value != null && localClass.isAssignableFrom(value.getClass())) {
            result = (T) value;
        }
        return result;
    }

    /**
     * Sets a object in local storage using the specified key as its reference value. If the
     * key matches a key in local storage, the current value is not replaced. To remove an 
     * object from local storage use the overloaded set method. 
     * @param key The key to use for referencing the object. 
     * @param value The object to add into local storage. 
     * @return Flag to indicate if the object was added to local storage. If false this will
     * mean the key already existed so the object was not added. To force replacement of the existing
     * value, use the overloaded set method. 
     */
    public static boolean set(String key, Object value) {
        return set(key, value, false, false);
    }

    /**
     * Sets a object in local storage using the specified key as its reference value.
     * This is an overloaded version of the set method that can be used to force replacement
     * of the object if it already exists. This method can also be used to remove an Object from
     * local storage by specifying null as the value. e.g. LocalInfo.set(<Key>, null, true); 
     * @param key The key to use for referencing the object.
     * @param value The object to add into ThreadLocal storage. 
     * @param replace If true, force replacement of any existing object with the one provided. 
     * @return Flag to indicate if the object was added to or replaced an existing object in 
     * ThreadLocal storage. 
     */
    public static boolean set(String key, Object value, boolean replace) {
        return set(key, value, replace, false);
    }

    /**
     * Sets a object in local storage using the specified key as its reference value.
     * This is an overloaded version of the set method that can be used to force replacement
     * of the object if it already exists. This method can also be used to remove an Object from
     * local storage by specifying null as the value. e.g. LocalInfo.set(<Key>, null, true); 
     * @param key The key to use for referencing the object.
     * @param value The object to add into ThreadLocal storage. 
     * @param replace If true, force replacement of any existing object with the one provided. 
     * @param useThreadLocal Forces the use of ThreadLocal storage. This should be used if you expect
     * a value must be obtained outside of the scope of a transaction. E.g. user name may be required
     * during exception handling processes.
     * @return Flag to indicate if the object was added to or replaced an existing object in 
     * ThreadLocal storage. 
     */
    public static boolean set(String key, Object value, boolean replace, boolean useThreadLocal) {
        boolean result = false;
        TransactionSynchronizationRegistry registry = getTransactionRegistry();
        if (registry != null & !useThreadLocal) {
            // Use Transaction Local Storage to store the value
            if (registry.getResource(key) == null || replace) {
                registry.putResource(key, value);
                result = true;
            }
        } else {
            // Use Thread Local Storage to store the value
            if (localInfo == null) {
                localInfo = new ThreadLocal<HashMap<String, Object>>();
            }
            if (localInfo.get() == null) {
                localInfo.set(new HashMap<String, Object>());
            }
            if (localInfo.get().containsKey(key)) {
                if (replace) {
                    localInfo.get().remove(key);
                    if (value != null) {
                        localInfo.get().put(key, value);
                    }
                    result = true;
                }
            } else {
                localInfo.get().put(key, value);
                result = true;
            }
        }
        return result;
    }

    /**
     * Clears all objects from ThreadLocal storage. 
     */
    public static void clear() {
        if (localInfo != null && localInfo.get() != null) {
            localInfo.get().clear();
        }
    }

    /**
     * Removes the threadLocal storage from the local tread. Should be used during PreDestory of the
     * EJB to ensure no unnecessary references remain to objects. 
     */
    public static void remove() {
        if (localInfo != null) {
            clear();
            localInfo.remove();
            localInfo = null;
        }
    }
}
