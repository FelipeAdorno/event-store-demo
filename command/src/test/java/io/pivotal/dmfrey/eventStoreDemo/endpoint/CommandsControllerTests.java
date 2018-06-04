package io.pivotal.dmfrey.eventStoreDemo.endpoint;

import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith( SpringRunner.class )
@WebMvcTest( CommandsController.class )
public class CommandsControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BoardService service;

    private UUID boardUuid = UUID.randomUUID();
    private UUID storyUuid = UUID.randomUUID();

    @Test
    public void testCreateBoard() throws Exception {

        when( this.service.createBoard() ).thenReturn( boardUuid );

        this.mockMvc.perform( post( "/boards" ).param( "name", "Test Board" ) )
                .andDo( print() )
                .andExpect( status().isCreated() )
                .andExpect( header().string( HttpHeaders.LOCATION, is( equalTo( "http://localhost/boards/" + boardUuid.toString() ) ) ) );

        verify( this.service, times( 1 ) ).createBoard();
        verifyNoMoreInteractions( this.service );

    }

    @Test
    public void testRenameBoard() throws Exception {

        this.mockMvc.perform( patch( "/boards/{boardUuid}", boardUuid ).param( "name", "Test Board" ) )
                .andDo( print() )
                .andExpect( status().isAccepted() );;

        verify( this.service, times( 1 ) ).renameBoard( boardUuid, "Test Board" );
        verifyNoMoreInteractions( this.service );

    }

    @Test
    public void testCreateStoryOnBoard() throws Exception {

        when( this.service.addStory( any( UUID.class ), anyString() ) ).thenReturn( storyUuid );

        this.mockMvc.perform( post( "/boards/{boardUuid}/stories", boardUuid ).param( "name", "Test Story" ) )
                .andDo( print() )
                .andExpect( status().isCreated() )
                .andExpect( header().string( HttpHeaders.LOCATION, is( equalTo( "http://localhost/boards/" + boardUuid.toString() + "/stories/" + storyUuid.toString() ) ) ) );

        verify( this.service, times( 1 ) ).addStory( boardUuid, "Test Story" );
        verifyNoMoreInteractions( this.service );

    }

    @Test
    public void testUpdateStoryOnBoard() throws Exception {

        this.mockMvc.perform( put( "/boards/{boardUuid}/stories/{storyUuid}", boardUuid, storyUuid ).param( "name", "Test Story Updated" ) )
                .andDo( print() )
                .andExpect( status().isAccepted() );

        verify( this.service, times( 1 ) ).updateStory( boardUuid, storyUuid, "Test Story Updated" );
        verifyNoMoreInteractions( this.service );

    }

    @Test
    public void testDeleteStoryOnBoard() throws Exception {

        this.mockMvc.perform( delete( "/boards/{boardUuid}/stories/{storyUuid}", boardUuid, storyUuid ) )
                .andDo( print() )
                .andExpect( status().isAccepted() );

        verify( this.service, times( 1 ) ).deleteStory( boardUuid, storyUuid );
        verifyNoMoreInteractions( this.service );

    }

}
