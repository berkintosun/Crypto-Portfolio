package com.berkin.cryptoportfolio.controller;

import com.berkin.cryptoportfolio.dto.AssetDTO;
import com.berkin.cryptoportfolio.dto.CreateAssetRequest;
import com.berkin.cryptoportfolio.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/assets")
public class AssetController {
    @Autowired
    AssetService assetService;

    @PostMapping("/{userid}")
    @ResponseBody
    AssetDTO addAsset(@RequestBody CreateAssetRequest assetRequest, Principal principal){
        return assetService.createAsset(principal.getName(),assetRequest);
    }

}
