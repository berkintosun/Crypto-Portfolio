package com.berkin.cryptoportfolio.integration;

import com.berkin.cryptoportfolio.dto.AssetDTO;
import com.berkin.cryptoportfolio.dto.CreateAssetRequest;
import com.berkin.cryptoportfolio.dto.SupportedAssetsDTO;
import com.berkin.cryptoportfolio.entity.Asset;
import com.berkin.cryptoportfolio.entity.auth.User;
import com.berkin.cryptoportfolio.repository.AssetRepository;
import com.berkin.cryptoportfolio.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class AssetControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    UserRepository userRepository;
    @MockBean
    RestTemplate restTemplate;
    User user;
    User secondUser;

    @BeforeEach
    void setUp(){
        assetRepository.deleteAll();
        userRepository.deleteAll();
        user = userRepository.save(testDataBuilder.createUser("user"));
        secondUser = userRepository.save(testDataBuilder.createUser("newUser"));
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("{ \"bitcoin\": { \"eur\": 52012 }, \"ethereum\": { \"eur\": 2997.86 } }");
    }


    @Test
    @WithMockUser
    public void testAddAssetAddsNewAsset() throws Exception {
        CreateAssetRequest request = testDataBuilder.createAssetRequestDTO();
        assertEquals(0, assetRepository.count());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/assets/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        AssetDTO asset = mapper.readValue(result.getResponse().getContentAsString(),AssetDTO.class);

        assertEquals(1, assetRepository.count());
        assertEquals(request.getAmount(), asset.getAmount());
        assertEquals(request.getCryptoType(), asset.getCryptoType());
        assertEquals(request.getPurchaseDate().withOffsetSameInstant(ZoneOffset.UTC), asset.getPurchaseDate());
    }

    @Test
    @WithMockUser
    public void testAddAssetWillNotAddNewAssetWhenMissingName() throws Exception {
        CreateAssetRequest request = testDataBuilder.createAssetRequestDTO();
        request.setCryptoType(null);
        assertEquals(0, assetRepository.count());
        mockMvc.perform(MockMvcRequestBuilders.post("/assets/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        assertEquals(0, assetRepository.count());
    }

    @Test
    @WithMockUser
    public void testAddAssetAddsNewAssetForAuthUser() throws Exception {
        CreateAssetRequest request = testDataBuilder.createAssetRequestDTO();
        assertEquals(0, assetRepository.count());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/assets/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        AssetDTO assetDTO= mapper.readValue(result.getResponse().getContentAsString(),AssetDTO.class);
        Asset asset = assetRepository.findById(assetDTO.getId()).orElse(null);
        assertNotNull(asset);
        assertEquals(user.getId(), asset.getUserId());
    }

    @Test
    public void testAddAssetDoesNotAddAssetWithoutAuthUser() throws Exception {
        CreateAssetRequest request = testDataBuilder.createAssetRequestDTO();
        assertEquals(0, assetRepository.count());
        mockMvc.perform(MockMvcRequestBuilders.post("/assets/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @WithMockUser
    public void testListAssetsListsEveryAssetOfAuthUser() throws Exception {
        assertEquals(0, assetRepository.count());
        Asset asset = assetRepository.save(testDataBuilder.createAsset(user.getId()));
        assertEquals(1, assetRepository.count());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/assets/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        AssetDTO[] assets = mapper.readValue(result.getResponse().getContentAsString(),AssetDTO[].class);
        assertEquals(1, assets.length);
        AssetDTO assetDTO = assets[0];
        assertEquals(asset.getId(), assetDTO.getId());
        assertEquals(asset.getAmount(), assetDTO.getAmount());
        assertEquals(asset.getName(), assetDTO.getCryptoType());
        assertEquals(asset.getPurchaseDate().withOffsetSameInstant(ZoneOffset.UTC), assetDTO.getPurchaseDate());
    }

    @Test
    @WithMockUser
    public void testDeleteAssetDeletesAssetOfUser() throws Exception {
        Asset asset = assetRepository.save(testDataBuilder.createAsset(user.getId()));
        assertEquals(1, assetRepository.count());
        mockMvc.perform(MockMvcRequestBuilders.delete("/assets/{assetId}", asset.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertEquals(0, assetRepository.count());
    }
    @Test
    @WithMockUser("newUser")
    public void testDeleteAssetDoesNotDeleteAssetOfAnotherUser() throws Exception {
        Asset asset = assetRepository.save(testDataBuilder.createAsset(user.getId()));
        assertEquals(1, assetRepository.count());
        assertEquals(2, userRepository.count());
            mockMvc.perform(MockMvcRequestBuilders.delete("/assets/{assetId}", asset.getId()))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof RuntimeException))
                    .andExpect(result -> assertEquals("Asset cannot be deleted", result.getResolvedException().getMessage()))
                    .andReturn();
        assertEquals(1, assetRepository.count());
    }

    @Test
    @WithMockUser
    public void testDeleteNonExistingAsset() throws Exception {
        long nonExistingAssetId = 999L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/assets/{assetId}", nonExistingAssetId))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @WithMockUser
    public void testGetSupportedAssetNames() throws Exception {
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("[{\"id\":\"bitcoin\",\"symbol\":\"btc\",\"name\":\"Bitcoin\"},{\"id\":\"ethereum\",\"symbol\":\"eth\",\"name\":\"Ethereum\"}]");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/assets/supported"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        SupportedAssetsDTO[] supportedAssetsDTOS = mapper.readValue(result.getResponse().getContentAsString(),SupportedAssetsDTO[].class);

        assertEquals(2, supportedAssetsDTOS.length);
        assertEquals("bitcoin", supportedAssetsDTOS[0].getName());
        assertEquals("Bitcoin", supportedAssetsDTOS[0].getDisplayName());
        assertEquals("ethereum", supportedAssetsDTOS[1].getName());
        assertEquals("Ethereum", supportedAssetsDTOS[1].getDisplayName());

    }

}
