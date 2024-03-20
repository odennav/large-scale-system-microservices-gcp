//////////////////////////////////////////////////////////////////////////////
// Copyright 2020 Anurag Yadav (anurag.yadav@newtechways.com)               //
//                                                                          //
// Licensed under the Apache License, Version 2.0 (the "License");          //
// you may not use this file except in compliance with the License.         //
// You may obtain a copy of the License at                                  //
//                                                                          //
//     http://www.apache.org/licenses/LICENSE-2.0                           //
//                                                                          //
// Unless required by applicable law or agreed to in writing, software      //
// distributed under the License is distributed on an "AS IS" BASIS,        //
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. //
// See the License for the specific language governing permissions and      //
// limitations under the License.                                           //
//////////////////////////////////////////////////////////////////////////////

package com.ntw.oms.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

/**
 * Created by anurag on 01/07/19.
 */
public class ResponseFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

    @Override
    public int filterOrder() {
        return 3;
    }

    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        logger.debug("Response post filter called");
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        if (request.getRequestURI().startsWith("/status") ||
                request.getRequestURI().startsWith("/actuator/prometheus")) {
            return null;
        }
        try (final InputStream responseDataStream = context.getResponseDataStream()) {
            if(responseDataStream == null) {
                logger.info("Error in getting response body for request URL={} Status={} Body={}",
                        request.getRequestURI() , context.getResponseStatusCode(), "None");
                return null;
            }
            String responseData;
            if (context.getResponseGZipped()) {
                InputStream gzipResponseDataStream = new GZIPInputStream(responseDataStream);
                responseData = StreamUtils.copyToString(gzipResponseDataStream, Charset.forName("UTF-8"));
            } else {
                responseData = StreamUtils.copyToString(responseDataStream, Charset.forName("UTF-8"));
            }
            logger.info("Request post filter executed; url={} status={} response-body={}", request.getRequestURI() ,
                    context.getResponseStatusCode(), responseData);
            context.setResponseBody(responseData);
        }
        catch (Exception e) {
            throw new ZuulException(e, 500, e.getMessage());
        }
        return null;
    }
}
