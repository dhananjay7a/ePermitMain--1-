package com.organisation.util;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import com.organisation.model.IdGenerator;
import com.organisation.repository.IdGeneratorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdGeneratorUtil {

	private final IdGeneratorRepository idGenRepo;

	@Transactional
	public IdGenerator getNextId(String txnType) {
	    IdGenerator idGenerator = idGenRepo.findById(txnType)
	        .map(existing -> {
	            existing.setId(existing.getId().add(BigDecimal.ONE));
	            return existing;
	        })
	        .orElseGet(() -> {
	            IdGenerator newIdGen = new IdGenerator();
	            newIdGen.setTxnType(txnType);
	            newIdGen.setId(BigDecimal.ONE);
	            return newIdGen;
	        });

	    idGenerator.setTxnNo(txnType);
	    return idGenRepo.save(idGenerator);
	}

	
	public IdGenerator getIdGenerator(String txnType) {
	    IdGenerator idGenerator = this.getNextId(txnType);
	    // idGenerator has id and txnNo already set
	    return idGenerator;
	}
}
