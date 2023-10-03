package kr.or.kosa.config.jwt;

import java.io.IOException;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.kosa.config.auth.PrincipalDetails;
import kr.or.kosa.model.Users;
import kr.or.kosa.repository.UserRepository;

// 인가 필터 
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

	final private UserRepository userRepository;

	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		//인증 정보를 해더에서 읽는다
		String header = request.getHeader(JwtProperties.HEADER_STRING);

		//인증 정보에 해더가 존재하면 요청인 URL로 이동하여 실행한다
		if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
			chain.doFilter(request, response);
			return;
		}
		//인증 토큰 해더 정보를 출력한다 
		System.out.println("header : " + header);
		//인증에 관련된 토큰 정보만 추출한다 
		String token = request.getHeader(JwtProperties.HEADER_STRING)
				.replace(JwtProperties.TOKEN_PREFIX, "");

		// 토큰 검증 (이게 인증이기 때문에 AuthenticationManager도 필요 없음)
		//서버의 인증 키를 생성한다
		SecretKey key = Keys.hmacShaKeyFor(JwtProperties.getSecretKey());
		//인증 토큰 문자열을 이용하여 클래임 객체를 얻는다
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
		
		String username = String.valueOf(claims.get("username"));

		if (username != null) {
			//사용자이름을 이용하여 실제 사용자의 정보를 얻는다
			Users user = userRepository.findByUsername(username);
			
			// 인증은 토큰 검증시 끝. 인증을 하기 위해서가 아닌 스프링 시큐리티가 수행해주는 권한 처리를 위해
			// 아래와 같이 사용자 객체를 이용하여 Authentication 객체를 만들어 
			// 스프링 시큐리티 관리자에 등록한다 
			PrincipalDetails principalDetails = new PrincipalDetails(user);
			Authentication authentication = new UsernamePasswordAuthenticationToken(
					principalDetails, // 나중에 컨트롤러에서 DI해서 쓸 때 사용하기 편함.
					null, // 토큰 인증시 패스워드는 알수 없어 null 값을 전달하는 것임  
					principalDetails.getAuthorities()); //사용자가 소유한 역할 권한을 전달한다 

			// 강제로 시큐리티의 세션에 접근하여 값 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		chain.doFilter(request, response);
	}

}
