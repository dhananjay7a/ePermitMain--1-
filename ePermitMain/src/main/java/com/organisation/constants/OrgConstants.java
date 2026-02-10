package com.organisation.constants;

public class OrgConstants {
	public static final String YES = "Y";
	public static final String NO = "N";

	public static final String SELF_REGISTRATION = "SELF";

	public static final String ORG_CATEGORY_STATE_OWNER = "OWS";
	public static String OWNER_STRING = "EPERMIT";

	public static final int OTP_SIZE = 6;
	public static final int DEFAULT_PASSWORD_SIZE = 8;
	public static final String IS_OTP_TRUE = "T";
	public static final String IS_OTP_FALSE = "F";

	public static final String SUCCESS_STRING = "1|";
	public static final String FAILURE_STRING = "0|";
	public static final String DUPLICATE_STRING = "2|";
	public static final String EXISTS_STRING = "3|";

	public static final String CM_ROLE_STRING = "CM";
	public static final String TM_ROLE_STRING = "TM";
	public static final String TCM_ROLE_STRING = "TCM";
	public static final String DEFAULT_ROLE_STRING = "DF";

	public static final String SMS_TRANSCODE = "354";
	public static final String SMS_TRANSCODE1 = "801";

	public static final String transcode = "TC";
	public static final String mobileNo = "MBLNO";
	public static final String msgTransaction = "MSGTXT";
	public static final String responseCode = "RCODE";

	public static final String DEFAULT_SMS_STATUS = "P";

	public static interface MESSAGE_TYPE {
		public static final String SMS = "S";
		public static final String EMAIL = "E";
	}

	public static interface ERROR_CODES {

		public static final long NO_ERROR = 500000;
		public static final long REGISTRATION_FAILED = 500001;
		public static final long HOST_SIGN_UP_NOT_ALLOWED = 500002;
		public static final long ORG_NOT_FOUND = 500003;

		public static final long FILE_FORMAT_MISMATCH = 500115;

	}

	public static interface REGISTRATION_STATUS {
		public static final String ENROLLED = "EN";
		public static final String SAVED = "SV";
		public static final String PENDING = "PD";
		public static final String SUBMITTED = "SB";
		public static final String REJECTED = "RJ";
		public static final String SCRUTINY = "SC";
		public static final String REVIEWED = "RV";
		public static final String FINAL_APPROVED = "FA";
		public static final String DIGITAL_APPROVED = "DA";
		public static final String DSIGN_PENDING = "DP";
		public static final String DIGITAL_SIGNED = "DS";
	}

	public static interface ORG_CATEGORY_ID_SEQ {

		public static final String REG = "REG";

		public static final String TRADER = "TRD";
		public static final String PROCESSOR = "PRS";
		public static final String COMMISSION_AGENT = "COM";
		public static final String BROKER = "BKR";
		public static final String SURVEYOR = "SVR";
		public static final String WEIGHMAN = "WEI";
		public static final String MEASURER = "MSR";
		public static final String WAREHOUSEMAN = "WHM";
		public static final String PROCURER_PRESERVER = "PRV";
		public static final String SELLER_PURCHASER = "SLR";

		public static final String STORAGE = "STO";
		public static final String HAT = "HAT";
		public static final String BAZAR = "BAZ";
		public static final String MELA = "MEL";
		public static final String OTHER_PLACE_OF_SALE_PURCHASE = "OTH";
		public static final String PRIVATE_MARKET_APPLICATION = "PMA";
		public static final String RENTER = "REN";

	}

	public static interface ORG_CATEGORY {

		public static final String ORG_CATEGORY_HOST = "HST";
		public static final String ORG_CATEGORY_BOARD = "BRD";

		public static final String ORG_CATEGORY_RMC = "RMC";

		public static final String ORG_CATEGORY_TRADER = "TRD";
		public static final String ORG_CATEGORY_PROCESSOR = "PRS";
		public static final String ORG_CATEGORY_COMMISSION_AGENT = "COM";
		public static final String ORG_CATEGORY_BROKER = "BKR";
		public static final String ORG_CATEGORY_SURVEYOR = "SVR";
		public static final String ORG_CATEGORY_WEIGHMAN = "WEI";
		public static final String ORG_CATEGORY_MEASURER = "MSR";
		public static final String ORG_CATEGORY_WAREHOUSEMAN = "WHM";
		public static final String ORG_CATEGORY_PROCURER_PRESERVER = "PRV";
		public static final String ORG_CATEGORY_SELLER_PURCHASER = "SLR";
		public static final String ORG_CATEGORY_TRANSPOTER = "TRS";

