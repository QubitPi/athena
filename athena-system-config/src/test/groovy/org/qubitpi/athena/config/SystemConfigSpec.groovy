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
package org.qubitpi.athena.config

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

abstract class SystemConfigSpec extends Specification {

    static final String MISSING_PROPERTY_KEY = "MISSING KEY"

    static final String STRING_PROPERTY_KEY = "string_property_key"
    static final String STRING_PROPERTY_VALUE = "forty-two"
    static final String STRING_VALUE = "forty-two"
    static final String STRING_DEFAULT_VALUE = "seven"

    static final String INT_PROPERTY_KEY = "int_property_key"
    static final String INT_PROPERTY_VALUE = "42"
    static final Integer INT_VALUE = 42
    static final Integer INT_DEFAULT_VALUE = 7

    static final String LONG_PROPERTY_KEY = "long_property_key"
    static final String LONG_PROPERTY_VALUE = "42"
    static final Long LONG_VALUE = 42L
    static final Long LONG_DEFAULT_VALUE = 7L

    static final String FLOAT_PROPERTY_KEY = "float_property_key"
    static final String FLOAT_PROPERTY_VALUE = "42.0"
    static final Float FLOAT_VALUE = 42.0F
    static final Float FLOAT_DEFAULT_VALUE = 7.0F

    static final String DOUBLE_PROPERTY_KEY = "double_property_key"
    static final String DOUBLE_PROPERTY_VALUE = "42.0"
    static final Double DOUBLE_VALUE = 42.0D
    static final Double DOUBLE_DEFAULT_VALUE = 7.0D

    static final String BOOLEAN_PROPERTY_KEY = "boolean_property_key"
    static final String BOOLEAN_PROPERTY_VALUE = "true"
    static final Boolean BOOLEAN_VALUE = true
    static final Boolean BOOLEAN_DEFAULT_VALUE = true

    @Shared
    SystemConfig systemConfig = getTestSystemConfig()

    def setupSpec() {
        // Setup the known values
        systemConfig.setProperty(STRING_PROPERTY_KEY, STRING_PROPERTY_VALUE)
        systemConfig.setProperty(INT_PROPERTY_KEY, INT_PROPERTY_VALUE)
        systemConfig.setProperty(LONG_PROPERTY_KEY, LONG_PROPERTY_VALUE)
        systemConfig.setProperty(FLOAT_PROPERTY_KEY, FLOAT_PROPERTY_VALUE)
        systemConfig.setProperty(DOUBLE_PROPERTY_KEY, DOUBLE_PROPERTY_VALUE)
        systemConfig.setProperty(BOOLEAN_PROPERTY_KEY, BOOLEAN_PROPERTY_VALUE)
    }

    def "Package scoped variable name starts with Athena identifier"() {
        expect:
        systemConfig.getPackageVariableName("foo").startsWith("athena__")
    }

    @Unroll
    def "Reading a #propertyType property gives the property value"() {
        expect: "We read a property that exists, we get get value"
        value == systemConfig."get${propertyType}Property"(property).get()

        where:
        [propertyType, property, _, value] << getTestIterationData()
    }

    @Unroll
    def "Reading a missing #propertyType property returns an Optional.empty() instance"() {
        when: "we read a property that doesn't exist"
        Optional actual = systemConfig."get${propertyType}Property"(MISSING_PROPERTY_KEY)

        then:
        actual == Optional.empty()

        where:
        [propertyType] << getTestIterationData()
    }

    def "When a property is cleared, it no longer binds a value"() {
        setup: "a property is preset"
        systemConfig.setProperty("foo", "bar")

        expect: "an value is bound to that property"
        systemConfig.getStringProperty("foo").get() == "bar"

        when: "the property is deleted"
        systemConfig.clearProperty("foo")

        then: "we can no longer retrieve the bound value"
        !systemConfig.getStringProperty("foo").isPresent()
    }

    abstract SystemConfig getTestSystemConfig();

    /**
     * Returns the data to use in where blocks as a list of lists.
     * <p>
     * The skip parameter allows for skipping sets of properties
     *
     * @param skips  A Map of type of skips. Set a type to true to skip that data
     *
     * @return the data as a list of lists
     */
    def getTestIterationData(Map<String, Boolean> skips = [:]) {
        Map<String, Boolean> doSkip = skips.withDefault {false}

        [
                //                 prop type, property,             property value,         value,         default value,         unconvertible,       real type
                doSkip.String  ?: ["String",  STRING_PROPERTY_KEY,  STRING_PROPERTY_VALUE,  STRING_VALUE,  STRING_DEFAULT_VALUE,  null,                String],
                doSkip.Int     ?: ["Int",     INT_PROPERTY_KEY,     INT_PROPERTY_VALUE,     INT_VALUE,     INT_DEFAULT_VALUE,     STRING_PROPERTY_KEY, Integer],
                doSkip.Long    ?: ["Long",    LONG_PROPERTY_KEY,    LONG_PROPERTY_VALUE,    LONG_VALUE,    LONG_DEFAULT_VALUE,    STRING_PROPERTY_KEY, Long],
                doSkip.Float   ?: ["Float",   FLOAT_PROPERTY_KEY,   FLOAT_PROPERTY_VALUE,   FLOAT_VALUE,   FLOAT_DEFAULT_VALUE,   STRING_PROPERTY_KEY, Float],
                doSkip.Double  ?: ["Double",  DOUBLE_PROPERTY_KEY,  DOUBLE_PROPERTY_VALUE,  DOUBLE_VALUE,  DOUBLE_DEFAULT_VALUE,  STRING_PROPERTY_KEY, Double],
                doSkip.Boolean ?: ["Boolean", BOOLEAN_PROPERTY_KEY, BOOLEAN_PROPERTY_VALUE, BOOLEAN_VALUE, BOOLEAN_DEFAULT_VALUE, STRING_PROPERTY_KEY, Boolean],
        ].findAll {
            // Only return data lists
            it instanceof List
        }
    }
}
