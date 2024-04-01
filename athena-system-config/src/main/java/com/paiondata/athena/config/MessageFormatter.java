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
package com.paiondata.athena.config;

import jakarta.validation.constraints.NotNull;

/**
 * {@link MessageFormatter} provides shared functionality to provide formatted logging and messaging.
 * <p>
 * Webservice applications deals with unchecked exceptions frequently. The application catches the exception, logs a
 * custom message, and send another custom user-face error message back to client (also often passed to downstream
 * exceptions). It has been observed that common or similar log/error messages can be used across components of the
 * application.
 * <p>
 * For example, a Webservice endpoint takes a user request, which has a parameter like people.age. The
 * endpoint will reserve an exception message like "age must be positive" for use for the case when user asks a negative
 * age. This message is essentially saying "some number has to be positive".
 * <p>
 * It is very likely that other parts of the system also need to make sure some number needs to be positive, such as
 * number of connections in a connection pool config. DRY principle encourages us to deduplicate the similar message in
 * these 2 situations.
 * <p>
 * To do that, we can have a message format like "%s has to be positive" and replace the "%s" with "age" or
 * "num connections". {@link #getLoggingFormat()} in this case returns "%s has to be positive" and the argument to
 * {@link #format(Object...)} will be "age" and "num connections", respectively.
 * <p>
 * {@link MessageFormatter} also gives you flexibility to customize the message content based on the reader of the
 * message. In the example of people age above, the error message to Webservice client might be "age has to be
 * positive", but in the server log, a message like "endpoint 'xyz' has an invalid request parameter 'people.age'" will
 * be much more useful to an engineer. To support that, {@link #getMessageFormat()} and {@link #format(Object...)} will
 * be used for message that is sent to the client, while {@link #getLoggingFormat()} and {@link #logFormat(Object...)}
 * will be used for message for the logging.
 * <p>
 * A recommended implementation of {@link MessageFormatter} is enum such as the following:
 * <pre>
 * {@code
 * public enum ErrorMessageFormat implements MessageFormatter {
 *     NEGATIVE_NUMBER("'%s' has to be positive", "Negative invalid value found at component '%s'"),
 *     ...
 *     ;
 *
 *     private final String messageFormat;
 *     private final String loggingFormat;
 *
 *     ErrorMessageFormat(String messageFormat, String loggingFormat) {
 *         this.messageFormat = messageFormat;
 *         this.loggingFormat = loggingFormat;
 *     }
 *
 *     @Override
 *     public String getMessageFormat() {
 *         return messageFormat;
 *     }
 *
 *     @Override
 *     public String getLoggingFormat() {
 *         return loggingFormat;
 *     }
 * }
 * }
 * </pre>
 */
public interface MessageFormatter {

    /**
     * Returns the message used for publishing out of the system, typically in error messages.
     *
     * @return the format for a message
     */
    @NotNull
    String getMessageFormat();

    /**
     * Returns the message format used for logging.
     *
     * @return the format for a log message
     */
    @NotNull
    String getLoggingFormat();

    /**
     * Formats a message for reporting to a user/client.
     *
     * @param values  The values to populate the format string
     *
     * @return the use message
     */
    @NotNull
    default String format(final Object... values) {
        return String.format(getMessageFormat(), values);
    }

    /**
     * Formats a message for writing to the log.
     *
     * @param values  The values to populate the format string
     *
     * @return the logging message
     */
    @NotNull
    default String logFormat(final Object... values) {
        return String.format(getLoggingFormat(), values);
    }
}