		public static final String ORG_CATEGORY_HAT = "HAT";
		public static final String ORG_CATEGORY_STORAGE = "STO";
		public static final String ORG_CATEGORY_BAZAR = "BAZ";
		public static final String ORG_CATEGORY_MELA = "MEL";
		public static final String ORG_CATEGORY_OTHER_PLACE_OF_SALE_PUR = "OTH";
		public static final String ORG_CATEGORY_AGENCY = "AGS";
		public static final String ORG_CATEGORY_MILLER = "MIL";
		public static final String ORG_CATEGORY_WAREHOUSE = "WSP";
		public static final String ORG_CATEGORY_PRIVATE_MARKET_APPLICATION = "PMA";
		public static final String ORG_CATEGORY_RENTER = "REN";

	}

	public static interface ORG_PREFIX_CHARACTER {
		public static final String NEW_ORG_PREFIX_CHARACTER = "N";

		public static final String HST_PREFIX_CHARACTER = "H";
		public static final String BOARD_PREFIX_CHARACTER = "B";
		public static final String RMC_OFFICER_PREFIX_CHARACTER = "R";

		public static final String TRADER_PREFIX_CHARACTER = "T";
		public static final String PROCESSOR_PREFIX_CHARACTER = "P";
		public static final String COMMISSION_AGENT_PREFIX_CHARACTER = "C";
		public static final String BROKER_PREFIX_CHARACTER = "B";
		public static final String SURVEYOR_PREFIX_CHARACTER = "V";
		public static final String WEIGHMAN_PREFIX_CHARACTER = "E";
		public static final String MEASURER_PREFIX_CHARACTER = "M";
		public static final String WAREHOUSEMAN_PREFIX_CHARACTER = "W";
		public static final String PROCURER_PRESERVER_PREFIX_CHARACTER = "O";
		public static final String SELLER_PURCHASER_PREFIX_CHARACTER = "S";

		public static final String STORAGE_PREFIX_CHARACTER = "G";
		public static final String HAT_PREFIX_CHARACTER = "A";
		public static final String MELA_PREFIX_CHARACTER = "L";
		public static final String BAZAR_PREFIX_CHARACTER = "Z";
		public static final String OTHER_PLACE_SALE_PUR_PREFIX_CHARACTER = "X";
		public static final String AGENCY_PREFIX_CHARACTER = "E";
		public static final String MILLER_PREFIX_CHARACTER = "I";
		public static final String WAREHOUSE_PREFIX_CHARACTER = "U";
		public static final String PRIVATE_MARKET_PREFIX_CHARACTER = "K";
		public static final String RENTER_PREFIX_CHARACTER = "Y";

	}

	public static interface RENEWAL_STATUS {
		public static final String NOT_APPLICABLE = "N";
		public static final String ELIGIBLE = "E";
		public static final String PENDING = "P";
		public static final String COMPLETED = "C";
		public static final String APPLICABLE = "Y";
	}

	public static final String FIRST_LOGIN_FLAG = "Y";
	public static final String FIRST_LOGIN_FLAG_NO = "N";
	public static final String FIRST_LOGIN_ROLE = "0";
	public static final String ORG_CATEGORY_RP = "Repositor Participant";
	public static final String IS_FREEZED = "F";
	public static final String IS_ACTIVE = "T";
	public static final String IS_ACTIVE1 = "Y";
	public static final String IS_NOTACTIVE = "N";
	public static final String IS_ANNOUNCEMENT_NOT_ACTIVE = "F";

	public static final String FALSE = "F";
	public static final String TRUE = "T";

	public static final String PAYOUT_TYPE = "B";

	public static final String USER_ID = "userId";
	public static final String USER_INVALID_SESSION = "invalidSession";
	public static final String USER_SESSION_TIMEOUT = "sessionTimeOut";
	public static final String USER_SESSION_EXPIRED = "sessionExpired";
	public static final String USER_SESSION_CHANGED = "sessionChanged";
	public static final String USER_SESSION_VALID = "validSession";

	public static interface CATEGORY_TYPE {
		public static final String HOST = "H";
		public static final String RMC = "R";
		public static final String MEMBER = "M";
		public static final String HAAT_BAZAR = "B";
	}

