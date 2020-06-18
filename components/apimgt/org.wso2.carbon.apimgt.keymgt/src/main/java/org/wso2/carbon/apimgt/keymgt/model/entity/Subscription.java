/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.keymgt.model.entity;

import org.wso2.carbon.apimgt.api.model.subscription.CacheableEntity;
import org.wso2.carbon.apimgt.keymgt.model.util.SubscriptionDataStoreUtil;

/**
 * Entity for representing a SubscriptionDTO in APIM
 */
public class Subscription implements CacheableEntity<String> {

    private String subscriptionId = null;
    private String policyId = null;
    private Integer apiId = null;
    private Integer appId = null;
    private String subscriptionState = null;

    public String getSubscriptionId() {

        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {

        this.subscriptionId = subscriptionId;
    }

    public String getPolicyId() {

        return policyId;
    }

    public void setPolicyId(String policyId) {

        this.policyId = policyId;
    }

    public Integer getApiId() {

        return apiId;
    }

    public void setApiId(Integer apiId) {

        this.apiId = apiId;
    }

    public Integer getAppId() {

        return appId;
    }

    public void setAppId(Integer appId) {

        this.appId = appId;
    }

    public String getSubscriptionState() {

        return subscriptionState;
    }

    public void setSubscriptionState(String subscriptionState) {

        this.subscriptionState = subscriptionState;
    }

    @Override
    public String getCacheKey() {

        return SubscriptionDataStoreUtil.getSubscriptionCacheKey(getAppId(), getApiId());
    }
}
