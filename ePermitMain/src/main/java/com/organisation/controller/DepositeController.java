package com.organisation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epermit.Exception.EntityNotFoundException;
import com.epermit.Exception.UnauthorizedException;
import com.epermit.register.responsehandler.ApiResponses;
import com.epermit.register.responsehandler.ResponseBean;
import com.organisation.dto.ApproveRejectDepositRequestDTO;
import com.organisation.dto.DepositWithdrawalBookDTO;
import com.organisation.model.DepositWithdrawalBook;
import com.organisation.service.FundsOperationService;

@RestController
@RequestMapping("/api/funds")
public class DepositeController {
    @Autowired
    private  FundsOperationService fundsOperationService;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponses> depositFund( @RequestBody DepositWithdrawalBookDTO depositRequest ,
                                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        ResponseBean responseBean = new ResponseBean();

        try {
          
            DepositWithdrawalBook response = fundsOperationService.depositFund(depositRequest , authHeader);
            
            if (response!=null) {
                responseBean.InsertResponse("Success");
                return ResponseEntity.ok(responseBean.getResponse());
            } else {
                responseBean.InsertResponse("Fail");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBean.getResponse());
            }

        } catch (UnauthorizedException e) {
        	e.printStackTrace();
            responseBean.AllResponse("TokenInvalid", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBean.getResponse());
        }catch (Exception e) {
        	e.printStackTrace();
            responseBean.InsertResponse("Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBean.getResponse());
        }
    }
    
    @PostMapping("/getFund")
    public ResponseEntity<ApiResponses> getFundDetails(@RequestBody DepositWithdrawalBookDTO request,
        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        ResponseBean responseBean = new ResponseBean();

        try {
        
            // Call service method to get fund details
            List<DepositWithdrawalBookDTO> fundDetails = fundsOperationService.getFundDetails(request, authHeader);
            if(!fundDetails.isEmpty()) {
                responseBean.AllResponse("Success", fundDetails);
                return ResponseEntity.ok(responseBean.getResponse());
            } else {
                throw new EntityNotFoundException("No fund details found");
            }

        } catch (UnauthorizedException e) {
        	e.printStackTrace();
            responseBean.AllResponse("TokenInvalid", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBean.getResponse());
        }catch (EntityNotFoundException e) {
        	e.printStackTrace();
            responseBean.AllResponse("NoDataFound", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBean.getResponse());

        }catch (Exception e) {
        	e.printStackTrace();
            responseBean.InsertResponse("Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBean.getResponse());
        }
    }
    @PostMapping("/approveRejectDeposit")
    public ResponseEntity<ApiResponses> approveRejectDeposit(@RequestBody ApproveRejectDepositRequestDTO request,
             @RequestHeader(value = "Authorization", required = false) String authHeader) {
        ResponseBean responseBean = new ResponseBean();
        try {
        
            DepositWithdrawalBook response = fundsOperationService.approveRejectDeposit(
                request.getTrnsNo(),
                request.getDecision(),
                request.getRemarks(),
                authHeader
            );
            if(response != null) {
                responseBean.InsertResponse("Success");
                return ResponseEntity.ok(responseBean.getResponse());
            } else {
                responseBean.InsertResponse("Fail");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBean.getResponse());
            }

        } catch (UnauthorizedException e) {
        	e.printStackTrace();
            responseBean.AllResponse("TokenInvalid", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBean.getResponse());
        }catch (Exception e) {
        	e.printStackTrace();
            responseBean.InsertResponse("Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBean.getResponse());
        }
    }


}
