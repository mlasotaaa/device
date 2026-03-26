package org.example.device.infrastructure.rest;

import org.example.common.domain.DomainException;
import org.example.device.application.DeviceService;
import org.example.device.application.command.UpdateDeviceCommand;
import org.example.device.domain.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private DeviceService deviceService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");
    static {
        postgres.start();
    }

    @AfterEach
    void cleanAfterEachTest() {
        jdbcTemplate.execute("TRUNCATE TABLE devices CASCADE");
    }

    @Test
    void shouldReturn404WhenNotFound() throws Exception {
        // given
        DeviceId deviceId = new DeviceId(UUID.randomUUID());

        // when & then
        mockMvc.perform(get("/api/v1/devices/{id}", deviceId.value()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Device Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("Device with ID %s not found.", deviceId.value())))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldCreateAndGetDeviceViaHttp() throws Exception {
        // given
        String createJson = """
                {
                    "name": "HTTP",
                    "brand": "Whatever",
                    "status": "AVAILABLE"
                }
                """;

        // when & then
        String responseJson = createDeviceRequest(createJson)
                .getContentAsString();

        String createdId = com.jayway.jsonpath.JsonPath.read(responseJson, "deviceId");

        mockMvc.perform(get("/api/v1/devices/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("HTTP"));
    }

    @Test
    void shouldCreateDeviceWithoutNameAndBrand() throws Exception {
        // given
        String createJson = """
                {
                    "name": "",
                    "brand": "",
                    "status": "AVAILABLE"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value(
                        allOf(
                                containsString("name: name cannot be empty"),
                                containsString("brand: brand cannot be empty")
                        )
                ))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldCreateAndUpdateDevice() throws Exception {
        // given
        String createJson = """
                {
                    "name": "HTTP",
                    "brand": "Whatever",
                    "status": "AVAILABLE"
                }
                """;

        // when & then
        String responseJson = createDeviceRequest(createJson)
                .getContentAsString();

        String createdId = com.jayway.jsonpath.JsonPath.read(responseJson, "deviceId");

        String jsonRequest = """
                {
                    "name": "New Name"
                }
                """;

        mockMvc.perform(patch("/api/v1/devices/{id}", UUID.fromString(createdId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/devices/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.brand").value("Whatever"))
                .andExpect(jsonPath("$.state").value("AVAILABLE"));
    }

    @Test
    void shouldReturn422WhenDeviceInUse() throws Exception {
        // given
        String createJson = """
                {
                    "name": "HTTP",
                    "brand": "Whatever",
                    "status": "IN_USE"
                }
                """;

        // when & then
        String responseJson = createDeviceRequest(createJson)
                .getContentAsString();

        String createdId = com.jayway.jsonpath.JsonPath.read(responseJson, "deviceId");

        String jsonRequest = """
                {
                    "name": "New Name"
                }
                """;

        mockMvc.perform(patch("/api/v1/devices/{id}", UUID.fromString(createdId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.title").value("Device Is In Use"))
                .andExpect(jsonPath("$.detail").value(String.format("Cannot update device %s because it is currently IN_USE.", createdId)))
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void shouldReturn500WhenServiceFails() throws Exception {
        // given
        DeviceId deviceId = new DeviceId(UUID.randomUUID());

        doThrow(new RuntimeException("Unexpected database failure"))
                .when(deviceService).updateDevice(eq(deviceId), any(UpdateDeviceCommand.class));

        String jsonRequest = """
                {
                    "name": "New Name"
                }
                """;

        // when & then
        mockMvc.perform(put("/api/v1/devices/{id}", deviceId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred."));
    }

    @Test
    void shouldReturn422WhenGeneralDomainException() throws Exception {
        // given
        DeviceId deviceId = new DeviceId(UUID.randomUUID());

        doThrow(new DomainException("General domain exception foo,bar"))
                .when(deviceService).updateDevice(eq(deviceId), any(UpdateDeviceCommand.class));

        String jsonRequest = """
                {
                    "name": "New Name"
                }
                """;

        // when & then
        mockMvc.perform(patch("/api/v1/devices/{id}", deviceId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.title").value("Domain Business Rule Violation"))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.detail").value("General domain exception foo,bar"));
    }

    @Test
    void shouldCreateAndDeleteDeviceViaHttp() throws Exception {
        // given
        String createJson = """
                {
                    "name": "To Be Deleted",
                    "brand": "Temporary",
                    "status": "AVAILABLE"
                }
                """;

        // when & then
        String responseJson = createDeviceRequest(createJson).getContentAsString();
        String id = com.jayway.jsonpath.JsonPath.read(responseJson, "deviceId");

        mockMvc.perform(delete("/api/v1/devices/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/devices/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllDevices() throws Exception {
        // given
        createDeviceRequest("""
                {
                   "name":"Device 1",
                   "brand":"Brand A",
                   "status":"AVAILABLE"
                }
                """);
        createDeviceRequest("""
                {
                   "name":"Device 2",
                   "brand":"Brand B",
                   "status":"IN_USE"
                }
                """);

        // when & then
        mockMvc.perform(get("/api/v1/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[*]name",
                        org.hamcrest.Matchers.containsInAnyOrder("Device 1", "Device 2")));
    }

    @Test
    void shouldGetDevicesByState() throws Exception {
        // given
        createDeviceRequest("""
                {
                   "name":"Available 1",
                   "brand":"A",
                   "status":"AVAILABLE"
                }
                """);
        createDeviceRequest("""
                {
                   "name":"In Use 1",
                   "brand":"B",
                   "status":"IN_USE"
                }
                """
        );

        // when & then
        mockMvc.perform(get("/api/v1/devices").param("state", "IN_USE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0]name").value("In Use 1"))
                .andExpect(jsonPath("$[0]state").value("IN_USE"));
    }

    @Test
    void shouldGetDevicesByBrand() throws Exception {
        // given
        createDeviceRequest("""
                {
                   "name":"Macbook",
                   "brand":"Apple",
                   "status":"AVAILABLE"
                }
                """);
        createDeviceRequest("""
                {
                   "name":"Phone",
                   "brand":"Samsung",
                   "status":"AVAILABLE"
                }
                """);

        // when & then
        mockMvc.perform(get("/api/v1/devices").param("brand", "Apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0]brand").value("Apple"))
                .andExpect(jsonPath("$[0]name").value("Macbook"));
    }

    private MockHttpServletResponse createDeviceRequest(String json) throws Exception {
        return mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
    }
}