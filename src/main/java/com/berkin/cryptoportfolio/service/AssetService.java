package com.berkin.cryptoportfolio.service;

import com.berkin.cryptoportfolio.dto.AssetDTO;
import com.berkin.cryptoportfolio.dto.CreateAssetRequest;
import com.berkin.cryptoportfolio.entity.Asset;
import com.berkin.cryptoportfolio.repository.AssetRepository;
import com.berkin.cryptoportfolio.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

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

    public AssetDTO createAsset(Long id, CreateAssetRequest request){
        Asset asset = new Asset();
        asset.setName(request.getCryptoType());
        asset.setAmount(request.getAmount());
        asset.setUserId(id);
        asset.setPurchaseDate(request.getPurchaseDate());
        assetRepository.save(asset);
        AssetDTO response = new AssetDTO();
        response.setAmount(asset.getAmount());
        response.setPurchaseDate(asset.getPurchaseDate());
        response.setCryptoType(asset.getName());
        response.setMarketValue(asset.getAmount().multiply(BigDecimal.valueOf(1.4583)));
        return response;
    }


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
}