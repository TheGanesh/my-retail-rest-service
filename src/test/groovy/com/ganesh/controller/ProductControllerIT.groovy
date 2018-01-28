package com.ganesh.controller

import com.myRetail.MyRetailApplication
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit4.SpringRunner

import static org.hamcrest.CoreMatchers.equalTo

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyRetailApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIT {

    @LocalServerPort
    private int port

    private URL base
    private TestRestTemplate testRestTemplate

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/")
        testRestTemplate = new TestRestTemplate()
    }

    @Test
    public void greeting() throws Exception {
        ResponseEntity<String> response = testRestTemplate.getForEntity(base.toString() + "hi", String.class)
        MatcherAssert.assertThat(response.getBody(), equalTo("Hello localhost"))
    }
}
