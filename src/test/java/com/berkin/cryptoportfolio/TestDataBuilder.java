package com.berkin.cryptoportfolio;

import com.berkin.cryptoportfolio.dto.CreateAssetRequest;
import com.berkin.cryptoportfolio.dto.SupportedAssetsDTO;
import com.berkin.cryptoportfolio.entity.Asset;
import com.berkin.cryptoportfolio.entity.auth.Role;
import com.berkin.cryptoportfolio.entity.auth.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
public class TestDataBuilder {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd'T'HH:mm:ssXXX");
    public CreateAssetRequest createAssetRequestDTO(){
        CreateAssetRequest request = new CreateAssetRequest();
        request.setCryptoType("bitcoin");
        request.setAmount(new BigDecimal("1.0"));
        request.setPurchaseDate(OffsetDateTime.parse("2024/01/05T22:00:31+03:00",formatter));
        return request;
    }

    public Asset createAsset(long userid){
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setName("bitcoin");
        asset.setAmount(new BigDecimal("1.0"));
        asset.setUserId(userid);
        asset.setPurchaseDate(OffsetDateTime.parse("2024/01/05T22:00:31+03:00",formatter));
        return asset;
    }
    public List<SupportedAssetsDTO> createSupportedAssets(){
        SupportedAssetsDTO[] supportedAssets = {
                new SupportedAssetsDTO("bitcoin", "Bitcoin"),
                new SupportedAssetsDTO("ethereum", "Ethereum"),
        };
        return Arrays.asList(supportedAssets);
    }

    public User createUser(String username){
        User user = new User();
        user.setUsername(username);
        return user;
    }

    public Role createRole(String name){
        Role role = new Role();
        role.setId(1L);
        role.setName(name);
        return role;
    }
}
