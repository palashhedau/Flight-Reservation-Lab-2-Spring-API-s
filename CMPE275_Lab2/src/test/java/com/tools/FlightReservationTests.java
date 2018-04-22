package com.tools ;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Entry.class)
@SpringBootTest
public class FlightReservationTests {
	
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext wac;
	
	@Before
	public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}
	
	@Test
	public void addPassenger() throws Exception {
		mockMvc.perform(post("/passenger")
				.param("firstname", "Palash")
				.param("lastname", "Hedau")
				.param("age", "11")
				.param("gender", "Male")
				.param("phone", "66920331")
				).andExpect(status().isBadRequest()) ; 
	}
	
	@Test
	public void deletePassenger() throws Exception {
		mockMvc.perform(delete("/passenger/1000")
				).andExpect(status().isNotFound()) ; 
	}
	
	@Test
	public void getPassenger() throws Exception {
		mockMvc.perform(get("/passenger/1")
				).andExpect(status().isOk()) ; 
	}
	
	@Test
	public void getPassengerAsXML() throws Exception {
		mockMvc.perform(get("/passenger/1")
				.param("xml", "true")
				).andExpect(status().isOk()) ; 
	}
	
	@Test
	public void updatePassenger() throws Exception {
		mockMvc.perform(get("/passenger/1")
				.param("firstname", "Messi")
				.param("lastname", "Hedau")
				.param("age", "11")
				.param("gender", "Male")
				.param("phone", "66920")
				).andExpect(status().isOk()) ; 
	}
	
	
	
	@Test
	public void deleteFlight() throws Exception {
		mockMvc.perform(delete("/flight/111111"))
		.andExpect(status().isNotFound()) ; 
	}
	
	@Test
	public void getReservation() throws Exception {
		mockMvc.perform(get("/reservation/1000")
				).andExpect(status().isNotFound()) ; 
	}
	
	
}