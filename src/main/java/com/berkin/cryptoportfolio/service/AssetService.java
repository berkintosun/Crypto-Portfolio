package com.berkin.cryptoportfolio.service;

import com.berkin.cryptoportfolio.dto.CreateAssetRequest;
import com.berkin.cryptoportfolio.dto.AssetDTO;
import com.berkin.cryptoportfolio.dto.SupportedAssetsDTO;
import com.berkin.cryptoportfolio.entity.Asset;
import com.berkin.cryptoportfolio.entity.auth.User;
import com.berkin.cryptoportfolio.repository.AssetRepository;
import com.berkin.cryptoportfolio.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AssetService {

    AssetRepository assetRepository;
    UserRepository userRepository;
    @Autowired
    @Lazy
    AssetService self;
    @Value("${crypto.path}")
    String url;
    @Value("${crypto.supported}")
    String supportedUrl;

    @Autowired
    RestTemplate restTemplate;

    public AssetService(AssetRepository assetRepository, UserRepository userRepository) {
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
    }

    public AssetDTO createAsset(String username, CreateAssetRequest request){
        if(request.getCryptoType() == null || request.getPurchaseDate() == null || request.getAmount() == null){
            throw new RuntimeException("Missing field");
        }
        Asset asset = new Asset();
        asset.setName(request.getCryptoType());
        asset.setAmount(request.getAmount());
        asset.setUserId(getUserIdFromUsername(username));
        asset.setPurchaseDate(request.getPurchaseDate());
        assetRepository.save(asset);
        AssetDTO response = new AssetDTO();
        response.setId(asset.getId());
        response.setAmount(asset.getAmount());
        response.setPurchaseDate(asset.getPurchaseDate());
        response.setCryptoType(asset.getName());
        response.setMarketValue(asset.getAmount().multiply(self.getAssetPrice(asset.getName(),"eur")));
        return response;
    }

    public List<AssetDTO> listAssets(String username){
        List<Asset> assets = assetRepository.findByUserId(getUserIdFromUsername(username));
        List<AssetDTO> assetDTOS = new ArrayList<>();
        assets.stream().forEach(asset -> {
            AssetDTO dto = new AssetDTO();
            dto.setId(asset.getId());
            dto.setAmount(asset.getAmount());
            dto.setCryptoType(asset.getName());
            dto.setPurchaseDate(asset.getPurchaseDate());
            dto.setMarketValue(dto.getAmount().multiply(self.getAssetPrice(dto.getCryptoType(),"eur")));
            assetDTOS.add(dto);
        });
        return assetDTOS;
    }

    @Cacheable("marketrate")
    public BigDecimal getAssetPrice(String name, String currency){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String query = String.format("?ids=%s&vs_currencies=%s&precision=18",name,currency);
        String response = restTemplate.getForObject(url.concat(query), String.class);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            return new BigDecimal(jsonNode.get(name).get(currency).asText());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error rate exceeded");
        }
    }

    @CacheEvict(value = "marketrate", allEntries = true)
    @Scheduled(fixedRateString = "${crypto.market-clean-rate}")
    public void evictMarketRateCaches(){}

    public boolean deleteAsset(String username, long assetId) {
        Asset asset = assetRepository.findById(assetId).orElse(null);
        if(asset == null || asset.getUserId() != getUserIdFromUsername(username)){
            throw new RuntimeException("Asset cannot be deleted");
        }
        assetRepository.deleteById(assetId);
        return true;
    }

    public long getUserIdFromUsername(String username){
        User user = userRepository.findByUsername(username).orElse(null);
        if(user == null){
            throw new RuntimeException("User cannot found"); //Technically not necessary as this function wont execute without proper principal
        }
        return user.getId();
    }

    @Cacheable("supportedcoins")
    public List<SupportedAssetsDTO> getSupportedAssetNames() {
        try {
            String response = restTemplate.getForObject(supportedUrl, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            SupportedAssetsDTO[] supportedAssetsDTOS = objectMapper.readValue(response,SupportedAssetsDTO[].class);
            return Arrays.asList(supportedAssetsDTOS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error rate exceeded", e);
        }
    }
}