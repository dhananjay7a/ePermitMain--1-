package com.organisation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.organisation.model.MessageTracker;

@Repository
public interface MessageTrackerRepository extends JpaRepository<MessageTracker, Long>{
    Optional<MessageTracker> findTopByMobileNoOrderByCreatedOnDesc(String mobileNo);
    @Query(value = "SELECT message FROM epermit.tbl_message_tracker " +
            "WHERE message LIKE CONCAT(:userId, '%') AND mobile_no = :mobile " +
            "ORDER BY msg_id DESC LIMIT 1", 
    nativeQuery = true)
String findLatestMessage(@Param("mobile") String mobile, @Param("userId") String userId);




}
