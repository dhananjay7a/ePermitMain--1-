package com.organisation.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.organisation.model.IdGenerator;

@Repository
public class IDGeneratorUtilDao {

	private static final Logger log = LoggerFactory.getLogger(IDGeneratorUtilDao.class);

	public static synchronized IdGenerator getIdGenerator(String txnType, final Connection conn) throws Exception {
		log.info("Inside getIdGenerator()");
		IdGenerator idGenerator = new IdGenerator();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			String sql = "select ID,TXN_TYPE from UN_ID_GENERATOR where TXN_TYPE=? FOR UPDATE";
			pstmt = conn.prepareStatement(sql);
			pstmt.setObject(1, txnType);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				idGenerator.setId(rs.getBigDecimal("ID"));
				idGenerator.setTxnType(rs.getString("TXN_TYPE"));
			} else {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();

				sql = "INSERT INTO un_id_generator(txn_type, id, create_day, txn_desc) VALUES (?, ?, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setObject(1, txnType);
				pstmt.setObject(2, 1);
				pstmt.setObject(3, 1);
				pstmt.setObject(4, txnType);
				pstmt.executeUpdate();

				pstmt.close();
				idGenerator.setId(BigDecimal.ONE);
				idGenerator.setTxnType(txnType);
				return idGenerator;
			}
			// increment the value of ID
			idGenerator.setId(idGenerator.getId().add(BigDecimal.ONE));

			pstmt.close();
			String updateSql = "UPDATE UN_ID_GENERATOR set ID= ? where TXN_TYPE= ?	";

			pstmt = conn.prepareStatement(updateSql);
			pstmt.setObject(1, idGenerator.getId());
			pstmt.setObject(2, txnType);
			pstmt.executeUpdate();

			log.info("End getMaxIdGenerator>>> TxnType : " + txnType);

		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				log.error("Exception , ",e);
			}
		}

		return idGenerator;

	}

}
