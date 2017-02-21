/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ConcurrentHelper;

import java.util.concurrent.*;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;

import utils.LogUtils;

/**
 * This is a wrapper class for the <i>ScheduledThreadPoolExecutor</i>. It provides an implementation
 * for the <i>afterExecute()</i> found in the <i>ThreadPoolExecutor</i> class to log any unexpected 
 * Runtime Exceptions.
 * 
 * Author : Avinash Lakshman
 */
public final class DebuggableScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor
{
    private static Logger logger_ = LoggerFactory.getLogger(DebuggableScheduledThreadPoolExecutor.class);    
    
    public DebuggableScheduledThreadPoolExecutor(int threads)
    {
        super(threads);        
    }
    
    public void beforeExecute(Thread t, Runnable r)
    {
        super.beforeExecute(t, r);        
    }
    
    /**
     *  (non-Javadoc)
     * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable, java.lang.Throwable)
     */
    public void afterExecute(Runnable r, Throwable t)
    {
        super.afterExecute(r,t);        
        if ( t != null )
        {              
            Throwable cause = t.getCause();
            if ( cause != null )
            {
                logger_.info( LogUtils.throwableToString(cause) );
            }
            logger_.info( LogUtils.throwableToString(t) );
        }
    }
}
