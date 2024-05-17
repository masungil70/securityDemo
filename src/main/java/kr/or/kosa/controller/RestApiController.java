package kr.or.kosa.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.kosa.config.auth.PrincipalDetails;
import kr.or.kosa.model.Users;
import kr.or.kosa.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000") // CORS 허용
public class RestApiController {

	private final UserRepository userRepository;
	private final PasswordEncoder bCryptPasswordEncoder;

	// 모든 사람이 접근 가능
	@GetMapping("home")
	public String home() {
		return "<h1>home</h1>";
	}

	// Tip : JWT를 사용하면 UserDetailsService를 호출하지 않기 때문에 @AuthenticationPrincipal 사용 불가능.
	// 왜냐하면 @AuthenticationPrincipal은 UserDetailsService에서 리턴될 때 만들어지기 때문이다.
	// public String user(@AuthenticationPrincipal UserPrincipal userPrincipal) { 
	// 위코드는 아래와 같이 사용해야함 

	// 유저 혹은 매니저 혹은 어드민이 접근 가능
	@GetMapping("user")
	public String user(Authentication authentication) {
		PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("principal : " + principal.getUser().getId());
		System.out.println("principal : " + principal.getUser().getUsername());
		System.out.println("principal : " + principal.getUser().getPassword());

		return "<h1>user</h1>";
	}

	// 매니저 혹은 어드민이 접근 가능
	@GetMapping("manager/reports")
	public String reports() {
		return "<h1>reports</h1>";
	}

	// 어드민이 접근 가능
	@GetMapping("admin/users")
	public List<Users> users() {
		return userRepository.findAll();
	}

	@PostMapping("join")
	public String join(@RequestBody Users user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setRoles("ROLE_USER");
		userRepository.save(user);
		return "회원가입완료";
	}

}

/*
 * 
 테스팅 하는 방법 
 
모든 사람이 접근 가능
curl -v http://localhost:8090/api/v1/home

회원가입완료
curl -v -X POST  -H "content-type: application/json" -d "{\"id\":10,\"username\":\"masungil\",\"password\":\"1234\"}" http://localhost:8090/api/v1/join

로그인
curl -v -X POST  -d "{\"username\":\"masungil\",\"password\":\"1234\"}" http://localhost:8090/login

유저 혹은 매니저 혹은 어드민이 접근 가능
curl -v -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MTAsInVzZXJuYW1lIjoibWFzdW5naWwiLCJleHAiOjE3MTU5MTAzMTR9.DtQKTN4KsqhHQBybcUHFj8YyeSIJKE3yIuZg6G5rEco" http://localhost:8090/api/v1/user
*/
