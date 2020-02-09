package smartshare.administrationservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import smartshare.administrationservice.dto.ObjectAccessRequestFromUi;
import smartshare.administrationservice.dto.UsersAccessingOwnerObject;
import smartshare.administrationservice.models.ObjectAccessRequest;
import smartshare.administrationservice.service.ObjectAccessRequestService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ObjectAccessRequestControllerTest {

    @MockBean
    private ObjectAccessRequestService objectAccessRequestService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("POST /object/createAccessRequest - SUCCESS")
    void createObjectAccessRequest() throws Exception {
        // set up the mock service

        ObjectAccessRequestFromUi objectAccessRequestFromUi1 = new ObjectAccessRequestFromUi( "sethuram", "file.server.1", "folder", "owner", "read" );
        ObjectAccessRequestFromUi objectAccessRequestFromUi2 = new ObjectAccessRequestFromUi( "sethuram", "file.server.1", "folder/sample1.txt", "owner", "read" );
        ObjectAccessRequestFromUi objectAccessRequestFromUi3 = new ObjectAccessRequestFromUi( "sethuram", "file.server.1", "folder/sample2.txt", "owner", "read" );
        List<ObjectAccessRequestFromUi> objectAccessRequestFromUis = new ArrayList<>();
        objectAccessRequestFromUis.add( objectAccessRequestFromUi1 );
        objectAccessRequestFromUis.add( objectAccessRequestFromUi2 );
        objectAccessRequestFromUis.add( objectAccessRequestFromUi3 );

        when( objectAccessRequestService.createObjectAccessRequest( any() ) )
                .thenReturn( true );

        // execute the post request
        mockMvc.perform( post( "/object/createAccessRequest" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( asJsonString( objectAccessRequestFromUis ) )

        )
                .andExpect( status().isCreated() );
    }

    @Test
    @DisplayName("DELETE /object/deleteAccessRequest - SUCCESS")
    void deleteObjectAccessRequest() throws Exception {
        ObjectAccessRequest objectAccessRequest = new ObjectAccessRequest();
        // difficulty in creating mocks can change this stub for integration testing

        when( objectAccessRequestService.deleteObjectAccessRequest( any() ) )
                .thenReturn( true );

        // execute the post request
        mockMvc.perform( delete( "/object/deleteAccessRequest" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( asJsonString( Collections.singleton( objectAccessRequest ) ) )

        )
                .andExpect( status().isOk() );
    }

    @Test
    @DisplayName("PUT /object/approveAccessRequest - SUCCESS")
    void approveObjectAccessRequest() throws Exception {
        ObjectAccessRequest objectAccessRequest = new ObjectAccessRequest();
        when( objectAccessRequestService.approveObjectAccessRequest( any() ) )
                .thenReturn( true );

        // execute the post request
        mockMvc.perform( put( "/object/approveAccessRequest" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( asJsonString( objectAccessRequest ) )

        )
                .andExpect( status().isOk() );
    }

    @Test
    @DisplayName("PUT /object/rejectAccessRequest - SUCCESS")
    void rejectObjectAccessRequest() throws Exception {
        ObjectAccessRequest objectAccessRequest = new ObjectAccessRequest();
        when( objectAccessRequestService.rejectObjectAccessRequest( any() ) )
                .thenReturn( true );

        // execute the post request
        mockMvc.perform( put( "/object/rejectAccessRequest" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( asJsonString( objectAccessRequest ) )

        )
                .andExpect( status().isOk() );
    }

    @Test
    @DisplayName("GET /listOfUsersAccessingOwnersObject - SUCCESS")
    void getListOfUsersAccessingOwnerObject() throws Exception {
        // set up the mock service
        UsersAccessingOwnerObject usersAccessingOwnerObject1 = new UsersAccessingOwnerObject();
        UsersAccessingOwnerObject usersAccessingOwnerObject2 = new UsersAccessingOwnerObject();

        List<UsersAccessingOwnerObject> usersAccessingOwnerObjects = new ArrayList<>();
        usersAccessingOwnerObjects.add( usersAccessingOwnerObject1 );
        usersAccessingOwnerObjects.add( usersAccessingOwnerObject2 );


        when( objectAccessRequestService.getListOfUsersAccessingOwnerObject( any() ) ).thenReturn( usersAccessingOwnerObjects );

        // execute the get request

        mockMvc.perform( get( "/listOfUsersAccessingOwnersObject" )
                .param( "owner", "sethuram" )
        )
                .andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON ) );
        // not sure of how return value is

//                .andExpect( jsonPath( "$[0].objectMetadata.ownerName" ).value( "Owner" ) );


    }

    @Test
    @DisplayName("GET /accessRequestsCreatedByUser - SUCCESS")
    void getAccessRequestsCreatedByUser() throws Exception {

        // set up the mock service
        ObjectAccessRequest objectAccessRequest1 = new ObjectAccessRequest();
        ObjectAccessRequest objectAccessRequest2 = new ObjectAccessRequest();

        List<ObjectAccessRequest> objectAccessRequests = new ArrayList<>();
        objectAccessRequests.add( objectAccessRequest1 );
        objectAccessRequests.add( objectAccessRequest2 );


        when( objectAccessRequestService.getAccessRequestsCreatedByUser( any() ) ).thenReturn( objectAccessRequests );

        // execute the get request

        mockMvc.perform( get( "/accessRequestsCreatedByUser" )
                .param( "user", "sethuram" )
        )
                .andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON ) );
        // not sure of how return value is


    }

    @Test
    @DisplayName("GET /accessRequestsOfOwner - SUCCESS")
    void getAccessRequestsToBeApprovedByOwnerOfObject() throws Exception {

        // set up the mock service
        ObjectAccessRequest objectAccessRequest1 = new ObjectAccessRequest();
        ObjectAccessRequest objectAccessRequest2 = new ObjectAccessRequest();

        List<ObjectAccessRequest> objectAccessRequests = new ArrayList<>();
        objectAccessRequests.add( objectAccessRequest1 );
        objectAccessRequests.add( objectAccessRequest2 );


        when( objectAccessRequestService.getAccessRequestsToBeApprovedByOwnerOfObject( any() ) ).thenReturn( objectAccessRequests );

        // execute the get request

        mockMvc.perform( get( "/accessRequestsOfOwner" )
                .param( "owner", "sethuram" )
        )
                .andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON ) );

        // not sure of how return value is


    }

    private String asJsonString(Object object) throws JsonProcessingException {
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return objectWriter.writeValueAsString( object );
    }
}