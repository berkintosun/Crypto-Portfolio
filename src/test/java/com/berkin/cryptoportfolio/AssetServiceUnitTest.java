package com.berkin.cryptoportfolio;

import com.berkin.cryptoportfolio.dto.AssetDTO;
import com.berkin.cryptoportfolio.dto.CreateAssetRequest;
import com.berkin.cryptoportfolio.dto.SupportedAssetsDTO;
import com.berkin.cryptoportfolio.entity.Asset;
import com.berkin.cryptoportfolio.entity.auth.User;
import com.berkin.cryptoportfolio.repository.AssetRepository;
import com.berkin.cryptoportfolio.repository.UserRepository;
import com.berkin.cryptoportfolio.service.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@WithMockUser
class AssetServiceUnitTest extends AbstractUnitTest {

    @Autowired
    AssetRepository assetRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AssetService assetService;
    @MockBean
    RestTemplate restTemplate;

    User user;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        assetRepository.deleteAll();
        userRepository.deleteAll();
        user = userRepository.save(testDataBuilder.createUser("user"));
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("{ \"bitcoin\": { \"eur\": 52012 }, \"ethereum\": { \"eur\": 2997.86 } }");
    }

    @Test
    void testCreateAsset() {
        CreateAssetRequest request = testDataBuilder.createAssetRequestDTO();
        AssetDTO result = assetService.createAsset(user.getUsername(), request);

        assertEquals(request.getAmount(), result.getAmount());
        assertEquals(request.getCryptoType(), result.getCryptoType());
        assertEquals(request.getPurchaseDate(), result.getPurchaseDate());
    }

    @Test
    void testListAssets() {
        Asset asset = assetRepository.save(testDataBuilder.createAsset(user.getId()));
        List<AssetDTO> result = assetService.listAssets("user");

        assertEquals(1, result.size());
        AssetDTO assetDTO = result.get(0);
        assertEquals(asset.getId(), assetDTO.getId());
        assertEquals(asset.getAmount(), assetDTO.getAmount());
        assertEquals(asset.getName(), assetDTO.getCryptoType());
        assertEquals(asset.getPurchaseDate(), assetDTO.getPurchaseDate());
    }

    @Test
    void testGetAssetPrice() {
        BigDecimal btcResult = assetService.getAssetPrice("bitcoin", "eur");
        BigDecimal ethResult = assetService.getAssetPrice("ethereum", "eur");

        assertEquals(new BigDecimal("52012"), btcResult);
        assertEquals(new BigDecimal("2997.86"), ethResult);
    }

    @Test
    void testDeleteAsset() {
        Asset asset = assetRepository.save(testDataBuilder.createAsset(user.getId()));

        boolean result = assetService.deleteAsset("user", asset.getId());
        assertTrue(result);
        assertEquals(0,assetRepository.count());
    }

    @Test
    void testGetUserIdFromUsername() {
        long result = assetService.getUserIdFromUsername("user");
        assertEquals(user.getId(), result);
    }

    @Test
    void testGetSupportedAssetNames() {
        SupportedAssetsDTO[] supportedAssets = {
                new SupportedAssetsDTO("bitcoin", "Bitcoin"),
                new SupportedAssetsDTO("ethereum", "Ethereum"),
        };

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("[{\"id\":\"bitcoin\",\"symbol\":\"btc\",\"name\":\"Bitcoin\"},{\"id\":\"ethereum\",\"symbol\":\"eth\",\"name\":\"Ethereum\"}]");
        List<SupportedAssetsDTO> result = assetService.getSupportedAssetNames();

        assertEquals(Arrays.asList(supportedAssets), result);
    }
}
