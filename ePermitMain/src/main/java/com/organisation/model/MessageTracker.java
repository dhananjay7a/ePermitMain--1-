package com.organisation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="tbl_message_tracker")
public class MessageTracker implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
	private Long msgId;
	
	private String transcode;
	private String msgType;
	private String message;
	private String mobileNo;
	private String status;
	private String remarks;
	private String returnMsg;
	private String errorMsg;
	private boolean found;
	private String owner;
	private String messageRecipients;
	private String messageSubject;
	private int hasAttachments;
	private String attachLocation;	
	private LocalDateTime createdOn;
	
}
