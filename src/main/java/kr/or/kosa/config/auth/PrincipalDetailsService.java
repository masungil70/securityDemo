package kr.or.kosa.config.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import kr.or.kosa.model.Users;
import kr.or.kosa.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("PrincipalDetailsService : 진입");
		Users user = userRepository.findByUsername(username);

		//세션을 사용할때는 아래 코드 사용하여 추가함 
		// session.setAttribute("loginUser", user);
		return new PrincipalDetails(user);
	}
}
