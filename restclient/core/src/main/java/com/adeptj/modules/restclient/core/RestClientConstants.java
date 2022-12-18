/*
###############################################################################
#                                                                             #
#    Copyright 2016-2022, AdeptJ (http://www.adeptj.com)                      #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/
package com.adeptj.modules.restclient.core;

public class RestClientConstants {

    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final String HEADER_AUTHORIZATION = "Authorization";

    public static final String REQUEST_START = "<<==================== Request Processing Start ====================>>\n";

    public static final String REQUEST_END = "<<==================== Request Processing Complete ====================>>";

    public static final String RESPONSE_START = "<<==================== Response ====================>>\n";

    public static final String REQ_FMT
            = "\n{}\n Request ID: {}\n Request Method: {}\n Request URI: {}\n Request Headers: {}\n Request Body: {}";

    public static final String RESP_FMT
            = "\n{}\n Request ID: {}\n Response Status: {}\n Response Headers: {}\n Response Body: {}\n Total Time: {} ms\n\n{}";

}
