/*
 * Copyright Jiaqi Liu
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
package com.qubitpi.athena.application;

import com.qubitpi.athena.metadata.MetaData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ApplicationState {

    public Map<String, MetaData> metadataByFileId = new HashMap<>();
    public BiFunction<String, List<String>, String> queryFormatter = (fileId, fields) -> "";
    public BiFunction<String, MetaData, String> mutationFormatter = (fileId, metadata) -> "";
}
