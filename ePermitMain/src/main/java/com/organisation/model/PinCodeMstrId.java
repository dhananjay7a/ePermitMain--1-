package com.organisation.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PinCodeMstrId implements Serializable {

    @Column(name = "pin_code_ref_no")
    private String pinCodeRefNo;

    @Column(name = "pin_code")
    private String pinCode;

    public PinCodeMstrId() {
    }

    public PinCodeMstrId(String pinCodeRefNo, String pinCode) {
        this.pinCodeRefNo = pinCodeRefNo;
        this.pinCode = pinCode;
    }

    // REQUIRED: equals & hashCode for composite key
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PinCodeMstrId))
            return false;
        PinCodeMstrId that = (PinCodeMstrId) o;
        return Objects.equals(pinCodeRefNo, that.pinCodeRefNo)
                && Objects.equals(pinCode, that.pinCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pinCodeRefNo, pinCode);
    }

    // getters & setters
    public String getPinCodeRefNo() {
        return pinCodeRefNo;
    }

    public void setPinCodeRefNo(String pinCodeRefNo) {
        this.pinCodeRefNo = pinCodeRefNo;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }
}
