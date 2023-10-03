package kr.or.kosa.config.jwt;

import java.util.Base64;

public interface JwtProperties {
	String SECRET = "서버에기록된비밀번호"; // 서버만 알고 있는 비밀값
	int EXPIRATION_TIME = 2 * 60 * 1000; //1분짜리 토큰,  10일 (1/1000초)
	String TOKEN_PREFIX = "Bearer ";
	String HEADER_STRING = "Authorization";
	
	public static byte[] getSecretKey() {
		try {
			return Base64.getEncoder().encode(SECRET.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
