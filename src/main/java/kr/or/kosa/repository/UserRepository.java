package kr.or.kosa.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import kr.or.kosa.model.Users;

//DB연동을 위해 사용되는 클래스를 Map으로 모델링한 것임  
//@Component
@Mapper
public interface UserRepository  {
//	private Map<String, Users> usersMap = new HashMap<>();
	
//	//발급된 토큰 문자열에서 username을 얻고 해당 
//	//username을 이용해서 사용자 객체를 얻을 때 아래 함수 사용함  
//	public Users findByUsername(String username) {
//		return usersMap.get(username);
//	}
//	
//	//회원 가입시 사용되는 함수 
//	public Users save(Users users) {
//		usersMap.put(users.getUsername(), users);
//		return users;
//	}
//	
//	//어드민이 사용자 목록을 조회하기 위해 사용하는 함수
//	public List<Users> findAll() {
//		return new ArrayList<>(usersMap.values());
//	}
	
	Users findByUsername(String username);
	//회원 가입시 사용되는 함수 
	public Users save(Users users);
	
	//어드민이 사용자 목록을 조회하기 위해 사용하는 함수  
	List<Users> findAll();
	
}