	public static interface TRADER_ROLES {
		public static final String DEFAULT_ROLE = "CR";
		public static final String RENEWAL_ROLE = "TR";
		public static final String TRADER_ROLE = "TD";
	}

	public static interface ACCOUNT_TYPE {
		public static final String MEMBER = "M";
		public static final String RMC_BANK = "L";
	}

	public static interface CONFIGURATIONS {
		public static final String YES_BANK_CLIENT_ID = "YES_BANK_CLIENT_ID";

	}

	public static interface REGISTRATION_TYPE {
		public static final String NEW_REGISTRATION = "RG";
		public static final String RENEWAL = "RW";
	}

	public static interface RMC_OFFICE_CODE {
		public static final String RMC_ALIPURDUAR = "RMCA";
		public static final String RMC_BANKURA = "RMCB";
		public static final String RMC_BIRBHUM = "RMCBB";
		public static final String RMC_COOCBEHAR = "RMCC";
		public static final String RMC_DAKSHIN_DINAJPUR = "RMCD";
		public static final String RMC_DARJEELING = "RMCDJ";
		public static final String RMC_HOOGHLY = "RMCH";
		public static final String RMC_HOWRAH = "RMCHW";
		public static final String RMC_JALPAIGURI = "RMCJ";
		public static final String RMC_JHARGRAM = "RMCJG";
		public static final String RMC_KALIMPONG = "RMCKP";
		public static final String RMC_KOLKATA = "RMCK";
		public static final String RMC_MALDA = "RMCM";
		public static final String RMC_MURSHIDABAD = "RMCMR";
		public static final String RMC_NADIA = "RMCN";
		public static final String RMC_NORTH_24_PARGANAS = "RMCN24";
		public static final String RMC_PASCHIM_BARDHAMAN = "RMCPBD";
		public static final String RMC_PASCHIM_MEDINIPUR = "RMCPM";
		public static final String RMC_PURBA_BARDHAMAN = "RMCEBD";
		public static final String RMC_PURBA_MEDINIPUR = "RMCEPM";
		public static final String RMC_PURULIA = "RMCP";
		public static final String RMC_SOUTH_24_PARGANAS = "RMCS24";
		public static final String RMC_UTTAR_DINAJPUR = "RMCUD";
	}

	public static interface RMC_OFFICE_NAME {
		public static final String RMC_ALIPURDUAR = "RMC ALIPURDUAR";
		public static final String RMC_BANKURA = "RMC BANKURA";
		public static final String RMC_BIRBHUM = "RMC BIRBHUM";
		public static final String RMC_COOCBEHAR = "RMC COOCH BEHAR";
		public static final String RMC_DAKSHIN_DINAJPUR = "RMC DAKSHIN DINAJPUR";
		public static final String RMC_DARJEELING = "RMC DARJEELING";
		public static final String RMC_HOOGHLY = "RMC HOOGHLY";
		public static final String RMC_HOWRAH = "RMC HOWRAH";
		public static final String RMC_JALPAIGURI = "RMC JALPAIGURI";
		public static final String RMC_JHARGRAM = "RMC JHARGRAM";
		public static final String RMC_KALIMPONG = "RMC KALIMPONG";
		public static final String RMC_KOLKATA = "RMC KOLKATA";
		public static final String RMC_MALDA = "RMC MALDA";
		public static final String RMC_MURSHIDABAD = "RMC MURSHIDABAD";
		public static final String RMC_NADIA = "RMC NADIA";
		public static final String RMC_NORTH_24_PARGANAS = "RMC NORTH 24 PARGANAS";
		public static final String RMC_PASCHIM_BARDHAMAN = "RMC PASCHIM BARDHAMAN";
		public static final String RMC_PASCHIM_MEDINIPUR = "RMC PASCHIM MEDINIPUR";
		public static final String RMC_PURBA_BARDHAMAN = "RMC PURBA BARDHAMAN";
		public static final String RMC_PURBA_MEDINIPUR = "RMC PURBA MEDINIPUR";
		public static final String RMC_PURULIA = "RMC PURULIA";
		public static final String RMC_SOUTH_24_PARGANAS = "RMC SOUTH 24 PARGANAS";
		public static final String RMC_UTTAR_DINAJPUR = "RMC UTTAR DINAJPUR";
	}

}
