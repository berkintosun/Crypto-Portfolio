package com.berkin.cryptoportfolio.controller;

import com.berkin.cryptoportfolio.dto.AssetDTO;
import com.berkin.cryptoportfolio.dto.CreateAssetRequest;
import com.berkin.cryptoportfolio.dto.SupportedAssetsDTO;
import com.berkin.cryptoportfolio.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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

    @GetMapping("/all")
    @ResponseBody
    List<AssetDTO> listAssets(Principal principal){
        return assetService.listAssets(principal.getName());
    }

    @DeleteMapping("/{assetId}")
    ResponseEntity deleteAsset(@PathVariable long assetId, Principal principal){
        assetService.deleteAsset(principal.getName(),assetId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/supported")
    public List<SupportedAssetsDTO> getSupportedAssetNames(){
        return assetService.getSupportedAssetNames();
    }

}
