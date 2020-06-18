/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
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

package org.wso2.carbon.apimgt.rest.api.publisher.v1.utils.mappings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIMRegistryService;
import org.wso2.carbon.apimgt.impl.APIMRegistryServiceImpl;
import org.wso2.carbon.apimgt.impl.containermgt.ContainerBasedConstants;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.DeploymentClusterInfoDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.DeploymentListDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.DeploymentsDTO;
import org.wso2.carbon.apimgt.rest.api.util.utils.RestApiUtil;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.user.api.UserStoreException;

import java.util.ArrayList;
import java.util.List;

import static org.wso2.carbon.apimgt.impl.utils.APIUtil.handleException;

public class DeploymentsMappingUtil {

    private static final Log log = LogFactory.getLog(DeploymentsMappingUtil.class);
    public static final String SUPER_TENANT_DOMAIN_NAME = "carbon.super";

    /**
     * This method feeds data into DeploymentsDTO list from tenant-conf.json/ api-manager.xml
     *
     * @return DeploymentsDTO list. List of Deployments
     * @throws APIManagementException
     */
    public DeploymentListDTO fromTenantConftoDTO() throws APIManagementException {

        DeploymentListDTO deploymentListDTO = new DeploymentListDTO();
        List<DeploymentsDTO> deploymentsList = new ArrayList<DeploymentsDTO>();
        //Get cloud environments from tenant-conf.json file
        //Get tenant domain to access tenant conf
        APIMRegistryService apimRegistryService = new APIMRegistryServiceImpl();
        String tenantDomain = RestApiUtil.getLoggedInUserTenantDomain();
        //read tenant-conf.json and get details
        if (SUPER_TENANT_DOMAIN_NAME.equals(tenantDomain)) {
            //get details from deployment.toml
            JSONArray containerMgtInfo = APIUtil.getClusterInfoFromAPIMConfig();
            if (!containerMgtInfo.isEmpty()) {
                for (Object containerMgtInfoObj : containerMgtInfo) {
                    DeploymentsDTO k8sClustersInfoDTO = new DeploymentsDTO();
                    List<DeploymentClusterInfoDTO> deploymentClusterInfoDTOList = new ArrayList<>();
                    JSONArray clustersInfo = (JSONArray) (((JSONObject) containerMgtInfoObj).get(ContainerBasedConstants.CONTAINER_MANAGEMENT_INFO));
                    //check is the super tenant defined cluster ddtails
                    if (clustersInfo != null && !clustersInfo.isEmpty()) {
                        for (Object clusterInfo : clustersInfo) {
                            DeploymentClusterInfoDTO deploymentClusterInfoDTO = new DeploymentClusterInfoDTO();
                            deploymentClusterInfoDTO.setClusterId(((JSONObject) clusterInfo).get(ContainerBasedConstants.CLUSTER_ID).toString());
                            deploymentClusterInfoDTO.setClusterName(((JSONObject) clusterInfo).get(ContainerBasedConstants.DISPLAY_NAME).toString());
                            JSONObject properties = (JSONObject) ((JSONObject) clusterInfo).get(ContainerBasedConstants.PROPERTIES);
                            deploymentClusterInfoDTO.setMasterURL(properties.get(ContainerBasedConstants.MASTER_URL).toString().replace("\\", ""));
                            deploymentClusterInfoDTO.setNamespace(properties.get(ContainerBasedConstants.NAMESPACE).toString());
                            deploymentClusterInfoDTO.setIngressURL(properties.get(ContainerBasedConstants.INGRESS_URL).toString().replace("\\", ""));

                            deploymentClusterInfoDTOList.add(deploymentClusterInfoDTO);
                        }
                        k8sClustersInfoDTO.setClusters(deploymentClusterInfoDTOList);
                        k8sClustersInfoDTO.setName((String) ((JSONObject) containerMgtInfoObj).get(ContainerBasedConstants.TYPE));
                        deploymentsList.add(k8sClustersInfoDTO);
                    }
                }
                deploymentListDTO.setCount(deploymentsList.size());
                deploymentListDTO.setList(deploymentsList);
            }
        } else {
            try {
                String getTenantDomainConfContent = apimRegistryService
                        .getConfigRegistryResourceContent(tenantDomain, APIConstants.API_TENANT_CONF_LOCATION);
                JSONParser jsonParser = new JSONParser();
                Object tenantObject = jsonParser.parse(getTenantDomainConfContent);
                JSONObject tenant_conf = (JSONObject) tenantObject;
                //get kubernetes cluster info
                JSONArray containerMgtInfo = (JSONArray) tenant_conf.get(ContainerBasedConstants.CONTAINER_MANAGEMENT);
                for (Object containerMgtInfoObj : containerMgtInfo) {
                    DeploymentsDTO k8sClustersInfoDTO = new DeploymentsDTO();
                    List<DeploymentClusterInfoDTO> deploymentClusterInfoDTOList = new ArrayList<>();
                    JSONArray clustersInfo = (JSONArray) (((JSONObject) containerMgtInfoObj).get(ContainerBasedConstants.CONTAINER_MANAGEMENT_INFO));
                    for (Object clusterInfo : clustersInfo) {
                        //check whether cluster details are defined.
                        if (!((JSONObject) clusterInfo).get(ContainerBasedConstants.CLUSTER_ID).equals("")) {
                            DeploymentClusterInfoDTO deploymentClusterInfoDTO = new DeploymentClusterInfoDTO();
                            deploymentClusterInfoDTO.setClusterId(((JSONObject) clusterInfo).get(ContainerBasedConstants.CLUSTER_ID).toString());
                            deploymentClusterInfoDTO.setClusterName(((JSONObject) clusterInfo).get(ContainerBasedConstants.DISPLAY_NAME).toString());
                            JSONObject properties = (JSONObject) ((JSONObject) clusterInfo).get(ContainerBasedConstants.PROPERTIES);
                            deploymentClusterInfoDTO.setMasterURL(properties.get(ContainerBasedConstants.MASTER_URL).toString());
                            deploymentClusterInfoDTO.setNamespace(properties.get(ContainerBasedConstants.NAMESPACE).toString());
                            deploymentClusterInfoDTO.setIngressURL(properties.get(ContainerBasedConstants.INGRESS_URL).toString());

                            deploymentClusterInfoDTOList.add(deploymentClusterInfoDTO);
                        }
                    }
                    if (!deploymentClusterInfoDTOList.isEmpty()) {
                        k8sClustersInfoDTO.setName((String) ((JSONObject) containerMgtInfoObj).get(ContainerBasedConstants.TYPE));
                        k8sClustersInfoDTO.setClusters(deploymentClusterInfoDTOList);
                    }
                    deploymentsList.add(k8sClustersInfoDTO);
                }
                deploymentListDTO.setCount(deploymentsList.size());
                deploymentListDTO.setList(deploymentsList);
            } catch (RegistryException e) {
                handleException("Couldn't read tenant configuration from tenant registry", e);
            } catch (UserStoreException e) {
                handleException("Couldn't read tenant configuration from tenant registry", e);
            } catch (ParseException e) {
                handleException("Couldn't parse tenant configuration for reading extension handler position", e);
            }
        }
        return deploymentListDTO;
    }

}
