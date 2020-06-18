package org.wso2.carbon.apimgt.rest.api.admin.v1.impl;

import org.wso2.carbon.apimgt.api.APIAdmin;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.ExceptionCodes;
import org.wso2.carbon.apimgt.impl.APIAdminImpl;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.rest.api.admin.v1.*;
import org.wso2.carbon.apimgt.rest.api.admin.v1.dto.*;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.MessageContext;

import org.wso2.carbon.apimgt.rest.api.admin.v1.dto.ErrorDTO;
import org.wso2.carbon.apimgt.rest.api.admin.v1.dto.ScopeSettingsDTO;
import org.wso2.carbon.apimgt.rest.api.util.utils.RestApiUtil;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.util.Base64;
import java.util.List;

import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;


public class SystemScopesApiServiceImpl implements SystemScopesApiService {


    public Response systemScopesScopeNameGet(String scope, String username, MessageContext messageContext) throws APIManagementException{
        ScopeSettingsDTO scopeSettingsDTO = new ScopeSettingsDTO();
        APIAdmin apiAdmin = new APIAdminImpl();
        String decodedScope = new String(Base64.getDecoder().decode(scope));
        boolean existence ;

            if (username == null) {
                existence = apiAdmin.isScopeExists(RestApiUtil.getLoggedInUsername(), decodedScope);
                if (existence) {
                    scopeSettingsDTO.setName(decodedScope);
                } else {
                    throw new APIManagementException("Scope Not Found.  Scope : " + decodedScope +
                            ExceptionCodes.SCOPE_NOT_FOUND);
                }
            } else {
                existence = apiAdmin.isScopeExistsForUser(username, decodedScope);
                if (existence) {
                    scopeSettingsDTO.setName(decodedScope);
                } else {
                    throw new APIManagementException("User does not have  scope. Username :" + username + " Scope : "
                            + decodedScope + ExceptionCodes.SCOPE_NOT_FOUND_FOR_USER);
                }
            }
        return Response.ok().entity(scopeSettingsDTO).build();
    }
}
