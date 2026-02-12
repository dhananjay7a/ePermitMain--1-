package com.epermit.register.responsehandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class ResponseBean {
    @Autowired
    private ApiResponses response;

    private HttpServletResponse getHttpServletResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attributes != null) ? attributes.getResponse() : null;
    }

    public ApiResponses getResponse() {
        return response;
    }

    public void setResponse(ApiResponses response) {
        this.response = response;
    }

    public void AllResponse(String type, Object data) {
        if (this.response == null) {
            this.response = new ApiResponses();
        }
        HttpServletResponse httpResponse = getHttpServletResponse();
        if (httpResponse == null) {
            throw new IllegalStateException("HttpServletResponse is not available");
        }
        switch (type) {
            case "Success":
                this.response.setCode("200");
                this.response.setMessage("Success");
                this.response.setData(data);
                break;
            case "Exists":
                this.response.setCode("100");
                this.response.setMessage("Already Exists");
                this.response.setData(data);
                break;
            case "SuccessUpdt":
                this.response.setCode("200");
                this.response.setMessage("Update Successfully !!");
                break;
            case "OTP":
                this.response.setCode("200");
                this.response.setMessage("OTP Generated");
                this.response.setData(data);
                break;
            case "First":
                this.response.setCode("403");
                this.response.setMessage("First Time");
                this.response.setData(data);
                break;
            case "MAXOTP":
                this.response.setCode("429");
                this.response.setMessage("Maximum OTP requests reached. Please try again after 10 Minutes.");
                break;
            case "PWDSAME":
                this.response.setCode("400");
                this.response.setMessage("Password is same as your Last 3 Password !!");
                break;
            case "PWDINVAL":
                this.response.setCode("400");
                this.response.setMessage("Password Format is Not Matched !!");
                break;
            case "PWDCHANGE":
                this.response.setCode("200");
                this.response.setMessage("Password has been Chanded Succesfully !!");
                break;
            case "Pw_Expire":
                this.response.setCode("205");
                this.response.setMessage("Password Expired");
                this.response.setData(data);
                break;
            case "Expired":
                this.response.setCode("400");
                this.response.setMessage("Session Expired");
                httpResponse.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
                break;
            case "Notfound":
                this.response.setCode("400");
                this.response.setMessage("No Record Found");
                this.response.setData(null);
                break;
            case "NotfoundMob":
                this.response.setCode("400");
                this.response.setMessage("No Mobile Number Found");
                this.response.setData(null);
                break;
            case "Error":
                this.response.setCode("400");
                this.response.setMessage("Something went wrong");
                this.response.setData(data);
                break;
            case "Nulltype":
                this.response.setCode("400");
                this.response.setMessage("Provide Inputs");
                break;
            case "Invalid":
                this.response.setCode("400");
                this.response.setMessage("Invalid Credentials");
                break;
            case "InvalidOTP":
                this.response.setCode("400");
                this.response.setMessage("Invalid OTP");
                break;
            case "TokenMissing":
                this.response.setCode("400");
                this.response.setMessage("Provide Valid Token");
                break;
            case "LogoutSucces":
                this.response.setCode("200");
                this.response.setMessage("Logged Out Successfully");
                break;
            case "LogoutExist":
                this.response.setCode("400");
                this.response.setMessage("Already Logged Out");
                break;
            case "Unauthorized":
                this.response.setCode("400");
                this.response.setMessage("Unauthorized");
                break;
            case "TokenIdeal":
                // httpResponse.setStatus(HttpServletResponse.SC_OK); // 200
                this.response.setCode("100");
                this.response.setMessage("Token is Ideal !");
                this.response.setData(data);
                break;
            case "Partial":
                // httpResponse.setStatus(HttpServletResponse.SC_OK); // 200
                this.response.setCode("200");
                this.response.setMessage("Error");
                this.response.setData(data);
                break;
            case "Duplicate":
                // httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                this.response.setCode("404");
                this.response.setMessage("Duplicate !!");
                break;
            case "NoContent":
                // httpResponse.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
                this.response.setCode("204");
                this.response.setMessage("No Data Given !!");
                break;
            case "TokenInvalid":
                this.response.setCode("401");
                this.response.setMessage("Invalid or Expired Token");
                break;

            case "NoDataFound":
                this.response.setCode("404");
                this.response.setMessage("No Data Found");
                this.response.setData(null);
                break;

            case "FeeNotPaid":
                this.response.setCode("400");
                this.response.setMessage("Registration fee not paid");
                break;
        }
    }

    public void InsertResponse(String type) {
        if (this.response == null) {
            this.response = new ApiResponses();
        }

        switch (type) {
            case "Success":
                this.response.setCode("200");
                this.response.setMessage("Data Saved Successfully");
                break;
            case "Fail":
                this.response.setCode("200");
                this.response.setMessage("Data Not Saved,Try Again later !");
                break;
            case "InvalidEmail":
                this.response.setCode("400");
                this.response.setMessage("Invalid Email Format");
                break;
            case "InvalidMobile":
                this.response.setCode("400");
                this.response.setMessage("Invalid Mobile: Must be 10 digits");
                break;
            case "InvalidName":
                this.response.setCode("400");
                this.response.setMessage("Invalid contact name: Must not contain special characters or numbers");
                break;
            case "InvalidOrgName":
                this.response.setCode("400");
                this.response.setMessage("Invalid Organization name : Can't be null");
                break;
            case "InvalidContDet":
                this.response.setCode("400");
                this.response.setMessage("No contact details provided");
                break;
            case "NullScheme":
                this.response.setCode("400");
                this.response.setMessage("Invalid department or scheme ID");
                break;
            case "Error":
                this.response.setCode("400");
                this.response.setMessage("Something went wrong");
                break;
        }
    }

}
