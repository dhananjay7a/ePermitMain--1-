package com.organisation.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;



@Component
public class TokenService {
	
//	 @Autowired
//    private MasterRepository masterRepository;
//	@Autowired
//    private Key key;
	
	 @Value("${jwt.expiry}")
	 private int jwtExp;
	
	 @Value("${jwt.secret}")
	 private String secretKey;
	 
//	 private String secretKey="your_fixed_secret_key_here======";
//	public boolean validateTokenAndReturnBool(String token, String ip) {
//        try {
//
//        	
//            Claims claims = validateToken(token, secretKey);
//
//            if (claims != null) {
//                String userId = claims.getSubject();
//                String cd = claims.get("unique_name", String.class);
////                
//                
//                List<Map<String, Object>> data = masterRepository.CheckLogoutForToken(cd, userId, token,ip);
//                ////System.out.println("----"+data);
//                if (data != null && !data.isEmpty()) {
//                    boolean isLogout = (Boolean) data.get(0).get("check_flag");
//                    ////System.out.println(isLogout);
//                    return !isLogout; 
//                }
//            }
//            return false;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
	 public String generateToken(String uid, String username, String role) {
		    Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
		    Date expiryTime = new Date(System.currentTimeMillis() + jwtExp * 60 * 1000);

		    Date now = new Date();
		    Claims claims = Jwts.claims().setSubject(uid);
		    claims.put("unique_name", username);
		    claims.put("key", "user1-key");
		   // claims.put("role", role);
		    claims.put("expiry", expiryTime.toInstant().toString());

		    return Jwts.builder()
		            .setClaims(claims)
		            .setIssuedAt(now)
		            .setExpiration(expiryTime)
		            .setHeaderParam("typ", "JWT")
		            .signWith(key, SignatureAlgorithm.HS256)
		            .compact();
		}

	public Claims validateToken(String token) {
		
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            // Invalid token
            return null;
        }
    }
	
//	 public String generateRefreshToken(String token, String secretKey, String ip) throws Exception {
//	        try {
////	        	Instant  dbtime = Timestamp.valueOf(masterRepository.GetDBTime().get(0).values().iterator().next().toString()).toInstant();
//	            Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
//	            JwtParser parser = Jwts.parserBuilder().setSigningKey(key).build();
//	            Claims claims2 = validateToken(token, secretKey);
//	            if(claims2==null) {
//	            	return null;
//	            }
//	            Claims claims = parser.parseClaimsJws(token).getBody();
//	            String expiryClaim = claims.get("expiry", String.class);
//	            if (expiryClaim == null) {
//	                throw new Exception("Token is missing expiry claim");
//	            }
//
//	            Date expiryTime = Date.from(Instant.parse(expiryClaim));
//	            Date nowPlusFiveMinutes = Date.from(Instant.now().plus(refTime, ChronoUnit.MINUTES));
////	            Date nowPlusFiveMinutes = Date.from(dbtime.plus(5, ChronoUnit.MINUTES));
////	            System.out.println("sir er refresh "+ nowPlusFiveMinutes1);
////	            System.out.println("subham er refresh "+ nowPlusFiveMinutes);
////	            if (expiryTime.before(nowPlusFiveMinutes) && ip.equals(claims.get("role", String.class))) {
//	            if (expiryTime.before(nowPlusFiveMinutes) ) {
//	                String uid = claims.get("sub", String.class);
//	                String slno = claims.get("unique_name", String.class);
//	                String role = claims.get("role", String.class);
//
//	                String newToken= generateToken(uid, slno, role, secretKey);
//	                return newToken;
//	            }
//
//	        } catch (JwtException e) {
//	            e.printStackTrace();
//	            return null;
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	            return null;
//	        }
//			return null;
//	        
//	        
//	    }
	 
	 
//	 public  String[] decodeDeptCd(String token) {
//		 try {
////			 System.out.println("tok="+secretKey);
//			 String secretKey2="your_fixed_secret_key_here_jkenfjenfjefbjefbkejfbed";
//	            Claims claims = validateToken(token, secretKey2);
//	            if (claims != null) {
//	                String deptCd = claims.get("unique_name", String.class);
//	                String userId = claims.get("sub", String.class);
//	                return new String[]{userId, deptCd};
//	                
//	            } else {
//	                return null;
//	            }
//		 }catch(Exception e) {
//			 e.printStackTrace();
//			 return null;
//		 }
	
	 
	 public String decodeJwt(String token) {
		    try {
		        String[] parts = token.split("\\.");
		        if (parts.length != 3) {
		            throw new IllegalArgumentException("JWT does not have 3 parts");
		        }
		        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
		        ObjectMapper mapper = new ObjectMapper();
		        @SuppressWarnings("unchecked")
				Map<String, Object> claims = mapper.readValue(payload, Map.class);
		        ////System.out.println("heress5");
		        if (claims.get("sub") != null) {
		        	////System.out.println("heres6");
		            return (String) claims.get("sub");
		        } else {
		            return null;
		        }
		    } catch (Exception e) {
		        ////System.out.println("Token decoding failed: " + e.getMessage());
		        return null;
		    }
}
	 
	 public String getSecretKey() {
		    return secretKey;
		}
	 
	 public String extractUserId(String token) {
		    try {
		        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
		        return Jwts.parserBuilder()
		                .setSigningKey(key)
		                .build()
		                .parseClaimsJws(token)
		                .getBody()
		                .getSubject();
		    } catch (Exception e) {
		        return null;
		    }
		}


	 }
