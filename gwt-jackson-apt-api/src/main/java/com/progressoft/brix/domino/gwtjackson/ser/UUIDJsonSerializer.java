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

package com.progressoft.brix.domino.gwtjackson.ser;

import com.progressoft.brix.domino.gwtjackson.JsonSerializationContext;
import com.progressoft.brix.domino.gwtjackson.JsonSerializer;
import com.progressoft.brix.domino.gwtjackson.JsonSerializerParameters;
import com.progressoft.brix.domino.gwtjackson.stream.JsonWriter;

import java.util.UUID;

/**
 * Default {@link JsonSerializer} implementation for {@link UUID}.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public class UUIDJsonSerializer extends JsonSerializer<UUID> {

    private static final UUIDJsonSerializer INSTANCE = new UUIDJsonSerializer();

    /**
     * <p>getInstance</p>
     *
     * @return an instance of {@link UUIDJsonSerializer}
     */
    public static UUIDJsonSerializer getInstance() {
        return INSTANCE;
    }

    private UUIDJsonSerializer() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSerialize(JsonWriter writer, UUID value, JsonSerializationContext ctx, JsonSerializerParameters params) {
        writer.unescapeValue(value.toString());
    }
}
