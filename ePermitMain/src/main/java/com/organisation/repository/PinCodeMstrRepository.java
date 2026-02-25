package com.organisation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.epermit.register.dto.DistrictPinCodeResponseDTO;
import com.organisation.model.DistrictPinCodeMstr;
import com.organisation.model.PinCodeMstrId;

@Repository
public interface PinCodeMstrRepository
        extends JpaRepository<DistrictPinCodeMstr, PinCodeMstrId> {

    List<DistrictPinCodeMstr> findByIdPinCode(String pinCode);

    List<DistrictPinCodeMstr> findByIdPinCodeRefNo(String pinCodeRefNo);

    List<DistrictPinCodeMstr> findByStateNameAndIdPinCode(String stateName, String pinCode);

    List<DistrictPinCodeMstr> findByStateNameAndIdPinCodeRefNo(String stateName, String pinCodeRefNo);

    List<DistrictPinCodeMstr> findByStateName(String stateName);

    List<DistrictPinCodeMstr> findByDistrictNameAndStateName(String districtName, String stateName);

    @Query("""
                SELECT DISTINCT new com.epermit.register.dto.DistrictPinCodeResponseDTO(
                    upcm.id.pinCode,
                    upcm.blockName
                )
                FROM DistrictPinCodeMstr upcm
                JOIN StateDistrictMaster usdm
                    ON upcm.districtName = usdm.districtName
                WHERE usdm.id.districtCode = :districtCode
                  AND usdm.id.stateCode = :stateCode
            """)
    List<DistrictPinCodeResponseDTO> findPinCodesByStateCodeAndDistrictCode(@Param("stateCode") String stateCode,
            @Param("districtCode") String districtCode);

    @Query("""
            SELECT DISTINCT upcm.blockName
            FROM DistrictPinCodeMstr upcm
            JOIN StateDistrictMaster usdm
                ON upcm.districtName = usdm.districtName
            WHERE usdm.id.districtCode = :districtCode
              AND upcm.id.pinCode = :pinCode
            """)
    List<String> findBlockCodesByPinCodeAndDistrictCode(@Param("pinCode") String pinCode,
            @Param("districtCode") String districtCode);
}
