/*
 * Copyright 2013 Nicolas Morel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.progressoft.brix.domino.gwtjackson.ser.map.key;

import com.progressoft.brix.domino.gwtjackson.DefaultJsonSerializationContext;
import com.progressoft.brix.domino.gwtjackson.JsonSerializationContext;
import com.progressoft.brix.domino.gwtjackson.JacksonTestCase;
import com.progressoft.brix.domino.gwtjackson.JsonSerializationContext;

/**
 * @author Nicolas Morel
 */
public abstract class AbstractKeySerializerTest<T> extends JacksonTestCase {

    protected abstract KeySerializer createSerializer();

    public void testSerializeNullValue() {
        assertSerialization(null, null);
    }

    protected String serialize(T value) {
        JsonSerializationContext ctx = DefaultJsonSerializationContext.builder().build();
        return createSerializer().serialize(value, ctx);
    }

    protected void assertSerialization(String expected, T value) {
        assertEquals(expected, serialize(value));
    }

    public abstract void testSerializeValue();
}
