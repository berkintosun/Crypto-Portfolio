package com.berkin.cryptoportfolio.repository;

import com.berkin.cryptoportfolio.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

}
