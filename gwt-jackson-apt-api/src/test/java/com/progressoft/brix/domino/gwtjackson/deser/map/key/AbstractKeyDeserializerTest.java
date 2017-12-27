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

package com.progressoft.brix.domino.gwtjackson.deser.map.key;

import com.progressoft.brix.domino.gwtjackson.DefaultJsonDeserializationContext;
import com.progressoft.brix.domino.gwtjackson.JsonDeserializationContext;
import com.progressoft.brix.domino.gwtjackson.JacksonTestCase;
import com.progressoft.brix.domino.gwtjackson.JsonDeserializationContext;

/**
 * @author Nicolas Morel
 */
public abstract class AbstractKeyDeserializerTest<T> extends JacksonTestCase {

    protected abstract KeyDeserializer<T> createDeserializer();

    public void testDeserializeNullValue() {
        assertNull(deserialize(null));
    }

    protected T deserialize(String value) {
        JsonDeserializationContext ctx = DefaultJsonDeserializationContext.builder().build();
        return deserialize(ctx, value);
    }

    protected T deserialize(JsonDeserializationContext ctx, String value) {
        return createDeserializer().deserialize(value, ctx);
    }

    protected void assertDeserialization(T expected, String value) {
        assertEquals(expected, deserialize(value));
    }

    protected void assertDeserialization(JsonDeserializationContext ctx, T expected, String value) {
        assertEquals(expected, deserialize(ctx, value));
    }

    public abstract void testDeserializeValue();
}
