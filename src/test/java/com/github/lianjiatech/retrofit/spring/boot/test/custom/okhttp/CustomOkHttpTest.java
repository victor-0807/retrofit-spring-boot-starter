package com.github.lianjiatech.retrofit.spring.boot.test.custom.okhttp;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.lianjiatech.retrofit.spring.boot.test.RetrofitTestApplication;
import com.github.lianjiatech.retrofit.spring.boot.test.entity.Person;
import com.github.lianjiatech.retrofit.spring.boot.test.entity.Result;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

/**
 * @author 陈添明
 */
@SpringBootTest(classes = RetrofitTestApplication.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("sentinel")
public class CustomOkHttpTest {

    @Autowired
    private CustomOkHttpTestApi customOkHttpTestApi;

    private MockWebServer server;

    private static Gson gson = new GsonBuilder()
            .create();

    @Before
    public void before() throws IOException {
        System.out.println("=========开启MockWebServer===========");
        server = new MockWebServer();
        server.start(8080);

    }

    @After
    public void after() throws IOException {
        System.out.println("=========关闭MockWebServer===========");
        server.close();
    }

    @Test
    public void test() {

        // mock
        Person mockPerson = new Person().setId(1L)
                .setName("test")
                .setAge(10);
        Result mockResult = new Result<>()
                .setCode(0)
                .setMsg("ok")
                .setData(mockPerson);
        MockResponse response = new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Cache-Control", "no-cache")
                .setBody(gson.toJson(mockResult));
        server.enqueue(response);

        // http check
        Result<Person> person = customOkHttpTestApi.getPerson(1L);
        Person data = person.getData();
        Assert.assertNotNull(data);
        Assert.assertEquals("test", data.getName());
        Assert.assertEquals(10, data.getAge().intValue());
    }

}
