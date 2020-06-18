package org.wso2.carbon.apimgt.rest.api.publisher.v1;

import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.AlertConfigDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.AlertConfigInfoDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.AlertConfigListDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.ErrorDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.AlertsApiService;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.impl.AlertsApiServiceImpl;
import org.wso2.carbon.apimgt.api.APIManagementException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.inject.Inject;

import io.swagger.annotations.*;
import java.io.InputStream;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import java.util.Map;
import java.util.List;
import javax.validation.constraints.*;
@Path("/alerts")

@Api(description = "the alerts API")
@Consumes({ "application/json" })
@Produces({ "application/json" })


public class AlertsApi  {

  @Context MessageContext securityContext;

AlertsApiService delegate = new AlertsApiServiceImpl();


    @PUT
    @Path("/{alertType}/configurations/{configurationId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Add AbnormalRequestsPerMin alert configurations. ", notes = "This operation is used to add configuration for the AbnormalRequestsPerMin alert type. ", response = AlertConfigDTO.class, authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "apim:pub_alert_manage", description = "Get/ subscribe/ configure publisher alerts")
        })
    }, tags={ "Alert Configuration",  })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Created. Successful response with newly created object as entity. Location header contains URL of newly created entity. ", response = AlertConfigDTO.class),
        @ApiResponse(code = 400, message = "Bad Request The request parameters validation failed. ", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Internal Server Error An error occurred while retrieving subscribed alert types by user. ", response = ErrorDTO.class) })
    public Response addAlertConfig(@ApiParam(value = "The alert type.",required=true) @PathParam("alertType") String alertType, @ApiParam(value = "The alert configuration id.",required=true) @PathParam("configurationId") String configurationId, @ApiParam(value = "Configuration for AbnormalRequestCount alert type" ,required=true) AlertConfigInfoDTO body) throws APIManagementException{
        return delegate.addAlertConfig(alertType, configurationId, body, securityContext);
    }

    @DELETE
    @Path("/{alertType}/configurations/{configurationId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Delete the selected configuration from AbnormalRequestsPerMin alert type. ", notes = "This operation is used to delete configuration from the AbnormalRequestsPerMin alert type. ", response = Void.class, authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "apim:pub_alert_manage", description = "Get/ subscribe/ configure publisher alerts")
        })
    }, tags={ "Alert Configuration",  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK. The alert config is deleted successfully. ", response = Void.class),
        @ApiResponse(code = 400, message = "Bad Request The request parameters validation failed. ", response = ErrorDTO.class),
        @ApiResponse(code = 404, message = "Not Found. The provided alert configuration is not found. ", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Internal Server Error An error occurred while retrieving subscribed alert types by user. ", response = ErrorDTO.class) })
    public Response deleteAlertConfig(@ApiParam(value = "The alert type.",required=true) @PathParam("alertType") String alertType, @ApiParam(value = "The alert configuration id.",required=true) @PathParam("configurationId") String configurationId) throws APIManagementException{
        return delegate.deleteAlertConfig(alertType, configurationId, securityContext);
    }

    @GET
    @Path("/{alertType}/configurations")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Get all AbnormalRequestsPerMin alert configurations ", notes = "This operation is used to get all configurations of the AbnormalRequestsPerMin alert type. ", response = AlertConfigListDTO.class, authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "apim:pub_alert_manage", description = "Get/ subscribe/ configure publisher alerts")
        })
    }, tags={ "Alert Configuration" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK. The store alert configuration. ", response = AlertConfigListDTO.class),
        @ApiResponse(code = 500, message = "Internal Server Error An error occurred while retrieving subscribed alert types by user. ", response = ErrorDTO.class) })
    public Response getAllAlertConfigs(@ApiParam(value = "The alert type.",required=true) @PathParam("alertType") String alertType) throws APIManagementException{
        return delegate.getAllAlertConfigs(alertType, securityContext);
    }
}
