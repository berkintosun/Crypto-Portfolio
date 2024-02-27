package com.berkin.cryptoportfolio.controller;

import com.berkin.cryptoportfolio.dto.CreateAssetRequest;
import com.berkin.cryptoportfolio.dto.AssetDTO;
import com.berkin.cryptoportfolio.dto.SupportedAssetsDTO;
import com.berkin.cryptoportfolio.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/assets")
@Tag(name = "Asset",description = "testing")
public class AssetController {
    @Autowired
    AssetService assetService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            description = "It adds asset and links the created asset with a user by using auth principal.",
            summary = "Endpoint for adding an Asset",
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Unauthorized",
                            responseCode = "403"
                    )
            }
    )
    @ResponseBody
    AssetDTO addAsset(@RequestBody CreateAssetRequest assetRequest, Principal principal){
        return assetService.createAsset(principal.getName(),assetRequest);
    }

    @GetMapping("/all")
    @ResponseBody
    @Operation(
            description = "It returns all the assets that belongs to the user. This endpoint does not aggregate the similar type coins.",
            summary = "Endpoint for listing individual assets",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized",
                            responseCode = "403"
                    )
            }
    )
    List<AssetDTO> listAssets(Principal principal){
        return assetService.listAssets(principal.getName());
    }

    @DeleteMapping("/{assetId}")
    @Operation(
            description = "It deletes an asset that belongs to the user.",
            summary = "Endpoint for deleting an Asset",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized",
                            responseCode = "403"
                    )
            }
    )
    ResponseEntity deleteAsset(@PathVariable long assetId, Principal principal){
        assetService.deleteAsset(principal.getName(),assetId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/supported")
    @Operation(
            description = "It returns the supported list of coins with the ids. These ids needs to be used when creating an asset.",
            summary = "Returns the supported coin list with their id names.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized",
                            responseCode = "403"
                    )
            }
    )
    public List<SupportedAssetsDTO> getSupportedAssetNames(){
        return assetService.getSupportedAssetNames();
    }
}
