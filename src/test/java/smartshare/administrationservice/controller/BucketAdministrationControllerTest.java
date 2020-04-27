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
import smartshare.administrationservice.dto.Status;
import smartshare.administrationservice.dto.UserBucketMapping;
import smartshare.administrationservice.models.BucketAccessRequestEntity;
import smartshare.administrationservice.service.BucketAccessRequestService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class BucketAdministrationControllerTest {

    @MockBean
    private BucketAccessRequestService bucketAccessRequestService;

    @Autowired
    private MockMvc mockMvc;



    @Test
    @DisplayName("POST /bucket/addUser - SUCCESS")
    void addUserToBucket() throws Exception {
        // set up the mock service

        // difficulty in creating mocks can change this stub for integration testing

        UserBucketMapping addUserFromUiToBucket = new UserBucketMapping( "sethuram", "file.server.1" );

        when( bucketAccessRequestService.addUserToBucketByBucketAdmin( any() ) ).thenReturn( Boolean.TRUE );

        // execute the post request
        mockMvc.perform( post( "/bucket/addUser" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( asJsonString( addUserFromUiToBucket ) )
        )
                .andExpect( status().isCreated() );
    }

    @Test
    @DisplayName("DELETE /bucket/removeUser - SUCCESS")
    void removeUserFromBucket_success() throws Exception {
        // set up the mock service

        // difficulty in creating mocks can change this stub for integration testing

        UserBucketMapping removeUserFromBucket = new UserBucketMapping( "sethuram", "file.server.1" );
        Status status = new Status();
        status.setValue( Boolean.TRUE );
        when( bucketAccessRequestService.removeUserFromBucketByBucketAdmin( any() ) ).thenReturn( status );

        // execute the post request
        mockMvc.perform( delete( "/bucket/removeUser" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( asJsonString( removeUserFromBucket ) )
        )
                .andExpect( status().isOk() );
    }

    @Test
    @DisplayName("DELETE /bucket/removeUser - FAILURE")
    void removeUserFromBucket_failure() throws Exception {
        // set up the mock service

        // difficulty in creating mocks can change this stub for integration testing

        UserBucketMapping removeUserFromBucket = new UserBucketMapping( "sethuram", "file.server.1" );
        Status status = new Status();
        status.setValue( Boolean.FALSE );
        status.setReasonForFailure( "The user has Objects which has to be deleted before removing the user" );
        when( bucketAccessRequestService.removeUserFromBucketByBucketAdmin( any() ) ).thenReturn( status );

        // execute the post request
        mockMvc.perform( delete( "/bucket/removeUser" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( asJsonString( removeUserFromBucket ) )
        )
                .andExpect( status().isBadRequest() )
                .andExpect( content().string( "The user has Objects which has to be deleted before removing the user" ) );
    }






    @Test
    @DisplayName("GET  /bucket/bucketAccessRequestForUser - SUCCESS")
    void getBucketAccessRequestForUser() throws Exception {
        // set up the mock service
        BucketAccessRequestEntity bucketAccessRequest1 = new BucketAccessRequestEntity();
        BucketAccessRequestEntity bucketAccessRequest2 = new BucketAccessRequestEntity();
        BucketAccessRequestEntity bucketAccessRequest3 = new BucketAccessRequestEntity();
        List<BucketAccessRequestEntity> bucketAccessRequestList = new ArrayList<>();
        bucketAccessRequestList.add( bucketAccessRequest1 );
        bucketAccessRequestList.add( bucketAccessRequest2 );
        bucketAccessRequestList.add( bucketAccessRequest3 );


        when( bucketAccessRequestService.getBucketAccessRequestForUser( any() ) ).thenReturn( bucketAccessRequestList );

        // execute the get request

        mockMvc.perform( get( "/bucket/bucketAccessRequestForUser" )
                .param( "userName", "sethuram" )
        )
                .andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON ) );
        // not sure of how return value is

//                .andExpect( jsonPath( "$[0].objectMetadata.ownerName" ).value( "Owner" ) );


    }


    private String asJsonString(Object object) throws JsonProcessingException {
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return objectWriter.writeValueAsString( object );
    }

}