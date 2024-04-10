---
title: Key Performance Indicators - Athena Web Service
sidebar_position: 7
---

[//]: # (Copyright Jiaqi Liu)

[//]: # (Licensed under the Apache License, Version 2.0 &#40;the "License"&#41;;)
[//]: # (you may not use this file except in compliance with the License.)
[//]: # (You may obtain a copy of the License at)

[//]: # (    http://www.apache.org/licenses/LICENSE-2.0)

[//]: # (Unless required by applicable law or agreed to in writing, software)
[//]: # (distributed under the License is distributed on an "AS IS" BASIS,)
[//]: # (WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.)
[//]: # (See the License for the specific language governing permissions and)
[//]: # (limitations under the License.)

These are the key performance indicators for the Athena Web Service component, listed in categories by order of
importance.

Server Error Responses (HTTP 5XX)
---------------------------------

Shows how much trouble _the service_ is having.

- `com.codahale.metrics.servlet.AbstractInstrumentedFilter.responseCodes.serverError.m1_rate`

Swift Errors
------------

Shows how much trouble queries are having against swift.

- `swift.errors.exceptions.m1_rate`
- `swift.errors.http.m1_rate`

Requests
--------

Shows how many requests the service is serving.

- `com.codahale.metrics.servlet.AbstractInstrumentedFilter.requests.m1_rate`
- `com.codahale.metrics.servlet.AbstractInstrumentedFilter.requests.m15_rate`

System Metrics
--------------

Shows the overall health of the system's low-level resources and activities.

- `CPU`
- `Memory`
- `Network IO`
- `GC Pauses`

Latency
-------

Shows duration of overall requests and druid requests. (m1_rate and pN)

- `com.codahale.metrics.servlet.AbstractInstrumentedFilter.requests.p50`
- `com.codahale.metrics.servlet.AbstractInstrumentedFilter.requests.p75`
- `com.codahale.metrics.servlet.AbstractInstrumentedFilter.requests.p95`
- `com.codahale.metrics.servlet.AbstractInstrumentedFilter.requests.p98`
- `com.codahale.metrics.servlet.AbstractInstrumentedFilter.requests.p99`
- `com.codahale.metrics.servlet.AbstractInstrumentedFilter.requests.p999`

Rate Limiting Rejections
------------------------

Shows if users are hitting rate limits.

- `ratelimit.meter.reject.ui.m1_rate`
- `ratelimit.meter.reject.user.m1_rate`
- `ratelimit.meter.reject.global.m1_rate`

Active Requests
---------------

Shows load at a given point in time. (ie. how close are the load is to the limits of Swift)

- `com.codahale.metrics.servlet.AbstractInstrumentedFilter.activeRequests.count`

Bad Request Responses (HTTP 4XX)
--------------------------------

Shows how much trouble users are having interacting with the API.

- `com.codahale.metrics.servlet.AbstractInstrumentedFilter.responseCodes.badRequest.m1_rate`
- `com.codahale.metrics.servlet.AbstractInstrumentedFilter.responseCodes.notFound.m1_rate`
